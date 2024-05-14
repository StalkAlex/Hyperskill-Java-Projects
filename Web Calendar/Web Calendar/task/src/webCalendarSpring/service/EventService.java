package webCalendarSpring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webCalendarSpring.dto.Event;
import webCalendarSpring.dto.ResponseEvent;
import webCalendarSpring.repository.EventRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }


    public List<ResponseEvent> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map((event) -> new ResponseEvent(event.getId(), event.getEvent(), event.getDate()))
                .toList();

    }

    public List<ResponseEvent> getTodayEvents() {
        return eventRepository.findByDate(LocalDate.now())
                .stream()
                .map((event) -> new ResponseEvent(event.getId(), event.getEvent(), event.getDate()))
                .toList();
    }

    public List<ResponseEvent> getEventsForDateRange(LocalDate startDate, LocalDate endDate) {
        return eventRepository.findByDateBetween(startDate, endDate)
                .stream()
                .map((event) -> new ResponseEvent(event.getId(), event.getEvent(), event.getDate()))
                .toList();
    }

    public Optional<ResponseEvent> getEventById(Long id) {
        return eventRepository.findById(id.intValue()).map(
                event -> new ResponseEvent(event.getId(), event.getEvent(), event.getDate())
        );
    }

    public Optional<ResponseEvent> deleteEventById(Long id) {
        var event = eventRepository.findById(id.intValue());

        event.ifPresent(eventRepository::delete);

        return event.map(e -> new ResponseEvent(e.getId(), e.getEvent(), e.getDate()));
    }

    public void store(Event event) {
        eventRepository.saveAndFlush(Event.fromDto(event));
    }
}
