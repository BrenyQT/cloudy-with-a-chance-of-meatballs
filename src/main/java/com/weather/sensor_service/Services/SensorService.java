package com.weather.sensor_service.Services;

import com.weather.sensor_service.DTO.SensorDTO;
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

    // Returns passed metrics for a specific sensor
    public List<SensorDTO> getMetricsForSensor(Long sensorId, boolean temperature, boolean humidity, boolean wind) {

        // TO:DO I CAN MAKE THIS SO I ONLY HAVE TO GET SPECIFIC PROPERTIES AND NOT RETURN ENTIRE OBJECT
        List<SensorReading> readings = repository.findBySensorId(sensorId);

        // Create a new DTO based on the properties set as true
        return readings.stream()
                .map(reading -> new SensorDTO(
                        reading.getId(),
                        temperature ? reading.getTemperature() : null,
                        humidity ? reading.getHumidity() : null,
                        wind ? reading.getWindSpeed() : null
                ))
                .toList();
    }

    // Return records for a specific sensor between time periods
    public List<SensorReading> getSensorDataBetweenTimePeriod(Long sensorId, LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findBySensorIdAndTimestampBetween(sensorId, startDate, endDate);
    }

}


// Return MIN/MAX/SUM/AVG for a specific sensor
// sensorId  - which sensor
// parameter - MIN/MAX/SUM/AVG
// statistic - Temperature/Humidity/WindSpeed
// Return Average
// Return between DATE

// Isolate a particular metric


