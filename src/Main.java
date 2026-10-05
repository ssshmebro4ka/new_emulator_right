import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static String vfsPath;
    private static String scriptPath;
    private static boolean debugMode = false;

    public static void main(String[] args) {
        if (!parseArgs(args)) {
            return;
        }

        if (debugMode) {
            printDebugInfo();
        }

        if (scriptPath != null) {
            runScript(scriptPath);
            return;
        }

        runRepl();
    }

    private static boolean parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--vfs":
                    if (i + 1 >= args.length) {
                        System.out.println("Ошибка: не указан путь к VFS после --vfs");
                        return false;
                    }
                    vfsPath = args[++i];
                    break;
                case "--script":
                    if (i + 1 >= args.length) {
                        System.out.println("Ошибка: не указан путь к скрипту после --script");
                        return false;
                    }
                    scriptPath = args[++i];
                    break;
                case "--debug":
                    debugMode = true;
                    break;
                default:
                    System.out.println("Ошибка: неизвестный параметр '" + args[i] + "'");
                    return false;
            }
        }
        return true;
    }

    private static void printDebugInfo() {
        System.out.println("=== Отладочный вывод параметров ===");
        System.out.println("VFS path:    " + (vfsPath != null ? vfsPath : "(не задан)"));
        System.out.println("Script path: " + (scriptPath != null ? scriptPath : "(не задан)"));
        System.out.println("===================================");
    }

    private static void runRepl() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(getPrompt());
            if (!scanner.hasNextLine()) break;
            String input = scanner.nextLine();
            if (input.trim().isEmpty()) continue;

            if (!handleInput(input)) {
                scanner.close();
                System.out.println("Выход из эмулятора");
                return;
            }
        }
        scanner.close();
    }

    private static void runScript(String path) {
        Path script = Paths.get(path);
        if (!Files.exists(script)) {
            System.out.println("Ошибка: стартовый скрипт не найден: " + path);
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(script)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                    continue;
                }
                System.out.println(getPrompt() + line);
                String result = executeCommand(line.trim());
                if (result == null) {
                    System.out.println("Ошибка: строка скрипта пропущена: " + line);
                    continue;
                }
                if (result.equals("EXIT")) {
                    System.out.println("Выход из эмулятора");
                    return;
                }
                System.out.println(result);
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения стартового скрипта: " + e.getMessage());
        }
    }

    private static boolean handleInput(String input) {
        String result = executeCommand(input);
        if (result == null) {
            System.out.println("Ошибка: команда не распознана: " + input);
            return true;
        }
        if (result.equals("EXIT")) {
            return false;
        }
        System.out.println(result);
        return true;
    }

    private static String executeCommand(String input) {
        String[] parts = tokenize(input);
        if (parts.length == 0) return null;
        String command = parts[0];
        String[] arguments = Arrays.copyOfRange(parts, 1, parts.length);

        switch (command) {
            case "exit":
                return arguments.length > 0 ? errorExit() : "EXIT";
            case "ls":
                return printStub(command, arguments);
            case "cd":
                return arguments.length > 1
                        ? "Ошибка: команда cd принимает не более одного аргумента"
                        : printStub(command, arguments);
            default:
                return "Ошибка: команда '" + command + "' не найдена";
        }
    }

    private static String errorExit() {
        return "Ошибка: команда exit не принимает аргументов";
    }

    private static String printStub(String command, String[] arguments) {
        return "Выполнена команда-заглушка: " + command + "\n"
                + "Аргументы: " + Arrays.toString(arguments);
    }

    private static String getPrompt() {
        String user = System.getProperty("user.name");
        String host = "localhost";
        try {
            host = java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception ignored) {
        }
        return user + "@" + host + ":~$ ";
    }

    private static String[] tokenize(String input) {
        String[] rawTokens = input.trim().split("\\s+");
        String[] result = new String[rawTokens.length];
        for (int i = 0; i < rawTokens.length; i++) {
            result[i] = expandVariables(rawTokens[i]);
        }
        return result;
    }

    private static String expandVariables(String input) {
        Pattern pattern = Pattern.compile("\\$([A-Za-z_][A-Za-z0-9_]*)");
        Matcher matcher = pattern.matcher(input);
        StringBuilder sb = new StringBuilder();
        int last = 0;
        while (matcher.find()) {
            sb.append(input, last, matcher.start());
            String varName = matcher.group(1);
            String varValue = System.getenv(varName);
            sb.append(varValue != null ? varValue : matcher.group(0));
            last = matcher.end();
        }
        sb.append(input, last, input.length());
        return sb.toString();
    }
}