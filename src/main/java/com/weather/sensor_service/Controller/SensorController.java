package com.weather.sensor_service.Controller;

import com.weather.sensor_service.DTO.SensorAggregationResponseDTO;
import com.weather.sensor_service.Entity.SensorReading;
import com.weather.sensor_service.Services.SensorService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.locks.ReentrantLock;


import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/sensors")
public class SensorController {

    // Endpoint can access service level
    private final SensorService service;

    // Used for creating records to ensure database integrity
    private final ReentrantLock lock = new ReentrantLock();

    // Constructor
    public SensorController(SensorService service) {
        this.service = service;
    }

    // Create a new sensor record
    // Lock used to ensure data integrity
    @PostMapping("/create-reading")
    public SensorReading createReading(@RequestBody SensorReading reading) {
        lock.lock();
        try {
            reading.setTimestamp(LocalDateTime.now());
            return service.saveReading(reading);
        } finally {
            lock.unlock();
        }
    }

    // Retrieves ALL sensor records
    @GetMapping("/get-all")
    public List<SensorReading> getReading() {
        return  service.getAllReadings();
    }


    // Retrieves a specific sensor record between 2 time points
    // Metrics can be dynamically requested
    // Time defaults to all time if no range given
    @GetMapping("/get-metrics-and-time-period")
    public List<SensorReading> getMetricsAndTimePeriod(@RequestParam("sensorId") Long sensorId,
                                                       @RequestParam (required = false, defaultValue = "false") boolean temperature,
                                                       @RequestParam (required = false, defaultValue = "false") boolean humidity,
                                                       @RequestParam (required = false, defaultValue = "false") boolean wind,
                                                       @RequestParam (required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                       @RequestParam (required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return service.getSpecificSensorMetricsBetweenTimePeriod(sensorId,temperature, humidity, wind, startDate,endDate);
    }

    // Returns statistics for a list of readings and their metrics between 2 time points
    // Statistic defaults to AVG (AVG, MAX, MIN, SUM)
    // Metrics can be dynamically requested
    // Time defaults to all time if no range given
    @GetMapping("/get-metrics-and-time-period-with-constraint")
    public List<SensorAggregationResponseDTO> getMetricsAndTimePeriodWithStatistic(@RequestParam("sensorIds") List<Long> sensorIds,
                                                                                    @RequestParam (required = false, defaultValue = "false") boolean temperature,
                                                                                    @RequestParam (required = false, defaultValue = "false") boolean humidity,
                                                                                    @RequestParam (required = false, defaultValue = "false") boolean wind,
                                                                                    @RequestParam (required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                                    @RequestParam (required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                                                    @RequestParam (defaultValue = "avg") String statistic) {

        return service.getMetricsAndTimePeriodWithConstraintAndStatistic(sensorIds,temperature, humidity, wind, startDate,endDate, statistic);
    }
}
