package com.weather.sensor_service.Services;

import com.weather.sensor_service.DTO.SensorAggregationResponseDTO;
import com.weather.sensor_service.Entity.SensorReading;
import com.weather.sensor_service.Exceptions.SensorExceptions;
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

        if (reading.getSensorId() == null) {
            throw new SensorExceptions.SensorSaveException("sensorId is needed to create reading ");
        }
        if (reading.getTemperature() == null) {
            throw new SensorExceptions.SensorSaveException("temperature is needed to create reading ");
        }
        if (reading.getHumidity() == null) {
            throw new SensorExceptions.SensorSaveException("humidity is needed to create reading ");
        }
        if (reading.getWindSpeed() == null) {
            throw new SensorExceptions.SensorSaveException("windSpeed is needed to create reading ");
        }

        try {
            return repository.save(reading);
        } catch (Exception e) {
            throw new SensorExceptions.SensorSaveException(
                    "Cant save sensor reading for sensorId: " + reading.getSensorId() + " Reason : " + e.getMessage());
        }
    }


    // Returns all records
    public List<SensorReading> getAllReadings() {
        List<SensorReading> readings = repository.findAll();

        if (readings.isEmpty()) {
            throw new SensorExceptions.SensorNotFoundException("No sensor readings in database");
        }

        return readings;
    }


    // Returns all records for a sensorId
    public List<SensorReading> getAllReadingsForaSensorId(Long sensorId) {
        List<SensorReading> readings = repository.findBySensorId(sensorId);

        if (readings.isEmpty()) {
            throw new SensorExceptions.SensorNotFoundException("No readings found in databases for sensorId : " + sensorId);
        }

        return readings;
    }


    // Returns passed metrics for a specific sensor
    public List<SensorReading> getMetricsForSensor(Long sensorId, boolean temperature, boolean humidity, boolean wind) {

        List<SensorReading> readings = repository.findBySensorId(sensorId);

        if (readings.isEmpty()) {
            throw new SensorExceptions.SensorNotFoundException("No readings found in databases for sensorId : " + sensorId);
        }

        return getSpecificMetrics(readings, temperature, humidity, wind);
    }


    // Return records for a specific sensor between time periods
    public List<SensorReading> getSensorDataBetweenTimePeriod(Long sensorId, LocalDateTime startDate, LocalDateTime endDate) {

        LocalDateTime[] validatedDates = validateAndNormalizeDates(startDate, endDate);
        startDate = validatedDates[0];
        endDate = validatedDates[1];

        List<SensorReading> readings = repository.findBySensorIdAndTimestampBetween(sensorId, startDate, endDate);

        if (readings.isEmpty()) {
            throw new SensorExceptions.SensorNotFoundException(
                    "No readings found in databases for sensorId : " + sensorId + " between " + startDate + " and " + endDate);
        }

        return readings;
    }


    // Returns specified metrics between a time period
    public List<SensorReading> getSpecificSensorMetricsBetweenTimePeriod(
            Long sensorId, boolean temperature, boolean humidity, boolean wind,
            LocalDateTime startDate, LocalDateTime endDate) {

        LocalDateTime[] validatedDates = validateAndNormalizeDates(startDate, endDate);
        startDate = validatedDates[0];
        endDate = validatedDates[1];

        List<SensorReading> timeReadings = repository.findBySensorIdAndTimestampBetween(sensorId, startDate, endDate);

        if (timeReadings.isEmpty()) {
            throw new SensorExceptions.SensorNotFoundException(
                    "No readings found in databases for sensorId : " + sensorId + " between " + startDate + " and " + endDate);
        }

        return getSpecificMetrics(timeReadings, temperature, humidity, wind);
    }


    public List<SensorReading> getSpecificMetrics(List<SensorReading> readings, boolean temperature, boolean humidity, boolean wind) {

        if (readings == null || readings.isEmpty()) {
            throw new SensorExceptions.SensorNotFoundException("No sensor readings so cant filter metrics");
        }
        // Create a new DTO based on the properties set as true
        try {
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
        } catch (Exception e) {
            throw new SensorExceptions.SensorSaveException("Cannot process input : " + e.getMessage());

        }
    }

    // Helper function used to calculate based on statistic
    private Double calculate(List<Double> values, String statistic) {

        // Safety check on data integrity
        if (values == null || values.isEmpty()) {
            throw new SensorExceptions.MetricCalculationException("Cannot calculate " + statistic + " when its empty");
        }

        // Switch case based on statistic needed
        // Turns a list fo doubles into a stream so that I can operate on it
        try {
            return switch (statistic.toLowerCase()) {
                case "min" -> values.stream().mapToDouble(Double::doubleValue).min().orElse(Double.NaN);
                case "max" -> values.stream().mapToDouble(Double::doubleValue).max().orElse(Double.NaN);
                case "sum" -> values.stream().mapToDouble(Double::doubleValue).sum();
                case "avg" -> values.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
                default -> throw new SensorExceptions.MetricCalculationException("Invalid statistic inputted : " + statistic);
            };
        } catch (Exception e) {
            throw new SensorExceptions.MetricCalculationException("Cant calculate " + statistic);
        }
    }


    public SensorAggregationResponseDTO getMetricsAndTimePeriodWithConstraintAndStatistic(
            Long sensorId,
            boolean temperature,
            boolean humidity,
            boolean wind,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String statistic) {

        LocalDateTime[] validatedDates = validateAndNormalizeDates(startDate, endDate);
        startDate = validatedDates[0];
        endDate = validatedDates[1];

        // Get readings between time ranges
        List<SensorReading> readings = repository.findBySensorIdAndTimestampBetween(sensorId, startDate, endDate);

        if (readings.isEmpty()) {
            throw new SensorExceptions.SensorNotFoundException(
                    "No readings found in databases for sensorId : " + sensorId + " between " + startDate + " and " + endDate);
        }

        // Fields to hold outputs
        Double temperatureMetric = null;
        Double humidityMetric = null;
        Double windSpeedMetric = null;

        // If Temperature true perform calculate function with specific statistic
        // Creates a list of non-null double values
        try {
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

        } catch (SensorExceptions.MetricCalculationException e) {
            throw new SensorExceptions.MetricCalculationException(
                    "Failed to calculate " + statistic + " metrics for sensorId " + sensorId);
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

        // Then nulling metrics if not requested
        if (!temperature) response.setTemperatureMetric(null);
        if (!humidity) response.setHumidityMetric(null);
        if (!wind) response.setWindSpeedMetric(null);

        return response;
    }


    // Helper function to check integrity of inputted startDate and endDate
    private LocalDateTime[] validateAndNormalizeDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null) {
            startDate = LocalDateTime.of(1970, 1, 1, 0, 0);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        if (endDate.isBefore(startDate)) {
            throw new SensorExceptions.MetricCalculationException(
                    "End date cant be before start date  startDate = " + startDate + " endDate = " + endDate
            );
        }

        return new LocalDateTime[]{startDate, endDate};
    }


}


