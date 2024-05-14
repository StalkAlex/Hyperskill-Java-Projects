package webCalendarSpring.entity;


import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String event;

    @Column(nullable = false)
    private LocalDate date;

    // getters and setters
    public Integer getId() {
        return id;
    }

    public Event setId(Integer id) {
        this.id = id;

        return this;
    }

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
}
