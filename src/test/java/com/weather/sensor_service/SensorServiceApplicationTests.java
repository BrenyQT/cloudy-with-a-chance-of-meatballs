package com.weather.sensor_service;

import com.weather.sensor_service.DTO.SensorAggregationResponseDTO;
import com.weather.sensor_service.Entity.SensorReading;
import com.weather.sensor_service.Exceptions.SensorExceptions;
import com.weather.sensor_service.Repository.SensorReadingRepository;
import com.weather.sensor_service.Services.SensorService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SensorServiceApplicationTests {

    @Autowired
    private SensorService service;

    @MockBean
    private SensorReadingRepository repository;

    public SensorServiceApplicationTests() {
        MockitoAnnotations.openMocks(this);
    }

    // *** 1. saveReading(SensorReading reading) TESTS ***

    // Throws SensorSaveException if sensorId is null.
    @Test
    void saveReadingThrowsWhenSensorIdIsMissing() {
        SensorReading reading = new SensorReading();
        reading.setTemperature(20.0);
        reading.setHumidity(60.0);
        reading.setWindSpeed(5.0);

        assertThrows(SensorExceptions.SensorSaveException.class, () -> service.saveReading(reading));
    }

    // Throws SensorSaveException if sensorId is null.
    @Test
    void saveReadingThrowsWhenTemperatureIsMissing() {
        SensorReading reading = new SensorReading();
        reading.setSensorId(11L);
        reading.setHumidity(60.0);
        reading.setWindSpeed(5.0);

        assertThrows(SensorExceptions.SensorSaveException.class, () -> service.saveReading(reading));
    }

    // Throws SensorSaveException if Wind Speed is null.
    @Test
    void saveReadingThrowsWhenWindSpeedIsMissing() {
        SensorReading reading = new SensorReading();
        reading.setSensorId(11L);
        reading.setTemperature(20.0);
        reading.setHumidity(60.0);

        assertThrows(SensorExceptions.SensorSaveException.class, () -> service.saveReading(reading));
    }

    // Throws SensorSaveException if Humidity is null.
    @Test
    void saveReadingThrowsWhenHumidityIsMissing() {
        SensorReading reading = new SensorReading();
        reading.setSensorId(11L);
        reading.setTemperature(20.0);
        reading.setWindSpeed(5.0);

        assertThrows(SensorExceptions.SensorSaveException.class, () -> service.saveReading(reading));
    }

    // HAPPY PATH - a new valid reading is saved
    @Test
    void saveValidReading() {
        SensorReading reading = new SensorReading();
        reading.setSensorId(11L);
        reading.setTemperature(20.0);
        reading.setHumidity(60.0);
        reading.setWindSpeed(5.0);

        service.saveReading(reading);

        verify(repository, times(1)).save(reading);

    }

    // Database failure
    @Test
    void saveReadingThrowsWhenRepositoryFails() {
        SensorReading reading = new SensorReading();
        reading.setSensorId(11L);
        reading.setTemperature(20.0);
        reading.setHumidity(60.0);
        reading.setWindSpeed(5.0);

        // Mock database failure
        when(repository.save(any(SensorReading.class)))
                .thenThrow(new RuntimeException("DB FAILED"));

        // Assert that an exception was thrown
        assertThrows(SensorExceptions.SensorSaveException.class, () -> {
            service.saveReading(reading);
        });
    }


    // *** 2. getAllReadings() TESTS ***

    // Throws exception when no data is present
    @Test
    void getAllReadingsThrowsWhenNoData() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(SensorExceptions.SensorNotFoundException.class, () -> {
            service.getAllReadings();
        });
    }

    // HAPPY PATH - all readings are returned
    @Test
    void getAllReadingsReturnsData() {
        SensorReading reading = new SensorReading();
        reading.setSensorId(11L);
        reading.setTemperature(20.0);
        reading.setHumidity(60.0);
        reading.setWindSpeed(5.0);
        reading.setTimestamp(LocalDateTime.now());

        when(repository.findAll()).thenReturn(List.of(reading));

        service.getAllReadings();

        verify(repository, times(1)).findAll();
    }


    // *** 3. getAllReadingsForaSensorId(Long sensorId) TESTS ***

    // Throws an error when empty list returned
    @Test
    void getReadingThrowsWhenSensorIdIsMissing() {
        when(repository.findBySensorId(1L)).thenReturn(Collections.emptyList());

        assertThrows(SensorExceptions.SensorNotFoundException.class, () -> {
            service.getAllReadings();
        });
    }

    // HAPPY PATH - returns all readings for a sensorId
    @Test
    void getReadingWithSensorIdReturnsData() {
        SensorReading reading = new SensorReading();
        reading.setSensorId(11L);
        reading.setTemperature(20.0);
        reading.setHumidity(60.0);
        reading.setWindSpeed(5.0);
        reading.setTimestamp(LocalDateTime.now());

        when(repository.findBySensorId(1L)).thenReturn(List.of(reading));

        List<SensorReading> result = service.getAllReadingsForaSensorId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(repository, times(1)).findBySensorId(1L);
    }


    // *** 4. getMetricsForSensor(Long sensorId, boolean temperature, boolean humidity, boolean wind) TESTS ***

    // Throws exception if sensorId has no records
    @Test
    void getReadingThrowsWhenSensorIdIsNotFound() {
        when(repository.findBySensorId(1L)).thenReturn(Collections.emptyList());

        assertThrows(SensorExceptions.SensorNotFoundException.class,
                () -> service.getMetricsForSensor(1L, true, true, true));

        verify(repository, times(1)).findBySensorId(1L);
    }

    // returns a list when only temperature = true
    @Test
    void getReadingUsingIdAndTemperature() {
        SensorReading reading = new SensorReading(1L, 1L, 25.0, 55.0, 7.0, LocalDateTime.now());
        when(repository.findBySensorId(1L)).thenReturn(List.of(reading));

        List<SensorReading> result = service.getMetricsForSensor(1L, true, false, false);

        assertEquals(1, result.size());
        assertEquals(25.0, result.get(0).getTemperature());
        assertNull(result.get(0).getHumidity());
        assertNull(result.get(0).getWindSpeed());
    }

    // returns a list when only humidity = true
    @Test
    void getReadingUsingIdAndHumidity() {
        SensorReading reading = new SensorReading(1L, 1L, 25.0, 55.0, 7.0, LocalDateTime.now());
        when(repository.findBySensorId(1L)).thenReturn(List.of(reading));

        List<SensorReading> result = service.getMetricsForSensor(1L, false, true, false);

        assertEquals(1, result.size());
        assertNull(result.get(0).getTemperature());
        assertEquals(55.0, result.get(0).getHumidity());
        assertNull(result.get(0).getWindSpeed());
    }

    // returns a list when only wind = true
    @Test
    void getReadingUsingIdAndWind() {
        SensorReading reading = new SensorReading(1L, 1L, 25.0, 55.0, 7.0, LocalDateTime.now());
        when(repository.findBySensorId(1L)).thenReturn(List.of(reading));

        List<SensorReading> result = service.getMetricsForSensor(1L, false, false, true);

        assertEquals(1, result.size());
        assertNull(result.get(0).getTemperature());
        assertNull(result.get(0).getHumidity());
        assertEquals(7.0, result.get(0).getWindSpeed());
    }

    // returns no metric fields when all three = false
    @Test
    void allFlagsFalse() {
        SensorReading reading = new SensorReading(1L, 1L, 25.0, 55.0, 7.0, LocalDateTime.now());
        when(repository.findBySensorId(1L)).thenReturn(List.of(reading));

        List<SensorReading> result = service.getMetricsForSensor(1L, false, false, false);

        assertEquals(1, result.size());
        assertNull(result.get(0).getTemperature());
        assertNull(result.get(0).getHumidity());
        assertNull(result.get(0).getWindSpeed());
    }

    // HAPPY PATH - Returns all readings with all metrics for a sensorId
    @Test
    void allFLagsTrue() {
        SensorReading reading = new SensorReading(1L, 1L, 25.0, 55.0, 7.0, LocalDateTime.now());
        when(repository.findBySensorId(1L)).thenReturn(List.of(reading));

        List<SensorReading> result = service.getMetricsForSensor(1L, true, true, true);

        assertEquals(1, result.size());
        assertEquals(25.0, result.get(0).getTemperature());
        assertEquals(55.0, result.get(0).getHumidity());
        assertEquals(7.0, result.get(0).getWindSpeed());
    }

    // *** 5. getSensorDataBetweenTimePeriod(Long sensorId, LocalDateTime startDate, LocalDateTime endDate) TESTS ***

    // start date is null
    @Test
    void nullStartDate() {
        LocalDateTime end = LocalDateTime.now();
        SensorReading reading = new SensorReading(1L, 1L, 25.0, 50.0, 5.0, LocalDateTime.now());
        when(repository.findBySensorIdAndTimestampBetween(eq(1L), any(LocalDateTime.class), eq(end)))
                .thenReturn(Collections.singletonList(reading));

        List<SensorReading> result = service.getSensorDataBetweenTimePeriod(1L, null, end);

        assertFalse(result.isEmpty());
        verify(repository).findBySensorIdAndTimestampBetween(eq(1L),
                eq(LocalDateTime.of(1970, 1, 1, 0, 0)), eq(end));
    }

    // end date is null
    @Test
    void nullEndDate() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
        SensorReading reading = new SensorReading(1L, 1L, 25.0, 50.0, 5.0, LocalDateTime.now());
        when(repository.findBySensorIdAndTimestampBetween(eq(1L), eq(start), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(reading));

        List<SensorReading> result = service.getSensorDataBetweenTimePeriod(1L, start, null);

        assertFalse(result.isEmpty());
        verify(repository).findBySensorIdAndTimestampBetween(eq(1L),
                eq(start), any(LocalDateTime.class));
    }

    // end date is before start date
    @Test
    void EndDateBeforeStartDate() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.minusDays(1);

        assertThrows(SensorExceptions.MetricCalculationException.class,
                () -> service.getSensorDataBetweenTimePeriod(1L, start, end));
    }

    // no readings found between startDate and endDate
    @Test
    void NoReadingsFound() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 2, 0, 0);

        when(repository.findBySensorIdAndTimestampBetween(1L, start, end))
                .thenReturn(Collections.emptyList());

        assertThrows(SensorExceptions.SensorNotFoundException.class,
                () -> service.getSensorDataBetweenTimePeriod(1L, start, end));
    }

    // HAPPY PATH - Returns found readings
    @Test
    void returnsReadings() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 2, 0, 0);
        SensorReading reading = new SensorReading(1L, 1L, 25.0, 50.0, 5.0, LocalDateTime.now());

        when(repository.findBySensorIdAndTimestampBetween(1L, start, end))
                .thenReturn(Collections.singletonList(reading));

        List<SensorReading> result = service.getSensorDataBetweenTimePeriod(1L, start, end);

        assertEquals(1, result.size());
        assertEquals(reading, result.get(0));
    }


    // *** 6. getSpecificSensorMetricsBetweenTimePeriod(Long sensorId,boolean temperature, boolean humidity, boolean wind, LocalDateTime startDate, LocalDateTime endDate) TESTS ***

    // HAPPY PATH - metrics are retuened between time
    @Test
    void returnsFilteredMetricsBetweenDates() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 2, 0, 0);

        SensorReading reading = new SensorReading(1L, 1L, 25.0, 50.0, 5.0, LocalDateTime.now());
        when(repository.findBySensorIdAndTimestampBetween(1L, start, end))
                .thenReturn(Collections.singletonList(reading));

        // Only temperature
        List<SensorReading> tempOnly = service.getSpecificSensorMetricsBetweenTimePeriod(1L, true, false, false, start, end);
        assertEquals(25.0, tempOnly.get(0).getTemperature());
        assertNull(tempOnly.get(0).getHumidity());
        assertNull(tempOnly.get(0).getWindSpeed());

        // Only humidity
        List<SensorReading> humidityOnly = service.getSpecificSensorMetricsBetweenTimePeriod(1L, false, true, false, start, end);
        assertNull(humidityOnly.get(0).getTemperature());
        assertEquals(50.0, humidityOnly.get(0).getHumidity());
        assertNull(humidityOnly.get(0).getWindSpeed());

        // Only wind
        List<SensorReading> windOnly = service.getSpecificSensorMetricsBetweenTimePeriod(1L, false, false, true, start, end);
        assertNull(windOnly.get(0).getTemperature());
        assertNull(windOnly.get(0).getHumidity());
        assertEquals(5.0, windOnly.get(0).getWindSpeed());

        // All false
        List<SensorReading> allNull = service.getSpecificSensorMetricsBetweenTimePeriod(1L, false, false, false, start, end);
        assertNull(allNull.get(0).getTemperature());
        assertNull(allNull.get(0).getHumidity());
        assertNull(allNull.get(0).getWindSpeed());

        // All true
        List<SensorReading> allMetrics = service.getSpecificSensorMetricsBetweenTimePeriod(1L, true, true, true, start, end);
        assertEquals(25.0, allMetrics.get(0).getTemperature());
        assertEquals(50.0, allMetrics.get(0).getHumidity());
        assertEquals(5.0, allMetrics.get(0).getWindSpeed());
    }



	// *** 7. getSpecificMetrics(List<SensorReading> readings, boolean temperature, boolean humidity, boolean wind) TESTS ***

	// Readings is null
    @Test
    void ReadingsNull() {
        assertThrows(SensorExceptions.SensorNotFoundException.class,
                () -> service.getSpecificMetrics(null, true, false, false));
    }

    // Readings is empty
    @Test
    void ReadingsEmpty() {
        assertThrows(SensorExceptions.SensorNotFoundException.class,
                () -> service.getSpecificMetrics(Collections.emptyList(), true, false, false));
    }


    // only requested metrics
    @Test
    void returnsOnlyRequestedMetrics() {
        SensorReading reading = new SensorReading(1L, 1L, 25.0, 50.0, 5.0, LocalDateTime.now());
        List<SensorReading> input = Collections.singletonList(reading);

        List<SensorReading> tempOnly = service.getSpecificMetrics(input, true, false, false);
        assertEquals(25.0, tempOnly.get(0).getTemperature());
        assertNull(tempOnly.get(0).getHumidity());
        assertNull(tempOnly.get(0).getWindSpeed());

        List<SensorReading> humidityOnly = service.getSpecificMetrics(input, false, true, false);
        assertNull(humidityOnly.get(0).getTemperature());
        assertEquals(50.0, humidityOnly.get(0).getHumidity());
        assertNull(humidityOnly.get(0).getWindSpeed());

        List<SensorReading> windOnly = service.getSpecificMetrics(input, false, false, true);
        assertNull(windOnly.get(0).getTemperature());
        assertNull(windOnly.get(0).getHumidity());
        assertEquals(5.0, windOnly.get(0).getWindSpeed());
    }

    // all metrics flags = true
    @Test
    void returnsFullDataAllFlagsTrue() {
        SensorReading reading = new SensorReading(1L, 1L, 25.0, 50.0, 5.0, LocalDateTime.now());
        List<SensorReading> input = Collections.singletonList(reading);

        List<SensorReading> result = service.getSpecificMetrics(input, true, true, true);
        assertEquals(25.0, result.get(0).getTemperature());
        assertEquals(50.0, result.get(0).getHumidity());
        assertEquals(5.0, result.get(0).getWindSpeed());
    }

    // all metrics flags = false
    @Test
    void returnsAllNullIfAllFlagsFalse() {
        SensorReading reading = new SensorReading(1L, 1L, 25.0, 50.0, 5.0, LocalDateTime.now());
        List<SensorReading> input = Collections.singletonList(reading);

        List<SensorReading> result = service.getSpecificMetrics(input, false, false, false);
        assertNull(result.get(0).getTemperature());
        assertNull(result.get(0).getHumidity());
        assertNull(result.get(0).getWindSpeed());
    }

    // object property missing
    @Test
    void throwsIfMappingFails() {
        // We'll create a corrupted SensorReading subclass to throw an exception when accessing a getter
        SensorReading corrupted = new SensorReading(1L, 1L, 25.0, 50.0, 5.0, LocalDateTime.now()) {
            @Override
            public Double getTemperature() {
                throw new RuntimeException("corrupted");
            }
        };

        List<SensorReading> input = Collections.singletonList(corrupted);

        assertThrows(SensorExceptions.SensorSaveException.class,
                () -> service.getSpecificMetrics(input, true, false, false));
    }

    // *** 8. getMetricsAndTimePeriodWithConstraintAndStatistic(Long sensorId,boolean temperature,boolean humidity,boolean wind,LocalDateTime startDate,LocalDateTime endDate, String statistic TESTS ***

    // returns min statistic
    @Test
    void returnsMinStatistic() {
        LocalDateTime now = LocalDateTime.now();
        SensorReading r1 = new SensorReading(1L, 11L, 10.0, 20.0, 5.0, now);
        SensorReading r2 = new SensorReading(2L, 11L, 30.0, 40.0, 1.0, now);
        when(repository.findBySensorIdAndTimestampBetween(anyLong(), any(), any())).thenReturn(Arrays.asList(r1, r2));

        SensorAggregationResponseDTO dto = service.getMetricsAndTimePeriodWithConstraintAndStatistic(
                11L, true, true, true, now.minusDays(1), now, "min");

        assertEquals(10.0, dto.getTemperatureMetric());
        assertEquals(20.0, dto.getHumidityMetric());
        assertEquals(1.0, dto.getWindSpeedMetric());
    }


    // returns max statistic
    @Test
    void returnsMaxStatistic() {
        LocalDateTime now = LocalDateTime.now();
        SensorReading r1 = new SensorReading(1L, 11L, 10.0, 20.0, 5.0, now);
        SensorReading r2 = new SensorReading(2L, 11L, 30.0, 40.0, 1.0, now);
        when(repository.findBySensorIdAndTimestampBetween(anyLong(), any(), any())).thenReturn(Arrays.asList(r1, r2));

        SensorAggregationResponseDTO dto = service.getMetricsAndTimePeriodWithConstraintAndStatistic(
                11L, true, true, true, now.minusDays(1), now, "max");

        assertEquals(30.0, dto.getTemperatureMetric());
        assertEquals(40.0, dto.getHumidityMetric());
        assertEquals(5.0, dto.getWindSpeedMetric());
    }

    // returns sum statistic
    @Test
    void returnsSumStatistic() {
        LocalDateTime now = LocalDateTime.now();
        SensorReading r1 = new SensorReading(1L, 11L, 10.0, 20.0, 5.0, now);
        SensorReading r2 = new SensorReading(2L, 11L, 30.0, 40.0, 15.0, now);
        when(repository.findBySensorIdAndTimestampBetween(anyLong(), any(), any())).thenReturn(Arrays.asList(r1, r2));

        SensorAggregationResponseDTO dto = service.getMetricsAndTimePeriodWithConstraintAndStatistic(
                11L, true, true, true, now.minusDays(1), now, "sum");

        assertEquals(40.0, dto.getTemperatureMetric());
        assertEquals(60.0, dto.getHumidityMetric());
        assertEquals(20.0, dto.getWindSpeedMetric());
    }

    // return average statistic
    @Test
    void returnsAvgStatistic() {
        LocalDateTime now = LocalDateTime.now();
        SensorReading r1 = new SensorReading(1L, 11L, 10.0, 20.0, 5.0, now);
        SensorReading r2 = new SensorReading(2L, 11L, 30.0, 40.0, 15.0, now);
        when(repository.findBySensorIdAndTimestampBetween(anyLong(), any(), any())).thenReturn(Arrays.asList(r1, r2));

        SensorAggregationResponseDTO dto = service.getMetricsAndTimePeriodWithConstraintAndStatistic(
                11L, true, true, true, now.minusDays(1), now, "avg");

        assertEquals(20.0, dto.getTemperatureMetric());
        assertEquals(30.0, dto.getHumidityMetric());
        assertEquals(10.0, dto.getWindSpeedMetric());
    }

    // Invalid statistic
    @Test
    void InvalidStatistic() {
        LocalDateTime now = LocalDateTime.now();
        SensorReading r1 = new SensorReading(1L, 11L, 10.0, 20.0, 5.0, now);
        when(repository.findBySensorIdAndTimestampBetween(anyLong(), any(), any())).thenReturn(Collections.singletonList(r1));

        assertThrows(SensorExceptions.MetricCalculationException.class,
                () -> service.getMetricsAndTimePeriodWithConstraintAndStatistic(11L, true, true, true, now.minusDays(1), now, "invalid"));
    }

    // invalid statistics data
    @Test
    void MetricListIsBad() {
        LocalDateTime now = LocalDateTime.now();
        SensorReading r1 = new SensorReading(1L, 11L, null, null, null, now);
        when(repository.findBySensorIdAndTimestampBetween(anyLong(), any(), any())).thenReturn(Collections.singletonList(r1));

        assertThrows(SensorExceptions.MetricCalculationException.class,
                () -> service.getMetricsAndTimePeriodWithConstraintAndStatistic(11L, true, true, true, now.minusDays(1), now, "avg"));
    }

    // only temperature flag
    @Test
    void returnsOnlyTemperatureFlag() {
        LocalDateTime now = LocalDateTime.now();
        SensorReading r1 = new SensorReading(1L, 11L, 10.0, 20.0, 5.0, now);
        when(repository.findBySensorIdAndTimestampBetween(anyLong(), any(), any())).thenReturn(Collections.singletonList(r1));

        SensorAggregationResponseDTO dto = service.getMetricsAndTimePeriodWithConstraintAndStatistic(11L, true, false, false, now.minusDays(1), now, "avg");

        assertNotNull(dto.getTemperatureMetric());
        assertNull(dto.getHumidityMetric());
        assertNull(dto.getWindSpeedMetric());
    }

    // only Humidity flag
    @Test
    void returnsOnlyHumidityFlag() {
        LocalDateTime now = LocalDateTime.now();
        SensorReading r1 = new SensorReading(1L, 11L, 10.0, 20.0, 5.0, now);
        when(repository.findBySensorIdAndTimestampBetween(anyLong(), any(), any())).thenReturn(Collections.singletonList(r1));

        SensorAggregationResponseDTO dto = service.getMetricsAndTimePeriodWithConstraintAndStatistic(11L, false, true, false, now.minusDays(1), now, "avg");

        assertNull(dto.getTemperatureMetric());
        assertNotNull(dto.getHumidityMetric());
        assertNull(dto.getWindSpeedMetric());
    }

    // only Wind flag
    @Test
    void returnsOnlyWindFlag() {
        LocalDateTime now = LocalDateTime.now();
        SensorReading r1 = new SensorReading(1L, 11L, 10.0, 20.0, 5.0, now);
        when(repository.findBySensorIdAndTimestampBetween(anyLong(), any(), any())).thenReturn(Collections.singletonList(r1));

        SensorAggregationResponseDTO dto = service.getMetricsAndTimePeriodWithConstraintAndStatistic(11L, false, false, true, now.minusDays(1), now, "avg");

        assertNull(dto.getTemperatureMetric());
        assertNull(dto.getHumidityMetric());
        assertNotNull(dto.getWindSpeedMetric());
    }

    // returns temperature and humidity flag
    @Test
    void returnsMultipleMetricsWhenMultipleFlagsTrue() {
        LocalDateTime now = LocalDateTime.now();
        SensorReading r1 = new SensorReading(1L, 11L, 10.0, 20.0, 5.0, now);
        when(repository.findBySensorIdAndTimestampBetween(anyLong(), any(), any())).thenReturn(Collections.singletonList(r1));

        SensorAggregationResponseDTO dto = service.getMetricsAndTimePeriodWithConstraintAndStatistic(11L, true, true, false, now.minusDays(1), now, "avg");

        assertNotNull(dto.getTemperatureMetric());
        assertNotNull(dto.getHumidityMetric());
        assertNull(dto.getWindSpeedMetric());
    }

    // check if inputs are set as null if false flag
    @Test
    void setsUnusedMetricsToNull() {
        LocalDateTime now = LocalDateTime.now();
        SensorReading r1 = new SensorReading(1L, 11L, 10.0, 20.0, 5.0, now);
        when(repository.findBySensorIdAndTimestampBetween(anyLong(), any(), any())).thenReturn(Collections.singletonList(r1));

        SensorAggregationResponseDTO dto = service.getMetricsAndTimePeriodWithConstraintAndStatistic(11L, true, false, false, now.minusDays(1), now, "avg");
        assertNotNull(dto.getTemperatureMetric());
        assertNull(dto.getHumidityMetric());
        assertNull(dto.getWindSpeedMetric());

    }

    // sets null date to all dates
    @Test
    void nullStartDateTo1970() {
        SensorReading reading = new SensorReading(1L, 11L, 20.0, 50.0, 5.0, LocalDateTime.now());
        when(repository.findBySensorIdAndTimestampBetween(eq(11L), any(), any())).thenReturn(Collections.singletonList(reading));

        assertDoesNotThrow(() -> {
            service.getSensorDataBetweenTimePeriod(11L, null, LocalDateTime.now());
        });
    }

    // sets null end date to now
    @Test
    void nullEndDateToNow() {
        SensorReading reading = new SensorReading(1L, 11L, 20.0, 50.0, 5.0, LocalDateTime.now());
        when(repository.findBySensorIdAndTimestampBetween(eq(11L), any(), any())).thenReturn(Collections.singletonList(reading));

        assertDoesNotThrow(() -> {
            service.getSensorDataBetweenTimePeriod(11L, LocalDateTime.now().minusDays(1), null);
        });
    }
}
