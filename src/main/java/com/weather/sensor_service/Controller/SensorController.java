package com.weather.sensor_service.Controller;

import com.weather.sensor_service.Entity.SensorReading;
import com.weather.sensor_service.Services.SensorService;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.locks.ReentrantLock;


import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/sensors")
public class SensorController {

    private final SensorService service;

    // Used for creating records to ensure database integrity
    private final ReentrantLock lock = new ReentrantLock();


    public SensorController(SensorService service) {
        this.service = service;
    }

    // Create a new sensor record
    //Lock used to ensure data integrity
    @PostMapping("/create-reading")
    public SensorReading createReading(@RequestBody SensorReading reading) {
        lock.lock();
        try {
            reading.setTimestamp(LocalDateTime.now());
            return service.saveReading(reading);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save reading.");
        } finally {
            lock.unlock();
        }
    }

    // Retrieves ALL sensor records
    @GetMapping("/get-all")
    public List<SensorReading> getReading() {
        return  service.getAllReadings();
    }

    // Retrieves all readings for a particular sensor Id
    @GetMapping("/get-reading")
    public List<SensorReading> getReading(@RequestParam("sensorId") Long sensorId) {
        return  service.getAllReadingsForaSensorId(sensorId);
    }
}
