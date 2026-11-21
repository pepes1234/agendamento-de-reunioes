package br.edu.agendamento.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class Environment {
    private static final Map<String, String> FILE_VALUES = loadDotEnv();

    private Environment() {
    }

    public static String get(String name) {
        String systemValue = System.getenv(name);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }

        String fileValue = FILE_VALUES.get(name);
        return fileValue == null ? "" : fileValue.trim();
    }

    public static String get(String name, String defaultValue) {
        String value = get(name);
        return value.isBlank() ? defaultValue : value;
    }

    public static int getInt(String name, int defaultValue) {
        try {
            return Integer.parseInt(get(name));
        } catch (NumberFormatException exception) {
            return defaultValue;
        }
    }

    public static boolean getBoolean(String name, boolean defaultValue) {
        String value = get(name);
        if (value.isBlank()) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(value) || "1".equals(value);
    }

    private static Map<String, String> loadDotEnv() {
        Map<String, String> values = new HashMap<>();

        Path currentDirectory = Paths.get("").toAbsolutePath();
        Path envFile = currentDirectory.resolve(".env");
        if (!Files.isRegularFile(envFile)) {
            envFile = currentDirectory.resolve("..").resolve(".env").normalize();
        }

        if (!Files.isRegularFile(envFile)) {
            return values;
        }

        try {
            for (String line : Files.readAllLines(envFile, StandardCharsets.UTF_8)) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                int separator = trimmed.indexOf('=');
                if (separator <= 0) {
                    continue;
                }

                String name = trimmed.substring(0, separator).trim();
                String value = trimmed.substring(separator + 1).trim();
                if ((value.startsWith("\"") && value.endsWith("\""))
                        || (value.startsWith("'") && value.endsWith("'"))) {
                    value = value.substring(1, value.length() - 1);
                }
                values.put(name, value);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Nao foi possivel ler o arquivo .env", exception);
        }

        return values;
    }
}
