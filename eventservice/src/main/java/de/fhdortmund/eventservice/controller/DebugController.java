package de.fhdortmund.eventservice.controller;

import de.fhdortmund.eventservice.model.Event;
import de.fhdortmund.eventservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class DebugController {

    private final EventRepository eventRepository;

    @GetMapping("/debug/events")
    public ResponseEntity<Map<String, Object>> getEventDebugInfo() {
        List<Event> events = eventRepository.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("totalEvents", events.size());

        // Erstelle eine Liste aller Event-IDs
        List<String> eventIds = events.stream()
                .map(event -> event.getId().toString())
                .collect(Collectors.toList());
        response.put("eventIds", eventIds);

        // Informationen zu jedem Event
        List<Map<String, Object>> eventInfos = events.stream()
                .map(event -> {
                    Map<String, Object> eventInfo = new HashMap<>();
                    eventInfo.put("id", event.getId().toString());
                    eventInfo.put("title", event.getTitle());
                    eventInfo.put("category", event.getCategory());
                    eventInfo.put("status", event.getStatus());
                    return eventInfo;
                })
                .collect(Collectors.toList());
        response.put("events", eventInfos);

        // Beispiel-URL zum Testen
        if (!eventIds.isEmpty()) {
            response.put("sampleUrl", "/api/events/" + eventIds.get(0));
        }

        return ResponseEntity.ok(response);
    }
}
