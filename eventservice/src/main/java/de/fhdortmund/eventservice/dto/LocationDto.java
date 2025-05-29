package de.fhdortmund.eventservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    private Double latitude;
    private Double longitude;
    private String address;
    private String city;
    private String country;
    private String postalCode;
    private String displayAddress;
}
