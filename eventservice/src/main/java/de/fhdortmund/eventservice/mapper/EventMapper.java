package de.fhdortmund.eventservice.mapper;

import de.fhdortmund.eventservice.dto.*;
import de.fhdortmund.eventservice.model.Event;
import de.fhdortmund.eventservice.model.Location;
import de.fhdortmund.eventservice.model.Participant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "remainingSpots", expression = "java(event.getRemainingSpots())")
    @Mapping(target = "canJoin", expression = "java(event.canJoin())")
    EventDto eventToDto(Event event);

    @Mapping(target = "displayAddress", expression = "java(location.getDisplayAddress())")
    LocationDto locationToDto(Location location);

    @Mapping(target = "isActive", expression = "java(participant.isActive())")
    @Mapping(target = "isModerator", expression = "java(participant.isModerator())")
    ParticipantDto participantToDto(Participant participant);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currentParticipants", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Event createDtoToEvent(CreateEventDto createEventDto);

    Location locationDtoToLocation(LocationDto locationDto);
}
