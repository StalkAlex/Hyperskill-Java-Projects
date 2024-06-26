package calculator;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String line = scanner.nextLine().trim();

            if (line.isEmpty()) { // Current line is empty, skip it
                continue;
            }

            if (line.startsWith("/")) { // Is a command
                if (line.equals("/exit")) {
                    break;
                }
                else if (line.equals("/help")) {
                    System.out.println("The program calculates [+ - * /]");
                } else {
                    System.out.println("Unknown command");
                }
            } else if(line.contains("=")) { // Is an Assignment
                Memory.validateAssignment(line);
            } else { // Is an ArithmeticExpression
                try {
                    String result = ArithmeticExpression.evaluate(line);
                    System.out.println(result);
                } catch (UnsupportedOperationException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        System.out.println("Bye!");
    }
}
