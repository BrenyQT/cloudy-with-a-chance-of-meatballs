package com.weather.sensor_service.Services;

import com.weather.sensor_service.Entity.SensorReading;
import com.weather.sensor_service.Repository.SensorReadingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SensorService {

    // Repository handles db functionality
    private final SensorReadingRepository repository;

    public SensorService(SensorReadingRepository repository) {
        this.repository = repository;
    }

    // Saves a sensor reading when object hits endpoint

    public SensorReading saveReading(SensorReading reading) {

        // TO:DO add fault handling here
        return repository.save(reading);
    }

    // Return the average Metrics for Humidity, Wind Speed or Temperature
    public SensorReading findSpecificSensorReading(SensorReading reading) {
        return repository.findById(reading.getId()).orElse(null);
    }

    // Return MIN/MAX/SUM/AVG for a specific sensor
    // sensorId  - which sensor
    // parameter - MIN/MAX/SUM/AVG
    // statistic - Temperature/Humidity/WindSpeed



    // Return Average
    // Return between DATE

    // Isolate a particular metric

}
