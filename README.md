# Conversational AI Support Agents


## Architecture
* **Orchestrator (`SupportOrchestrator.java`):** The main controller. It analyzes user intent, routes queries, manages the `Chat` session state, and handles API interactions.
* **Agent A - Technical Specialist (`TechDocService.java`):** Implements Retrieval-Augmented Generation (RAG). It reads local `.txt` files from `src/main/resources/docs` and strictly answers technical questions based *only* on that context, preventing hallucinations.
* **Agent B - Billing Specialist (`BillingService.java`):** Uses LLM Tool Calling (Function Calling). It declares capabilities (check plan, open refund, get history) via schemas and maps LLM intentions to mock backend Java methods.

## Prerequisites
* **Java 21** or higher
* **Maven** (for dependency management)
* A valid **Google Gemini API Key**

## How to Run the Project

1. **Set your Gemini API key as an environment variable.** The application securely reads the key from the environment. Do not hardcode it.
    * **Windows (Command Prompt):** `set GEMINI_API_KEY="your_api_key_here"`
    * **Windows (PowerShell):** `$env:GEMINI_API_KEY="your_api_key_here"`
    * **Linux/Mac:** `export GEMINI_API_KEY="your_api_key_here"`

2. **Build and Run the application:**
    * **Via IDE (Recommended):** Open the project in IntelliJ IDEA / Eclipse, let Maven download the dependencies, and run the `Main.java` class located in `src/main/java/com/pl/ink/Main.java`.
    * **Via Terminal:** Navigate to the project root and run:
      ```bash
      mvn clean compile exec:java -Dexec.mainClass="com.pl.ink.Main"
      ```

3. **Interact with the Agents:** The console will display a prompt. Type your queries and watch the system route them. Type `exit` to terminate the session.

---

## Example Conversations (Demonstrating Agent Switching & Context)

Here is a demonstration of the system successfully routing requests, executing tools, maintaining context across multiple turns, and handling out-of-scope questions gracefully:

```text
Starting Support System...
System Ready. Type your message (or 'exit' to quit).

👤 You: Hi, my integration is failing with a 401 error. What should I do?
🤖 Agent A (Tech): Your integration is failing with a 401 Unauthorized error because your API token has likely expired. Tokens expire every 90 days. Please generate a new token in the Developer Portal under Settings -> API Keys.

👤 You: Okay, thanks. By the way, what is the current billing plan for user 404?
⚙️ [SYSTEM LOG] Agent B (Billing) returned: User 404 is on the Premium $29/mo plan.
🤖 Agent B (Billing): User 404 is on the Premium $29/mo plan.

👤 You: Can you show me the payment history for that same user?
⚙️ [SYSTEM LOG] Agent B (Billing) returned: User 404 paid $29 on March 1st and $29 on Feb 1st.
🤖 Agent B (Billing): User 404 paid $29 on March 1st and $29 on Feb 1st.

👤 You: Got it. Switching topics, how do I start the Webhook Listener?
🤖 Agent A (Tech): To start the Webhook Listener, execute `webhook-cli start --port 8080`. Make sure that port 8080 is open in your firewall rules.

👤 You: The listener didn't work and I'm frustrated. I want to open a refund request for 50 dollars for user 404.
⚙️ [SYSTEM LOG] Agent B (Billing) returned: Refund ticket #10294 created for user 404 for $50.

👤 You: Can you tell me the weather in London?
🤖 Agent A (Tech): I am sorry, I can only answer questions about API usage, setup, troubleshooting, or help with billing and refunds. I cannot provide weather information.