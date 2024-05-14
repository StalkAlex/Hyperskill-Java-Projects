package webCalendarSpring.dto;

import java.time.LocalDate;

public record ResponseEvent(int id, String event, LocalDate date) {
}
