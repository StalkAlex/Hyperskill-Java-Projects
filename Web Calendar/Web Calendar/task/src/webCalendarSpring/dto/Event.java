package webCalendarSpring.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class Event {
    @NotBlank
    private String event;
    @NotNull
    private LocalDate date;

    public String getEvent() {
        return event;
    }

    public Event setEvent(String event) {
        this.event = event;

        return this;
    }

    public LocalDate getDate() {
        return date;
    }

    public Event setDate(LocalDate date) {
        this.date = date;

        return this;
    }

    public static webCalendarSpring.entity.Event fromDto(Event eventDto) {
        return new webCalendarSpring.entity.Event()
                .setDate(eventDto.getDate())
                .setEvent(eventDto.getEvent());
    }
}
