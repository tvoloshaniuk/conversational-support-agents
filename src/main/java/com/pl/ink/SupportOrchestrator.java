package com.pl.ink;

import com.google.genai.Chat;
import com.google.genai.Client;
import com.google.genai.types.*;
import java.util.List;
import java.util.Map;

/**
 * The Orchestrator: Main AI Controller.
 * Routes user messages dynamically between Agent A (Technical RAG) and Agent B (Billing Tool Calling).
 * Maintains conversation history.
 */
public class SupportOrchestrator {
    private final Client client;
    private final Chat chatSession;
    private final String cachedTechContext;
    private final BillingService billingService; // <--- AGENT B

    public SupportOrchestrator() {
        this.client = new Client();

        // Load documents ONCE (Agent A)
        TechDocService techDocService = new TechDocService();
        this.cachedTechContext = techDocService.getAllDocsContext();

        // Initialize Agent B
        this.billingService = new BillingService();

        String systemText = """
            You are a Support Orchestrator. Route requests:
            1. TECH MODE: Use the provided context to answer technical questions. DO NOT hallucinate.
            2. BILLING MODE: Use tool-calling to get plans or process refunds.
            """;

        Content systemContent = Content.builder()
                .parts(List.of(Part.builder().text(systemText).build()))
                .build();

        GenerateContentConfig config = GenerateContentConfig.builder()
                .systemInstruction(systemContent)
                .tools(List.of(billingService.getBillingTools())) // <--- Injecting Agent B tools
                .build();

        this.chatSession = client.chats.create("gemini-2.5-flash", config);
    }

    public void handleUserMessage(String userText) {
        String augmentedPrompt = "CONTEXT:\n" + cachedTechContext + "\n\nUSER: " + userText;
        GenerateContentResponse response = chatSession.sendMessage(augmentedPrompt);

        if (response.functionCalls() != null && !response.functionCalls().isEmpty()) {
            handleFunctionCall(response.functionCalls().get(0));
        } else {
            System.out.println("🤖 Agent A (Tech): " + response.text());
        }
    }

    private void handleFunctionCall(FunctionCall call) {
        // Delegate execution to Agent B
        String dbResult = billingService.executeAction(call);
        System.out.println("⚙️ [SYSTEM LOG] Agent B (Billing) returned: " + dbResult);

        FunctionResponse functionResponse = FunctionResponse.builder()
                .name(call.name().orElse(""))
                .response(Map.of("database_result", dbResult))
                .build();

        Content responseContent = Content.builder()
                .role("user")
                .parts(List.of(Part.builder().functionResponse(functionResponse).build()))
                .build();

        GenerateContentResponse finalLlmResponse = chatSession.sendMessage(responseContent);

        if (finalLlmResponse.functionCalls() != null && !finalLlmResponse.functionCalls().isEmpty()) {
            System.out.println("🤖 Agent B (Billing): Action completed.");
        } else {
            System.out.println("🤖 Agent B (Billing): " + finalLlmResponse.text());
        }
    }
}