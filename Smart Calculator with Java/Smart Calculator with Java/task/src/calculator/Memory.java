package calculator;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Memory {
    private static final Pattern IDENTIFIER = Pattern.compile("[a-zA-Z]+");
    private static final Pattern INTEGER = Pattern.compile("[+-]?\\d+");

    private static final Map<String, String> VARIABLES = new HashMap<>();

    public static String getVariable(String key) {
        return VARIABLES.get(key);
    }

    public static void validateAssignment(String line) {
        String[] keyValue = line.split("\\s*=\\s*");
        String key = keyValue[0];
        String value = keyValue[1];

        if (!isValidIdentifier(key)) {
            System.out.println("Invalid identifier");
        } else if (isInteger(value)) {
            VARIABLES.put(key, value);
        } else if (isValidIdentifier(value)) {
            if (VARIABLES.containsKey(value)) {
                VARIABLES.put(key, VARIABLES.get(value));
            } else {
                System.out.println("Unknown variable");
            }
        } else {
            System.out.println("Invalid assignment");
        }
    }

    private static boolean isValidIdentifier(String line) {
        return IDENTIFIER.matcher(line).matches();
    }

    private static boolean isInteger(String line) {
        return INTEGER.matcher(line).matches();
    }
}
