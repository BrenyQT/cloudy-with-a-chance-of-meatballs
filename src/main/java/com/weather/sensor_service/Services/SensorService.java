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

    // Returns all records
    public List<SensorReading> getAllReadings() {
        return repository.findAll();
    }

    // Return the average Metrics for Humidity, Wind Speed or Temperature for a particular sensor
    public List<SensorReading> getAllReadingsForaSensorId(Long sensorId) {
        return repository.findBySensorId(sensorId);
    }







    // Return MIN/MAX/SUM/AVG for a specific sensor
    // sensorId  - which sensor
    // parameter - MIN/MAX/SUM/AVG
    // statistic - Temperature/Humidity/WindSpeed
    // Return Average
    // Return between DATE

    // Isolate a particular metric

}
