package com.weather.sensor_service.Controller;

import com.weather.sensor_service.Entity.SensorReading;
import com.weather.sensor_service.Services.SensorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/sensors")
public class SensorController {

    private final SensorService service;

    public SensorController(SensorService service) {
        this.service = service;
    }

    // Create a new sensor record
    @PostMapping("/create-reading")
    public SensorReading createReading(@RequestBody SensorReading reading) {
        reading.setTimestamp(LocalDateTime.now());
        return service.saveReading(reading);
    }
}
