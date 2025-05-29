package de.fhdortmund.eventservice.dto;

import de.fhdortmund.eventservice.model.EventCategory;
import de.fhdortmund.eventservice.model.EventStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventDto {
    private String title;
    private String description;

    @Future(message = "Startdatum muss in der Zukunft liegen")
    private LocalDateTime startDateTime;

    @Future(message = "Enddatum muss in der Zukunft liegen")
    private LocalDateTime endDateTime;

    @Valid
    private LocationDto location;

    @Min(value = 1, message = "Maximale Teilnehmeranzahl muss mindestens 1 sein")
    private Integer maxParticipants;

    private EventCategory category;
    private EventStatus status;
}
