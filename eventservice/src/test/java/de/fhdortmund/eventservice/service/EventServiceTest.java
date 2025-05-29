package de.fhdortmund.eventservice.service;

import de.fhdortmund.eventservice.dto.CreateEventDto;
import de.fhdortmund.eventservice.dto.LocationDto;
import de.fhdortmund.eventservice.dto.UpdateEventDto;
import de.fhdortmund.eventservice.mapper.EventMapper;
import de.fhdortmund.eventservice.model.Event;
import de.fhdortmund.eventservice.model.EventCategory;
import de.fhdortmund.eventservice.model.EventStatus;
import de.fhdortmund.eventservice.repository.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantService participantService;

    @Mock
    private ValidationService validationService;

    @Spy
    private EventMapper eventMapper = EventMapper.INSTANCE;

    @InjectMocks
    private EventService eventService;

    private CreateEventDto createEventDto;
    private Event event;
    private UUID eventId;
    private UUID organizerId;

    @BeforeEach
    void setUp() {
        // Vorbereitung der Testdaten
        eventId = UUID.randomUUID();
        organizerId = UUID.randomUUID();

        // CreateEventDto erstellen
        createEventDto = new CreateEventDto();
        createEventDto.setTitle("Test Event");
        createEventDto.setDescription("Test Description");
        createEventDto.setStartDateTime(LocalDateTime.now().plusDays(1));
        createEventDto.setEndDateTime(LocalDateTime.now().plusDays(2));

        LocationDto locationDto = new LocationDto();
        locationDto.setLatitude(51.5074);
        locationDto.setLongitude(-0.1278);
        locationDto.setAddress("Test Address");
        locationDto.setCity("Test City");
        locationDto.setCountry("Test Country");
        locationDto.setPostalCode("12345");
        createEventDto.setLocation(locationDto);

        createEventDto.setOrganizerId(organizerId);
        createEventDto.setMaxParticipants(100);
        createEventDto.setCategory(EventCategory.SOCIAL);

        // Event erstellen
        event = new Event();
        event.setId(eventId);
        event.setTitle("Test Event");
        event.setDescription("Test Description");
        event.setStartDateTime(LocalDateTime.now().plusDays(1));
        event.setEndDateTime(LocalDateTime.now().plusDays(2));
        event.setOrganizerId(organizerId);
        event.setMaxParticipants(100);
        event.setCurrentParticipants(0);
        event.setCategory(EventCategory.SOCIAL);
        event.setStatus(EventStatus.DRAFT);
        event.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createEvent_ShouldCreateEventSuccessfully() {
        // Arrange
        doNothing().when(validationService).validateNewEvent(any(CreateEventDto.class));
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        doNothing().when(participantService).addParticipant(any(UUID.class), any(UUID.class));

        // Act
        Event result = eventService.createEvent(createEventDto);

        // Assert
        assertNotNull(result);
        assertEquals("Test Event", result.getTitle());
        assertEquals(EventStatus.DRAFT, result.getStatus());
        assertEquals(EventCategory.SOCIAL, result.getCategory());

        // Verify
        verify(validationService, times(1)).validateNewEvent(any(CreateEventDto.class));
        verify(eventRepository, times(1)).save(any(Event.class));
        verify(participantService, times(1)).addParticipant(any(UUID.class), any(UUID.class));
    }

    @Test
    void findById_ShouldReturnEvent_WhenEventExists() {
        // Arrange
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Act
        Optional<Event> result = eventService.findById(eventId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(eventId, result.get().getId());

        // Verify
        verify(eventRepository, times(1)).findById(eventId);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenEventDoesNotExist() {
        // Arrange
        when(eventRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act
        Optional<Event> result = eventService.findById(UUID.randomUUID());

        // Assert
        assertTrue(result.isEmpty());

        // Verify
        verify(eventRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void updateEvent_ShouldUpdateEventSuccessfully() {
        // Arrange
        UpdateEventDto updateEventDto = new UpdateEventDto();
        updateEventDto.setTitle("Updated Title");

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        doNothing().when(validationService).validateEventUpdate(any(UpdateEventDto.class), any(Event.class));

        // Act
        Event result = eventService.updateEvent(eventId, updateEventDto);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());

        // Verify
        verify(eventRepository, times(1)).findById(eventId);
        verify(validationService, times(1)).validateEventUpdate(any(UpdateEventDto.class), any(Event.class));
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void updateEvent_ShouldThrowException_WhenEventDoesNotExist() {
        // Arrange
        UpdateEventDto updateEventDto = new UpdateEventDto();
        updateEventDto.setTitle("Updated Title");

        when(eventRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> eventService.updateEvent(UUID.randomUUID(), updateEventDto));

        // Verify
        verify(eventRepository, times(1)).findById(any(UUID.class));
        verify(validationService, never()).validateEventUpdate(any(UpdateEventDto.class), any(Event.class));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void deleteEvent_ShouldSetStatusToCancelled() {
        // Arrange
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        // Act
        eventService.deleteEvent(eventId);

        // Assert
        assertEquals(EventStatus.CANCELLED, event.getStatus());

        // Verify
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void deleteEvent_ShouldThrowException_WhenEventDoesNotExist() {
        // Arrange
        when(eventRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> eventService.deleteEvent(UUID.randomUUID()));

        // Verify
        verify(eventRepository, times(1)).findById(any(UUID.class));
        verify(eventRepository, never()).save(any(Event.class));
    }
}
