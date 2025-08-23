package com.weather.sensor_service.Services;

import com.weather.sensor_service.DTO.SensorAggregationResponseDTO;
import com.weather.sensor_service.Entity.SensorReading;
import com.weather.sensor_service.Repository.SensorReadingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
    public List<SensorReading> getMetricsForSensor(Long sensorId, boolean temperature, boolean humidity, boolean wind) {

        List<SensorReading> readings = repository.findBySensorId(sensorId);

        return getSpecificMetrics(readings, temperature,humidity,wind);
    }

    // Return records for a specific sensor between time periods
    public List<SensorReading> getSensorDataBetweenTimePeriod(Long sensorId, LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findBySensorIdAndTimestampBetween(sensorId, startDate, endDate);
    }

    public List<SensorReading> getSpecificSensorMetricsBetweenTimePeriod (Long sensorId, boolean temperature, boolean humidity, boolean wind, LocalDateTime startDate, LocalDateTime endDate){
        List<SensorReading> timeReadings = repository.findBySensorIdAndTimestampBetween(sensorId, startDate, endDate);
        return getSpecificMetrics(timeReadings, temperature,humidity,wind);
    }


    public List<SensorReading> getSpecificMetrics (List<SensorReading> readings, boolean temperature, boolean humidity, boolean wind){
        // Create a new DTO based on the properties set as true
        return readings.stream()
                .map(reading -> new SensorReading(
                        reading.getId(),
                        reading.getSensorId(),
                        temperature ? reading.getTemperature() : null,
                        humidity ? reading.getHumidity() : null,
                        wind ? reading.getWindSpeed() : null,
                        reading.getTimestamp()
                ))
                .toList();
    }

    // Helper function used to calculate based on statistic
    private Double calculate(List<Double> values, String statistic) {

        // Safety check on data integrity
        if (values == null || values.isEmpty()) return null;

        // Switch case based on statistic needed
        // Turns a list fo doubles into a stream so that I can operate on it
        return switch (statistic.toLowerCase()) {
            case "min" -> values.stream().mapToDouble(Double::doubleValue).min().orElse(Double.NaN);    // Return smallest number from stream
            case "max" -> values.stream().mapToDouble(Double::doubleValue).max().orElse(Double.NaN);    // Return biggest number from stream
            case "sum" -> values.stream().mapToDouble(Double::doubleValue).sum();                       // Return sum of stream
            case "avg" -> values.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);// Return average of stream
            default -> throw new IllegalArgumentException(statistic);
        };
    }


    public SensorAggregationResponseDTO getMetricsAndTimePeriodWithConstraintAndStatistic(
                                                                                                Long sensorId,
                                                                                                boolean temperature,
                                                                                                boolean humidity,
                                                                                                boolean wind,
                                                                                                LocalDateTime startDate,
                                                                                                LocalDateTime endDate,
                                                                                                String statistic){

        // If no date range is provided use all records
        if (startDate == null || endDate == null) {
            startDate = LocalDateTime.of(1970, 1, 1, 0, 0);
            endDate = LocalDateTime.now();
        }

        // Get readings between time ranges
        List<SensorReading> readings = repository.findBySensorIdAndTimestampBetween(sensorId, startDate, endDate);

        // Fields to hold outputs
        Double temperatureMetric = null;
        Double humidityMetric = null;
        Double windSpeedMetric = null;

        // If Temperature true perform calculate function with specific statistic
        // Creates a list of non-null double values
        if (temperature) {
            temperatureMetric = calculate(
                    readings.stream()
                            .map(SensorReading::getTemperature)
                            .filter(Objects::nonNull)
                            .toList(),
                    statistic
            );
        }

        // If Humidity true perform calculate function with specific statistic
        // Creates a list of non-null double values
        if (humidity) {
            humidityMetric = calculate(
                    readings.stream()
                            .map(SensorReading::getHumidity)
                            .filter(Objects::nonNull)
                            .toList(),
                    statistic
            );
        }

        // If Wind Speed true perform calculate function with specific statistic
        // Creates a list of non-null double values
        if (wind) {
            windSpeedMetric = calculate(
                    readings.stream()
                            .map(SensorReading::getWindSpeed)
                            .filter(Objects::nonNull)
                            .toList(),
                    statistic
            );
        }


        // Create object with statistics to return
        // Set metrics as doubles to create DTO
        // Doubles need t be a value to create object
        SensorAggregationResponseDTO response = new SensorAggregationResponseDTO(
                sensorId,
                statistic,
                temperatureMetric != null ? temperatureMetric : 0.0,
                windSpeedMetric != null ? windSpeedMetric : 0.0,
                humidityMetric != null ? humidityMetric : 0.0,
                startDate,
                endDate
        );

        // Update values t null for printing if none provided
        if (!temperature) response.setTemperatureMetric(null);
        if (!humidity) response.setHumidityMetric(null);
        if (!wind) response.setWindSpeedMetric(null);

        return response;
    }


}


