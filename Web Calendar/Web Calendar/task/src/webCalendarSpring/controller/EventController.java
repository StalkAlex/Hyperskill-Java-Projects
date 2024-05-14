package webCalendarSpring.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import webCalendarSpring.dto.Event;
import webCalendarSpring.dto.ResponseEvent;
import webCalendarSpring.dto.AddedEventMessage;
import webCalendarSpring.dto.NotFoundEventMessage;
import webCalendarSpring.service.EventService;

import java.time.LocalDate;
import java.util.List;

@RestController
public class EventController {
    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/event")
    public ResponseEntity<AddedEventMessage> create(@Valid @RequestBody Event event) {
        eventService.store(event);

        return ResponseEntity.ok(
                new AddedEventMessage(event.getEvent(), event.getDate())
        );
    }

    @GetMapping("/event")
    public ResponseEntity<List<ResponseEvent>> getAll(
            @RequestParam(name = "start_time", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "end_time", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<ResponseEvent> list;

        if (startDate != null && endDate != null) {
            list = eventService.getEventsForDateRange(startDate, endDate);
        } else {
            list = eventService.getAllEvents();
        }

        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(list);
    }


    @GetMapping("/event/today")
    public ResponseEntity<List<ResponseEvent>> getEventToday() {
        return ResponseEntity.ok(eventService.getTodayEvents());
    }

    @GetMapping("/event/{id}")
    public ResponseEntity<Object> getEventById(@PathVariable("id") Long id) {
        return eventService.getEventById(id)
            .map(event -> new ResponseEntity<Object>(event, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(
                    new NotFoundEventMessage(), HttpStatus.NOT_FOUND)
            );
    }

    @DeleteMapping("/event/{id}")
    public ResponseEntity<Object> deleteEventById(@PathVariable("id") Long id) {
        return eventService.deleteEventById(id)
                .map(event -> new ResponseEntity<Object>(event, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(
                        new NotFoundEventMessage(), HttpStatus.NOT_FOUND)
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
    }
}
