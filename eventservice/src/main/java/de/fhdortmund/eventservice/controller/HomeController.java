package de.fhdortmund.eventservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "Event Management API");
        response.put("version", "1.0.0");
        response.put("description", "REST API für die Verwaltung von Events und Teilnehmern");
        response.put("endpoints", new String[]{
                "/api/events",
                "/api/events/{eventId}",
                "/api/events/search",
                "/api/events/{eventId}/participants"
        });

        return ResponseEntity.ok(response);
    }
}
