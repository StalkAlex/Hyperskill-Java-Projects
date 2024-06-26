package calculator;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.regex.Pattern;

public class ArithmeticExpression {
    private static final Set<Character> OPERATORS = Set.of('+', '-', '*', '/');

    private static final Pattern WHITE_SPACES = Pattern.compile("\\s+");
    private static final Pattern PLUS_MINUS = Pattern.compile("-\\s*\\+|\\+\\s*-");
    private static final Pattern DOUBLE_MINUS = Pattern.compile("(\\s*-\\s*-)+");
    private static final Pattern DOUBLE_PLUS = Pattern.compile("(\\s*\\+)+");

    public static String evaluate(String expression) throws UnsupportedOperationException {
        char[] tokens = reduceExpression(expression).toCharArray();

        Deque<BigInteger> numbers = new ArrayDeque<>();
        Deque<Character> operators = new ArrayDeque<>();

        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i] == ' ') { // Current token is a whitespace, skip it
                continue;
            }

            if (tokens[i] >= '0' && tokens[i] <= '9') { // Current token is a number, push it to numbers
                StringBuilder builder = new StringBuilder();
                builder.append(tokens[i]);
                // There may be more than one digits in number
                while (i < tokens.length - 1 && tokens[i + 1] >= '0' && tokens[i + 1] <= '9')
                    builder.append(tokens[++i]);
                numbers.push(new BigInteger(builder.toString()));
            } else if (tokens[i] >= 'A' && tokens[i] <= 'z') { // Current token is a variable, push it to numbers
                StringBuilder builder = new StringBuilder();
                builder.append(tokens[i]);
                // There may be more than one letter
                while (i < tokens.length - 1 && tokens[i + 1] >= 'A' && tokens[i + 1] <= 'z')
                    builder.append(tokens[++i]);
                String variable = builder.toString();
                String value = Memory.getVariable(variable);
                if (value == null) {
                    throw new UnsupportedOperationException("Unknown variable");
                }
                numbers.push(new BigInteger(value));
            } else if (tokens[i] == '(') { // Current token is an opening brace, push it to operators
                operators.push(tokens[i]);
            } else if (tokens[i] == ')') { // Current token is an  Closing brace, solve entire brace
                while (!operators.isEmpty() && operators.peek() != '(') {
                    numbers.push(applyOperation(operators.pop(), numbers));
                }
                if (operators.isEmpty()) {
                    throw new UnsupportedOperationException("Invalid expression");
                }
                operators.pop();
            } else if (OPERATORS.contains(tokens[i])) {
                // While top of 'operators' has same or greater precedence to current token
                // Apply operator on top of 'operators' to top two elements in numbers stack
                while (!operators.isEmpty() && hasPrecedence(tokens[i], operators.peek())) {
                    numbers.push(applyOperation(operators.pop(), numbers));
                }
                // Push current token to 'operators'.
                operators.push(tokens[i]);
            }
        }
        // Entire expression has been parsed at this point
        // Apply remaining operations to remaining numbers
        while (!operators.isEmpty()) {
            numbers.push(applyOperation(operators.pop(), numbers));
        }
        // Top of 'values' contains result
        if (numbers.size() != 1) {
            throw new UnsupportedOperationException("Invalid expression");
        }
        return numbers.pop().toString();
    }

    private static BigInteger applyOperation(char operator, Deque<BigInteger> numbers) {
        if (numbers.size() == 1) {
            return applyUnaryOperation(operator, numbers.pop());
        } else {
            return applyBinaryOperation(operator, numbers.pop(), numbers.pop());
        }
    }

    private static BigInteger applyBinaryOperation(char operator, BigInteger b, BigInteger a) {
        switch (operator) {
            case '+':
                return a.add(b);
            case '-':
                return a.subtract(b);
            case '*':
                return a.multiply(b);
            case '/':
                if (b.equals(BigInteger.ZERO)) {
                    throw new UnsupportedOperationException("Cannot divide by zero");
                }
                return a.divide(b);
        }
        throw new UnsupportedOperationException("Invalid expression");
    }

    private static BigInteger applyUnaryOperation(char operator, BigInteger a) {
        switch (operator) {
            case '+':
                return a;
            case '-':
                return a.negate();
        }
        throw new UnsupportedOperationException("Invalid expression");
    }

    // Returns true if 'op2' has higher or same precedence as 'op1'
    public static boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        } else {
            return !((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-'));
        }
    }

    private static String reduceExpression(String expression) {
        String newExpression = expression;

        newExpression = WHITE_SPACES.matcher(newExpression).replaceAll(" ");
        newExpression = DOUBLE_MINUS.matcher(newExpression).replaceAll("+");
        newExpression = PLUS_MINUS.matcher(newExpression).replaceAll("-");
        newExpression = DOUBLE_PLUS.matcher(newExpression).replaceAll("+");

        return newExpression;
    }
}
