import java.util.Scanner;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
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

    private static boolean handleInput(String input) {
        String[] parts = tokenize(input);
        String command = parts[0];
        String[] arguments = Arrays.copyOfRange(parts, 1, parts.length);

        switch (command) {
            case "exit":
                return handleExit(arguments);
            case "ls":
                printStub(command, arguments);
                return true;
            case "cd":
                return handleCd(command, arguments);
            default:
                System.out.println("Ошибка: команда '" + command + "' не найдена");
                return true;
        }
    }

    private static boolean handleExit(String[] arguments) {
        if (arguments.length > 0) {
            System.out.println("Ошибка: команда exit не принимает аргументов");
            return true;
        }
        return false;
    }

    private static boolean handleCd(String command, String[] arguments) {
        if (arguments.length > 1) {
            System.out.println("Ошибка: команда cd принимает не более одного аргумента");
            return true;
        }
        printStub(command, arguments);
        return true;
    }

    private static void printStub(String command, String[] arguments) {
        System.out.println("Выполнена команда-заглушка: " + command);
        System.out.println("Аргументы: " + Arrays.toString(arguments));
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