package webCalendarSpring.dto;

import java.time.LocalDate;

public record AddedEventMessage(String event, LocalDate date) {
    public static final String RESPONSE_MESSAGE_ADDED = "The event has been added!";

    public String getMessage() {
        return RESPONSE_MESSAGE_ADDED;
    }
}
