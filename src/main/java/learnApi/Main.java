package learnApi;

import com.google.genai.Chat;
import com.google.genai.Client;
import com.google.genai.types.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
//        sampleOne();
//        sampleTwo();
//        sampleTwo_2();
//        sampleThree();
//        sampleFour();
//        ...
//        sampleFive();
//        sampleSix();
//        sampleSeven_justMimeType();
//        sampleEight();
//        sampleNine_arrayInsideJson_ObjectBase();
//        sampleTen_arrayAsJasonBase_WithDevsAmountBorders();

//        task5_tooling_sample1();
        task6_forAgemtA();
    }

    private static void task6_forAgemtA() {
        Client client = new Client();

        // 1. Схема для getBillingPlan (один аргумент)
        Schema userIdOnlySchema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(Map.of(
                        "userId", Schema.builder().type(Type.Known.STRING).description("ID користувача").build()
                ))
                .required(List.of("userId"))
                .build();

        // 2. Схема для refundRequest (ДВА обов'язкових аргументи)
        Schema refundSchema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(Map.of(
                        "userId", Schema.builder().type(Type.Known.STRING).description("ID користувача").build(),
                        "amount", Schema.builder().type(Type.Known.INTEGER).description("Сума повернення у доларах").build()
                ))
                .required(List.of("userId", "amount")) // <--- Обидва поля обов'язкові
                .build();

        // 3. Декларації функцій
        FunctionDeclaration getPlan = FunctionDeclaration.builder()
                .name("getBillingPlan")
                .description("Get the current billing plan for a specific user")
                .parameters(userIdOnlySchema)
                .build();

        FunctionDeclaration doRefund = FunctionDeclaration.builder()
                .name("refundRequest")
                .description("Create a refund request for a specific amount for a user")
                .parameters(refundSchema)
                .build();

        // 4. Пакуємо в Tool
        Tool billingTools = Tool.builder()
                .functionDeclarations(List.of(getPlan, doRefund))
                .build();

        // 5. Конфіг
        GenerateContentConfig config = GenerateContentConfig.builder()
                .tools(List.of(billingTools))
                .build();

        // 6. Запит на повернення коштів
        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash",
                "User 777 wants a refund of 50 dollars",
                config
        );

        // Вивід результату
        System.out.println("Function Calls: " + response.functionCalls());
    }

    private static void task5_tooling_sample1() {
        Client client = new Client();

        // 1. Описуємо аргументи функції через Schema
        Schema locationSchema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(Map.of(
                        "city", Schema.builder().type(Type.Known.STRING).description("Назва міста").build()
                ))
                .required(List.of("city"))
                .build();

        // 2. Декларуємо саму функцію
        FunctionDeclaration weatherFunc = FunctionDeclaration.builder()
                .name("getWeather") // <-------------------- Назва для LLM
                .description("Отримати поточну погоду для вказаного міста")
                .parameters(locationSchema) // <-------------------- Схема аргументів
                .build();

        // 3. Пакуємо в Tool
        Tool myTools = Tool.builder()
                .functionDeclarations(List.of(weatherFunc))
                .build();

        // 4. Додаємо в конфіг (responseMimeType тут не потрібен!)
        GenerateContentConfig config = GenerateContentConfig.builder()
                .tools(List.of(myTools)) // <--------------------
                .build();

        // 5. Запит
        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash",
                "Яка зараз погода в Лондоні?",
                config
        );

        System.out.println("Text: " + response.text());
        System.out.println("Function Calls: " + response.functionCalls());
    }


    private static void sampleTen_arrayAsJasonBase_WithDevsAmountBorders() {
        Client client = new Client();
        Schema arrayBaseSchemaPiece = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(Map.of(
                        "name", Schema.builder().type(Type.Known.STRING).build(),
                        "surname", Schema.builder().type(Type.Known.STRING).build(),
                        "age", Schema.builder().type(Type.Known.INTEGER).build()
                ))
                .required(List.of("name", "surname", "age"))
                .build();

        Schema arrayBaseSchema = Schema.builder()
                .type(Type.Known.ARRAY)
                .items(arrayBaseSchemaPiece)
                .minItems(2L)
                .maxItems(5L)
                .build();


        GenerateContentConfig config = GenerateContentConfig.builder()
                .responseMimeType("application/json")
                .responseSchema(arrayBaseSchema)
                .build();

        GenerateContentResponse response = client.models.generateContent("gemini-2.5-flash", "generate a " +
                "senior developer profile", config);
        System.out.println(response.text());

    }

    /*developer with array skills. in sum: name, age, skills*/
    /*result:
    `{"name":"Jane Doe","age":40,"skills":["JavaScript","Python","React","Node.js","AWS","System Design","Mentorship"]}`
    */
    private static void sampleNine_arrayInsideJson_ObjectBase() {
        Client client = new Client();
        Schema schema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(Map.of(
                        "name", Schema.builder().type(Type.Known.STRING).build(),
                        "age", Schema.builder().type(Type.Known.INTEGER).build(),
                        "skills", Schema.builder()
                                .type(Type.Known.ARRAY)
                                .items(Schema.builder()
                                        .type(Type.Known.STRING).build())
                                .build()
                ))
                .required(List.of("name", "age", "skills"))
                .build();

        GenerateContentConfig config = GenerateContentConfig.builder()
                .responseMimeType("application/json")
                .responseSchema(schema)
                .build();


        GenerateContentResponse response = client.models.generateContent("gemini-2.5-flash", "generate a " +
                "senior developer profile", config);
        System.out.println(response.text());
    }

    /* First Schema usage */
    private static void sampleEight() {
        Client client = new Client();

        // 1. Схема: тільки одне текстове поле "word"
        Schema oneWordSchema = Schema.builder()
                .type(Type.Known.OBJECT) // <--------------------
                .properties(Map.of(
                        "word", Schema.builder().type(Type.Known.STRING).build() // <--------------------
                ))
                .required(List.of("word"))
                .build();

        // 2. Конфіг
        GenerateContentConfig config = GenerateContentConfig.builder()
                .responseMimeType("application/json")
                .responseSchema(oneWordSchema)
                .build();

        // 3. Запит
        String json = client.models.generateContent(
                "gemini-2.5-flash",
                "Напиши назву будь-якого кольору.",
                config
        ).text();

        System.out.println(json);
    }

    /* without Scheme */
    private static void sampleSeven_justMimeType() {
        Client client = new Client();

//        // 1. Схема: тільки одне текстове поле "word"
//        Schema oneWordSchema = Schema.builder()
//                .type(Type.Known.OBJECT) // <--------------------
//                .properties(Map.of(
//                        "word", Schema.builder().type(Type.Known.STRING).build() // <--------------------
//                ))
//                .required(List.of("word"))
//                .build();

        // 2. Конфіг
        GenerateContentConfig config = GenerateContentConfig.builder()
                .responseMimeType("application/json")
                .build();

        // 3. Запит
        String json = client.models.generateContent(
                "gemini-2.5-flash",
                "Напиши назву будь-якого кольору.",
                config
        ).text();
        System.out.println(json);
//        vs
        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash",
                "Напиши назву будь-якого кольору.",
                config);
        System.out.println(response.text());
    }

    /* add Scheme and constrain by .required for at leasr "word" */
    private static void sampleSix() {
        Client client = new Client();

        // 1. Схема: тільки одне текстове поле "word"
        Schema oneWordSchema = Schema.builder()
                .type(Type.Known.OBJECT) // <--------------------
                .properties(Map.of(
                        "word", Schema.builder().type(Type.Known.STRING).build() // <--------------------
                ))
                .required(List.of("word"))
                .build();

        // 2. Конфіг
        GenerateContentConfig config = GenerateContentConfig.builder()
                .responseMimeType("application/json")
                .responseSchema(oneWordSchema)
                .build();

        // 3. Запит
        String json = client.models.generateContent(
                "gemini-2.5-flash",
                "Напиши назву будь-якого кольору.",
                config
        ).text();

        System.out.println(json);
    }


    private static void sampleFive() {
        Client client = new Client();

        GenerateContentConfig config = GenerateContentConfig.builder().temperature(0.1f).build();

        GenerateContentResponse response =
                client.models.generateContent("gemini-3-flash-preview", "Explain how AI works", config);

        System.out.println(response.text());
    }

    private static void sampleFour() {

    }


    private static void sampleThree() {
        Client client = new Client();

        Content content =
                Content.fromParts(
                        Part.fromText("Explain how AI works in a few words")
                );

        GenerateContentConfig config = GenerateContentConfig.builder()
                .systemInstruction(content) // <--------------------
                .build();

        Chat chat = client.chats.create("gemini-2.5-flash", config); // <--------------------

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String msg = sc.nextLine();
            if ("exit".equals(msg)) break;

            System.out.println(chat.sendMessage(msg).text());
        }
    }

    private static void sampleTwo_2() {
        // The client gets the API key from the environment variable `GEMINI_API_KEY`.
        Client client = new Client();

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-3-flash-preview",
                        "Explain how AI works in a few words",
                        null);

//        response.parts().

        System.out.println(response.text());
    }

    private static void sampleTwo() {
        // The client gets the API key from the environment variable `GEMINI_API_KEY`.
        Client client = new Client();

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-3-flash-preview",
                        "Explain how AI works in a few words",
                        null);

        System.out.println(response.text());
    }

    private static void sampleOne() {
        Client client = new Client();

        Content userContent = Content.fromParts(Part.fromText("Hello"));
        Content modelContent =
                Content.builder()
                        .role("model")
                        .parts(
                                Collections.singletonList(
                                        Part.fromText("Great to meet you. What would you like to know?")
                                )
                        ).build();

        Chat chat = client.chats.create(
                "gemini-2.0-flash",
                GenerateContentConfig.builder()
                        .systemInstruction(userContent)
                        .systemInstruction(modelContent)
                        .build()
        );

        GenerateContentResponse response1 = chat.sendMessage("I have 2 dogs in my house.");
        System.out.println(response1.text());

        GenerateContentResponse response2 = chat.sendMessage("How many paws are in my house?");
        System.out.println(response2.text());
    }
}