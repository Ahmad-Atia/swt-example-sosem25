package de.fhdortmund.eventservice.controller;

import de.fhdortmund.eventservice.dto.*;
import de.fhdortmund.eventservice.mapper.EventMapper;
import de.fhdortmund.eventservice.model.Event;
import de.fhdortmund.eventservice.model.Participant;
import de.fhdortmund.eventservice.service.EventService;
import de.fhdortmund.eventservice.service.ParticipantService;
import de.fhdortmund.eventservice.service.ValidationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final ParticipantService participantService;
    private final ValidationService validationService;
    private final EventMapper eventMapper;

    // In a real-world application, we would also inject an AuthService

    /**
     * Gibt alle Events zurück
     * @return Liste aller Events
     */
    @GetMapping
    public ResponseEntity<List<EventDto>> getAllEvents() {
        List<Event> events = eventService.findAll();
        List<EventDto> eventDtos = events.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(eventDtos);
    }

    /**
     * Erstellt ein neues Event
     * @param eventData Die Event-Daten
     * @return Das erstellte Event
     */
    @PostMapping
    public ResponseEntity<EventDto> createEvent(@Valid @RequestBody CreateEventDto eventData) {
        try {
            Event event = eventService.createEvent(eventData);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(event));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gibt ein Event anhand seiner ID zurück
     * @param eventId Die Event-ID
     * @return Das gefundene Event
     */
    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEvent(@PathVariable UUID eventId) {
        try {
            return eventService.findById(eventId)
                    .map(event -> ResponseEntity.ok(convertToDto(event)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("Fehler beim Abrufen des Events mit ID " + eventId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * Aktualisiert ein Event
     * @param eventId Die Event-ID
     * @param data Die neuen Event-Daten
     * @return Das aktualisierte Event
     */
    @PutMapping("/{eventId}")
    public ResponseEntity<EventDto> updateEvent(
            @PathVariable UUID eventId,
            @Valid @RequestBody UpdateEventDto data) {
        try {
            Event updatedEvent = eventService.updateEvent(eventId, data);
            return ResponseEntity.ok(convertToDto(updatedEvent));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Löscht ein Event
     * @param eventId Die Event-ID
     * @return HTTP-Statuscode
     */
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID eventId) {
        try {
            eventService.deleteEvent(eventId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Sucht Events anhand verschiedener Kriterien
     * @param criteria Die Suchkriterien
     * @return Eine Seite mit Events
     */
    @GetMapping("/search")
    public ResponseEntity<Page<EventDto>> searchEvents(EventSearchCriteria criteria) {
        Page<Event> events = eventService.searchEvents(criteria);
        Page<EventDto> eventDtos = events.map(this::convertToDto);
        return ResponseEntity.ok(eventDtos);
    }

    /**
     * Lässt einen Benutzer an einem Event teilnehmen
     * @param eventId Die Event-ID
     * @param userId Die Benutzer-ID
     * @return HTTP-Statuscode
     */
    @PostMapping("/{eventId}/participants/{userId}")
    public ResponseEntity<Void> joinEvent(
            @PathVariable UUID eventId,
            @PathVariable UUID userId) {
        try {
            eventService.joinEvent(eventId, userId);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Entfernt einen Benutzer von einem Event
     * @param eventId Die Event-ID
     * @param userId Die Benutzer-ID
     * @return HTTP-Statuscode
     */
    @DeleteMapping("/{eventId}/participants/{userId}")
    public ResponseEntity<Void> leaveEvent(
            @PathVariable UUID eventId,
            @PathVariable UUID userId) {
        try {
            eventService.leaveEvent(eventId, userId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gibt alle Teilnehmer eines Events zurück
     * @param eventId Die Event-ID
     * @return Liste der Teilnehmer
     */
    @GetMapping("/{eventId}/participants")
    public ResponseEntity<List<ParticipantDto>> getParticipants(@PathVariable UUID eventId) {
        try {
            List<Participant> participants = participantService.getParticipants(eventId);
            List<ParticipantDto> participantDtos = participants.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(participantDtos);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Konvertiert ein Event-Objekt in ein EventDto
     * @param event Das zu konvertierende Event
     * @return Das EventDto
     */
    private EventDto convertToDto(Event event) {
        return eventMapper.eventToDto(event);
    }

    /**
     * Konvertiert ein Participant-Objekt in ein ParticipantDto
     * @param participant Der zu konvertierende Teilnehmer
     * @return Das ParticipantDto
     */
    private ParticipantDto convertToDto(Participant participant) {
        return eventMapper.participantToDto(participant);
    }
}
