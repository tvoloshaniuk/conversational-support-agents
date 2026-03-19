package com.pl.ink;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Support System...");
        SupportOrchestrator orchestrator = new SupportOrchestrator();
        Scanner scanner = new Scanner(System.in);

        System.out.println("System Ready. Type your message (or 'exit' to quit).");

        while (true) {
            System.out.print("\n👤 You: ");
            String input = scanner.nextLine();

            if ("exit".equalsIgnoreCase(input.trim())) {
                break;
            }

            orchestrator.handleUserMessage(input);
        }
        scanner.close();
    }
}