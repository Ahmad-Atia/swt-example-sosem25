package de.fhdortmund.eventservice.dto;

import de.fhdortmund.eventservice.model.EventCategory;
import de.fhdortmund.eventservice.model.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventSearchCriteria {
    private String keyword;
    private EventCategory category;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocationDto location;
    private Double radius;
    private UUID organizerId;
    private EventStatus status;
    private Boolean onlyAvailable;
    private Integer page;
    private Integer size;
}
