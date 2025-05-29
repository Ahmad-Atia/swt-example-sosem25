package de.fhdortmund.eventservice.service;

import de.fhdortmund.eventservice.dto.CreateEventDto;
import de.fhdortmund.eventservice.dto.UpdateEventDto;
import de.fhdortmund.eventservice.model.Event;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ValidationService {

    /**
     * Validiert ein neues Event
     * @param eventDto Das zu validierende Event-DTO
     * @throws IllegalArgumentException wenn die Validierung fehlschlägt
     */
    public void validateNewEvent(CreateEventDto eventDto) {
        if (eventDto.getStartDateTime().isAfter(eventDto.getEndDateTime())) {
            throw new IllegalArgumentException("Startdatum muss vor dem Enddatum liegen");
        }

        if (eventDto.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Startdatum kann nicht in der Vergangenheit liegen");
        }

        validateLocation(eventDto.getLocation().getLatitude(), eventDto.getLocation().getLongitude());
    }

    /**
     * Validiert ein zu aktualisierendes Event
     * @param eventDto Das zu validierende Event-DTO
     * @param existingEvent Das bestehende Event
     * @throws IllegalArgumentException wenn die Validierung fehlschlägt
     */
    public void validateEventUpdate(UpdateEventDto eventDto, Event existingEvent) {
        // Prüfe Start- und Enddatum nur, wenn beide angegeben wurden
        if (eventDto.getStartDateTime() != null && eventDto.getEndDateTime() != null) {
            if (eventDto.getStartDateTime().isAfter(eventDto.getEndDateTime())) {
                throw new IllegalArgumentException("Startdatum muss vor dem Enddatum liegen");
            }
        } else if (eventDto.getStartDateTime() != null) {
            // Nur Startdatum wurde aktualisiert
            if (eventDto.getStartDateTime().isAfter(existingEvent.getEndDateTime())) {
                throw new IllegalArgumentException("Startdatum muss vor dem Enddatum liegen");
            }
        } else if (eventDto.getEndDateTime() != null) {
            // Nur Enddatum wurde aktualisiert
            LocalDateTime startDate = existingEvent.getStartDateTime();
            if (startDate.isAfter(eventDto.getEndDateTime())) {
                throw new IllegalArgumentException("Startdatum muss vor dem Enddatum liegen");
            }
        }

        // Prüfe Location, falls angegeben
        if (eventDto.getLocation() != null &&
            eventDto.getLocation().getLatitude() != null &&
            eventDto.getLocation().getLongitude() != null) {
            validateLocation(eventDto.getLocation().getLatitude(), eventDto.getLocation().getLongitude());
        }

        // Prüfe, ob Event bereits begonnen hat
        if (existingEvent.getStartDateTime().isBefore(LocalDateTime.now())) {
            if (eventDto.getStartDateTime() != null) {
                throw new IllegalArgumentException("Startdatum kann für ein bereits begonnenes Event nicht geändert werden");
            }
        }
    }

    /**
     * Validiert Koordinaten
     * @param latitude Breitengrad
     * @param longitude Längengrad
     * @throws IllegalArgumentException wenn die Koordinaten ungültig sind
     */
    private void validateLocation(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Latitude und Longitude müssen angegeben werden");
        }

        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude muss zwischen -90 und 90 liegen");
        }

        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude muss zwischen -180 und 180 liegen");
        }
    }
}
