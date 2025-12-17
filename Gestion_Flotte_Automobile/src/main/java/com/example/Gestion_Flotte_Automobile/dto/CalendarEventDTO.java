package com.example.Gestion_Flotte_Automobile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarEventDTO {
    private String title;
    private String start;
    private String end;
    private String color; // "blue", "red", "orange"
    private boolean allDay;
    private String type; // "PERSONAL", "RETURN", "MAINTENANCE"
    private String description;
}
