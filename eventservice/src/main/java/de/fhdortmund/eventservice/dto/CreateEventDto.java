package de.fhdortmund.eventservice.dto;

import de.fhdortmund.eventservice.model.EventCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventDto {
    @NotBlank(message = "Titel ist erforderlich")
    private String title;

    private String description;

    @NotNull(message = "Startdatum ist erforderlich")
    @Future(message = "Startdatum muss in der Zukunft liegen")
    private LocalDateTime startDateTime;

    @NotNull(message = "Enddatum ist erforderlich")
    @Future(message = "Enddatum muss in der Zukunft liegen")
    private LocalDateTime endDateTime;

    @Valid
    @NotNull(message = "Ort ist erforderlich")
    private LocationDto location;

    @NotNull(message = "Organisator-ID ist erforderlich")
    private UUID organizerId;

    @Min(value = 1, message = "Maximale Teilnehmeranzahl muss mindestens 1 sein")
    private Integer maxParticipants;

    @NotNull(message = "Kategorie ist erforderlich")
    private EventCategory category;
}
