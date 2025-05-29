package de.fhdortmund.eventservice.service;

import de.fhdortmund.eventservice.dto.CreateEventDto;
import de.fhdortmund.eventservice.dto.EventSearchCriteria;
import de.fhdortmund.eventservice.dto.UpdateEventDto;
import de.fhdortmund.eventservice.mapper.EventMapper;
import de.fhdortmund.eventservice.model.Event;
import de.fhdortmund.eventservice.model.EventStatus;
import de.fhdortmund.eventservice.model.Location;
import de.fhdortmund.eventservice.repository.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ParticipantService participantService;
    private final ValidationService validationService;
    private final EventMapper eventMapper;

    // In a real-world application, we would inject a NotificationService here
    // private final NotificationService notificationService;

    /**
     * Erstellt ein neues Event
     * @param createEventDto Die Event-Daten
     * @return Das erstellte Event
     */
    @Transactional
    public Event createEvent(CreateEventDto createEventDto) {
        validationService.validateNewEvent(createEventDto);

        Event event = eventMapper.createDtoToEvent(createEventDto);
        event.setCurrentParticipants(0);
        event.setStatus(EventStatus.DRAFT);
        event.setCreatedAt(LocalDateTime.now());

        Event savedEvent = eventRepository.save(event);

        // Füge den Organisator automatisch als Teilnehmer hinzu
        participantService.addParticipant(savedEvent.getId(), savedEvent.getOrganizerId());

        // Sende eine Benachrichtigung (in einer realen Anwendung)
        // notificationService.sendEventCreatedNotification(savedEvent);

        return savedEvent;
    }

    /**
     * Findet ein Event anhand seiner ID
     * @param id Die Event-ID
     * @return Das gefundene Event oder Optional.empty()
     */
    public Optional<Event> findById(UUID id) {
        return eventRepository.findById(id);
    }

    /**
     * Gibt alle Events zurück
     * @return Liste aller Events
     */
    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    /**
     * Sucht Events anhand verschiedener Kriterien
     * @param criteria Die Suchkriterien
     * @return Eine Seite mit Events
     */
    public Page<Event> searchEvents(EventSearchCriteria criteria) {
        int page = criteria.getPage() != null ? criteria.getPage() : 0;
        int size = criteria.getSize() != null ? criteria.getSize() : 20;
        PageRequest pageRequest = PageRequest.of(page, size);

        List<Event> results = new ArrayList<>();

        // Implementierung der komplexen Suchlogik
        // In einer realen Anwendung würde man hier Specification oder QueryDSL verwenden
        // Für dieses Beispiel führen wir einfache Filterungen durch

        if (criteria.getCategory() != null) {
            results.addAll(eventRepository.findByCategory(criteria.getCategory()));
        } else if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
            results.addAll(eventRepository.findByDateRange(criteria.getStartDate(), criteria.getEndDate()));
        } else if (criteria.getOrganizerId() != null) {
            results.addAll(eventRepository.findByOrganizerId(criteria.getOrganizerId()));
        } else if (criteria.getLocation() != null && criteria.getRadius() != null) {
            Location location = new Location();
            location.setLatitude(criteria.getLocation().getLatitude());
            location.setLongitude(criteria.getLocation().getLongitude());
            results.addAll(eventRepository.searchByLocation(location, criteria.getRadius()));
        } else {
            results.addAll(eventRepository.findAll());
        }

        // Weitere Filterungen
        if (criteria.getStatus() != null) {
            results = results.stream()
                    .filter(event -> event.getStatus() == criteria.getStatus())
                    .collect(Collectors.toList());
        }

        if (Boolean.TRUE.equals(criteria.getOnlyAvailable())) {
            results = results.stream()
                    .filter(event -> event.canJoin())
                    .collect(Collectors.toList());
        }

        if (criteria.getKeyword() != null && !criteria.getKeyword().isBlank()) {
            String keyword = criteria.getKeyword().toLowerCase();
            results = results.stream()
                    .filter(event ->
                        (event.getTitle() != null && event.getTitle().toLowerCase().contains(keyword)) ||
                        (event.getDescription() != null && event.getDescription().toLowerCase().contains(keyword)) ||
                        (event.getLocation() != null && event.getLocation().getCity() != null &&
                         event.getLocation().getCity().toLowerCase().contains(keyword))
                    )
                    .collect(Collectors.toList());
        }

        // Paginierung
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), results.size());

        if (start > results.size()) {
            return new PageImpl<>(List.of(), pageRequest, results.size());
        }

        return new PageImpl<>(results.subList(start, end), pageRequest, results.size());
    }

    /**
     * Lässt einen Benutzer an einem Event teilnehmen
     * @param eventId Event-ID
     * @param userId Benutzer-ID
     */
    @Transactional
    public void joinEvent(UUID eventId, UUID userId) {
        participantService.addParticipant(eventId, userId);
    }

    /**
     * Entfernt einen Benutzer von einem Event
     * @param eventId Event-ID
     * @param userId Benutzer-ID
     */
    @Transactional
    public void leaveEvent(UUID eventId, UUID userId) {
        participantService.removeParticipant(eventId, userId);
    }

    /**
     * Aktualisiert ein Event
     * @param eventId Das zu aktualisierende Event
     * @param updateEventDto Die neuen Event-Daten
     * @return Das aktualisierte Event
     */
    @Transactional
    public Event updateEvent(UUID eventId, UpdateEventDto updateEventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event nicht gefunden"));

        validationService.validateEventUpdate(updateEventDto, event);

        // Aktualisiere die Felder, falls sie im DTO vorhanden sind
        if (updateEventDto.getTitle() != null) {
            event.setTitle(updateEventDto.getTitle());
        }

        if (updateEventDto.getDescription() != null) {
            event.setDescription(updateEventDto.getDescription());
        }

        if (updateEventDto.getStartDateTime() != null) {
            event.setStartDateTime(updateEventDto.getStartDateTime());
        }

        if (updateEventDto.getEndDateTime() != null) {
            event.setEndDateTime(updateEventDto.getEndDateTime());
        }

        if (updateEventDto.getLocation() != null) {
            Location location = event.getLocation();
            if (location == null) {
                location = new Location();
            }

            if (updateEventDto.getLocation().getLatitude() != null) {
                location.setLatitude(updateEventDto.getLocation().getLatitude());
            }

            if (updateEventDto.getLocation().getLongitude() != null) {
                location.setLongitude(updateEventDto.getLocation().getLongitude());
            }

            if (updateEventDto.getLocation().getAddress() != null) {
                location.setAddress(updateEventDto.getLocation().getAddress());
            }

            if (updateEventDto.getLocation().getCity() != null) {
                location.setCity(updateEventDto.getLocation().getCity());
            }

            if (updateEventDto.getLocation().getCountry() != null) {
                location.setCountry(updateEventDto.getLocation().getCountry());
            }

            if (updateEventDto.getLocation().getPostalCode() != null) {
                location.setPostalCode(updateEventDto.getLocation().getPostalCode());
            }

            event.setLocation(location);
        }

        if (updateEventDto.getMaxParticipants() != null) {
            // Prüfe, ob die neue maximale Teilnehmerzahl kleiner ist als die aktuelle Teilnehmerzahl
            if (updateEventDto.getMaxParticipants() < event.getCurrentParticipants()) {
                throw new IllegalArgumentException("Die maximale Teilnehmerzahl kann nicht kleiner sein als die aktuelle Teilnehmerzahl");
            }
            event.setMaxParticipants(updateEventDto.getMaxParticipants());
        }

        if (updateEventDto.getCategory() != null) {
            event.setCategory(updateEventDto.getCategory());
        }

        if (updateEventDto.getStatus() != null) {
            event.setStatus(updateEventDto.getStatus());
        }

        return eventRepository.save(event);
    }

    /**
     * Löscht ein Event
     * @param id Die Event-ID
     */
    @Transactional
    public void deleteEvent(UUID id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event nicht gefunden"));

        // Prüfe, ob das Event bereits begonnen hat
        if (event.getStartDateTime().isBefore(LocalDateTime.now()) && event.getStatus() == EventStatus.ONGOING) {
            throw new IllegalStateException("Ein laufendes Event kann nicht gelöscht werden");
        }

        // Setze den Status auf CANCELLED, anstatt das Event zu löschen
        // Dies ist oft besser als ein echter Löschvorgang, um Referenzen zu erhalten
        event.setStatus(EventStatus.CANCELLED);
        eventRepository.save(event);

        // Sende eine Benachrichtigung (in einer realen Anwendung)
        // notificationService.sendEventCancelledNotification(event);
    }
}
