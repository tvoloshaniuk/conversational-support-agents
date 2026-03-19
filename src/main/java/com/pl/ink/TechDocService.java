package com.pl.ink;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Agent A: Technical Specialist.
 * Returns all technical documents from docs folder as a single context string.
 */
public class TechDocService {
    private final String docsPath = "src/main/resources/docs";

    /**
     * Reads all technical documents and combines them into a single context string.
     * In a real project, you should be used semantic search here.
     */
    public String getAllDocsContext() {
        try (Stream<Path> paths = Files.walk(Paths.get(docsPath))) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .map(path -> {
                        try {
                            return Files.readString(path);
                        } catch (IOException e) {
                            return "";
                        }
                    })
                    .collect(Collectors.joining("\n\n---\n\n"));
        } catch (IOException e) {
            return "No technical documentation found.";
        }
    }
}
