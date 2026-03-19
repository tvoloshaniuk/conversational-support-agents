package com.pl.ink;

import com.google.genai.types.*;
import java.util.List;
import java.util.Map;

/**
 * Agent B: Billing Specialist.
 * Handles tool declarations and mock backend logic for billing operations.
 */
public class BillingService {

    public Tool getBillingTools() {
        FunctionDeclaration getPlan = FunctionDeclaration.builder()
                .name("getBillingPlan")
                .description("Check customer's billing plan and pricing")
                .parameters(Schema.builder().type(Type.Known.OBJECT)
                        .properties(Map.of("userId", Schema.builder().type(Type.Known.STRING).build()))
                        .required(List.of("userId")).build()).build();

        FunctionDeclaration openRefund = FunctionDeclaration.builder()
                .name("openRefundRequest")
                .description("Open a refund request for a customer")
                .parameters(Schema.builder().type(Type.Known.OBJECT)
                        .properties(Map.of(
                                "userId", Schema.builder().type(Type.Known.STRING).build(),
                                "amount", Schema.builder().type(Type.Known.INTEGER).description("Amount in dollars").build()))
                        .required(List.of("userId", "amount")).build()).build();

        FunctionDeclaration getHistory = FunctionDeclaration.builder()
                .name("getBillingHistory")
                .description("Provide billing and payment history for a customer")
                .parameters(Schema.builder().type(Type.Known.OBJECT)
                        .properties(Map.of("userId", Schema.builder().type(Type.Known.STRING).build()))
                        .required(List.of("userId")).build()).build();

        return Tool.builder().functionDeclarations(List.of(getPlan, openRefund, getHistory)).build();
    }

    /** MOCK BACKEND EXECUTION */
    public String executeAction(FunctionCall call) {
        String funcName = call.name().orElse("");
        Map<String, Object> args = call.args().orElse(Map.of());

        if ("getBillingPlan".equals(funcName)) {
            return "User " + args.get("userId") + " is on the Premium $29/mo plan.";
        } else if ("openRefundRequest".equals(funcName)) {
            return "Refund ticket #10294 created for user " + args.get("userId") + " for $" + args.get("amount") + ".";
        } else if ("getBillingHistory".equals(funcName)) {
            return "User " + args.get("userId") + " paid $29 on March 1st and $29 on Feb 1st.";
        }
        return "Error: Unknown action.";
    }
}