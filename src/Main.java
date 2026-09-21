import java.util.Scanner;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String prompt = getPrompt();
            System.out.print(prompt);

            String input = scanner.nextLine();

            if (input.trim().isEmpty()) {
                continue;
            }

            String expandedInput = expandVariables(input);

            String[] parts = expandedInput.trim().split("\\s+");
            String command = parts[0];
            String[] arguments = Arrays.copyOfRange(parts, 1, parts.length);

            switch (command) {
                case "exit":
                    System.out.println("Выход из эмулятора");
                    scanner.close();
                    return;

                case "ls":
                case "cd":
                    System.out.println("Выполнена команда-заглушка: " + command);
                    System.out.println("Аргументы: " + Arrays.toString(arguments));
                    break;

                default:
                    System.err.println("Ошибка: команда '" + command + "' не найдена");
                    break;
            }
        }
    }

    private static String getPrompt() {
        String user = System.getProperty("user.name");
        String host = "localhost";

        try {
            host = java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
        }

        return user + "@" + host + ":~$ ";
    }

    private static String expandVariables(String input) {
        StringBuilder result = new StringBuilder();
        String[] tokens = input.split(" ");

        for (String token : tokens) {
            if (token.startsWith("$")) {
                String varName = token.substring(1);
                String varValue = System.getenv(varName);

                if (varValue != null) {
                    result.append(varValue);
                } else {
                    result.append(token);
                }
            } else {
                result.append(token);
            }
            result.append(" ");
        }

        return result.toString().trim();
    }
}