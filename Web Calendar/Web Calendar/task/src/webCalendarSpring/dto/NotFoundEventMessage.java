package webCalendarSpring.dto;

import java.time.LocalDate;

public record NotFoundEventMessage() {
    public static final String RESPONSE_MESSAGE_NOT_EXISTS = "The event doesn't exist!";

    public String getMessage() {
        return RESPONSE_MESSAGE_NOT_EXISTS;
    }
}
