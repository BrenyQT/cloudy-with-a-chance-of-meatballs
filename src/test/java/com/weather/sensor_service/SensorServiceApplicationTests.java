package com.weather.sensor_service;

import com.weather.sensor_service.DTO.SensorAggregationResponseDTO;
import com.weather.sensor_service.Entity.SensorReading;
import com.weather.sensor_service.Exceptions.SensorExceptions;
import com.weather.sensor_service.Repository.SensorReadingRepository;
import com.weather.sensor_service.Services.SensorService;
import org.junit.jupiter.api.Test;
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

    // ***  saveReading(SensorReading reading) TESTS ***

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


    // ***  getAllReadings() TESTS ***

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


    // ***  getAllReadingsForaSensorId(Long sensorId) TESTS ***

    // Throws an error when empty list returned
    @Test
    void getReadingThrowsWhenSensorIdIsMissing() {
        when(repository.findBySensorId(1L)).thenReturn(Collections.emptyList());

        assertThrows(SensorExceptions.SensorNotFoundException.class, () -> {
            service.getAllReadings();
        });
    }

    // ***  getSpecificSensorMetricsBetweenTimePeriod(Long sensorId,boolean temperature, boolean humidity, boolean wind, LocalDateTime startDate, LocalDateTime endDate) TESTS ***

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
        assertEquals(25.0, tempOnly.getFirst().getTemperature());
        assertNull(tempOnly.getFirst().getHumidity());
        assertNull(tempOnly.getFirst().getWindSpeed());

        // Only humidity
        List<SensorReading> humidityOnly = service.getSpecificSensorMetricsBetweenTimePeriod(1L, false, true, false, start, end);
        assertNull(humidityOnly.getFirst().getTemperature());
        assertEquals(50.0, humidityOnly.getFirst().getHumidity());
        assertNull(humidityOnly.getFirst().getWindSpeed());

        // Only wind
        List<SensorReading> windOnly = service.getSpecificSensorMetricsBetweenTimePeriod(1L, false, false, true, start, end);
        assertNull(windOnly.getFirst().getTemperature());
        assertNull(windOnly.getFirst().getHumidity());
        assertEquals(5.0, windOnly.getFirst().getWindSpeed());

        // All false
        List<SensorReading> allNull = service.getSpecificSensorMetricsBetweenTimePeriod(1L, false, false, false, start, end);
        assertNull(allNull.getFirst().getTemperature());
        assertNull(allNull.getFirst().getHumidity());
        assertNull(allNull.getFirst().getWindSpeed());

        // All true
        List<SensorReading> allMetrics = service.getSpecificSensorMetricsBetweenTimePeriod(1L, true, true, true, start, end);
        assertEquals(25.0, allMetrics.getFirst().getTemperature());
        assertEquals(50.0, allMetrics.getFirst().getHumidity());
        assertEquals(5.0, allMetrics.getFirst().getWindSpeed());
    }


    // *** getSpecificMetrics(List<SensorReading> readings, boolean temperature, boolean humidity, boolean wind) TESTS ***

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
        assertEquals(25.0, tempOnly.getFirst().getTemperature());
        assertNull(tempOnly.getFirst().getHumidity());
        assertNull(tempOnly.getFirst().getWindSpeed());

        List<SensorReading> humidityOnly = service.getSpecificMetrics(input, false, true, false);
        assertNull(humidityOnly.getFirst().getTemperature());
        assertEquals(50.0, humidityOnly.getFirst().getHumidity());
        assertNull(humidityOnly.getFirst().getWindSpeed());

        List<SensorReading> windOnly = service.getSpecificMetrics(input, false, false, true);
        assertNull(windOnly.getFirst().getTemperature());
        assertNull(windOnly.getFirst().getHumidity());
        assertEquals(5.0, windOnly.getFirst().getWindSpeed());
    }

    // all metrics flags = true
    @Test
    void returnsFullDataAllFlagsTrue() {
        SensorReading reading = new SensorReading(1L, 1L, 25.0, 50.0, 5.0, LocalDateTime.now());
        List<SensorReading> input = Collections.singletonList(reading);

        List<SensorReading> result = service.getSpecificMetrics(input, true, true, true);
        assertEquals(25.0, result.getFirst().getTemperature());
        assertEquals(50.0, result.getFirst().getHumidity());
        assertEquals(5.0, result.getFirst().getWindSpeed());
    }

    // all metrics flags = false
    @Test
    void returnsAllNullIfAllFlagsFalse() {
        SensorReading reading = new SensorReading(1L, 1L, 25.0, 50.0, 5.0, LocalDateTime.now());
        List<SensorReading> input = Collections.singletonList(reading);

        List<SensorReading> result = service.getSpecificMetrics(input, false, false, false);
        assertNull(result.getFirst().getTemperature());
        assertNull(result.getFirst().getHumidity());
        assertNull(result.getFirst().getWindSpeed());
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

// *** getMetricsAndTimePeriodWithConstraintAndStatistic(List<Long> sensorIds, boolean temperature, boolean humidity, boolean wind, LocalDateTime startDate, LocalDateTime endDate, String statistic TESTS ***

    // returns min statistic
    @Test
    void returnsMinStatistic() {
        LocalDateTime now = LocalDateTime.now();
        SensorReading r1 = new SensorReading(1L, 11L, 10.0, 20.0, 5.0, now);
        SensorReading r2 = new SensorReading(2L, 11L, 30.0, 40.0, 1.0, now);
        when(repository.findBySensorIdAndTimestampBetween(eq(11L), any(), any())).thenReturn(Arrays.asList(r1, r2));

        List<SensorAggregationResponseDTO> dtos = service.getMetricsAndTimePeriodWithConstraintAndStatistic(
                List.of(11L), true, true, true, now.minusDays(1), now, "min");

        SensorAggregationResponseDTO dto = dtos.getFirst();

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
        when(repository.findBySensorIdAndTimestampBetween(eq(11L), any(), any())).thenReturn(Arrays.asList(r1, r2));

        List<SensorAggregationResponseDTO> dtos = service.getMetricsAndTimePeriodWithConstraintAndStatistic(
                List.of(11L), true, true, true, now.minusDays(1), now, "max");

        SensorAggregationResponseDTO dto = dtos.getFirst();

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
        when(repository.findBySensorIdAndTimestampBetween(eq(11L), any(), any())).thenReturn(Arrays.asList(r1, r2));

        List<SensorAggregationResponseDTO> dtos = service.getMetricsAndTimePeriodWithConstraintAndStatistic(
                List.of(11L), true, true, true, now.minusDays(1), now, "sum");

        SensorAggregationResponseDTO dto = dtos.getFirst();

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
        when(repository.findBySensorIdAndTimestampBetween(eq(11L), any(), any())).thenReturn(Arrays.asList(r1, r2));

        List<SensorAggregationResponseDTO> dtos = service.getMetricsAndTimePeriodWithConstraintAndStatistic(
                List.of(11L), true, true, true, now.minusDays(1), now, "avg");

        SensorAggregationResponseDTO dto = dtos.getFirst();

        assertEquals(20.0, dto.getTemperatureMetric());
        assertEquals(30.0, dto.getHumidityMetric());
        assertEquals(10.0, dto.getWindSpeedMetric());
    }

    // Invalid statistic
    @Test
    void InvalidStatistic() {
        LocalDateTime now = LocalDateTime.now();
        SensorReading r1 = new SensorReading(1L, 11L, 10.0, 20.0, 5.0, now);
        when(repository.findBySensorIdAndTimestampBetween(eq(11L), any(), any())).thenReturn(Collections.singletonList(r1));

        assertThrows(SensorExceptions.MetricCalculationException.class,
                () -> service.getMetricsAndTimePeriodWithConstraintAndStatistic(List.of(11L), true, true, true, now.minusDays(1), now, "invalid"));
    }

    // invalid statistics data
    @Test
    void MetricListIsBad() {
        LocalDateTime now = LocalDateTime.now();
        SensorReading r1 = new SensorReading(1L, 11L, null, null, null, now);
        when(repository.findBySensorIdAndTimestampBetween(eq(11L), any(), any())).thenReturn(Collections.singletonList(r1));

        assertThrows(SensorExceptions.MetricCalculationException.class,
                () -> service.getMetricsAndTimePeriodWithConstraintAndStatistic(List.of(11L), true, true, true, now.minusDays(1), now, "avg"));
    }

    // only temperature flag
    @Test
    void returnsOnlyTemperatureFlag() {
        LocalDateTime now = LocalDateTime.now();
        SensorReading r1 = new SensorReading(1L, 11L, 10.0, 20.0, 5.0, now);
        when(repository.findBySensorIdAndTimestampBetween(eq(11L), any(), any())).thenReturn(Collections.singletonList(r1));

        List<SensorAggregationResponseDTO> dtos = service.getMetricsAndTimePeriodWithConstraintAndStatistic(List.of(11L), true, false, false, now.minusDays(1), now, "avg");
        SensorAggregationResponseDTO dto = dtos.getFirst();

        assertNotNull(dto.getTemperatureMetric());
        assertNull(dto.getHumidityMetric());
        assertNull(dto.getWindSpeedMetric());
    }

    // only Humidity flag
    @Test
    void returnsOnlyHumidityFlag() {
        LocalDateTime now = LocalDateTime.now();
        SensorReading r1 = new SensorReading(1L, 11L, 10.0, 20.0, 5.0, now);
        when(repository.findBySensorIdAndTimestampBetween(eq(11L), any(), any())).thenReturn(Collections.singletonList(r1));

        List<SensorAggregationResponseDTO> dtos = service.getMetricsAndTimePeriodWithConstraintAndStatistic(List.of(11L), false, true, false, now.minusDays(1), now, "avg");
        SensorAggregationResponseDTO dto = dtos.getFirst();

        assertNull(dto.getTemperatureMetric());
        assertNotNull(dto.getHumidityMetric());
        assertNull(dto.getWindSpeedMetric());
    }

    // only Wind flag
    @Test
    void returnsOnlyWindFlag() {
        LocalDateTime now = LocalDateTime.now();
        SensorReading r1 = new SensorReading(1L, 11L, 10.0, 20.0, 5.0, now);
        when(repository.findBySensorIdAndTimestampBetween(eq(11L), any(), any())).thenReturn(Collections.singletonList(r1));

        List<SensorAggregationResponseDTO> dtos = service.getMetricsAndTimePeriodWithConstraintAndStatistic(List.of(11L), false, false, true, now.minusDays(1), now, "avg");
        SensorAggregationResponseDTO dto = dtos.getFirst();

        assertNull(dto.getTemperatureMetric());
        assertNull(dto.getHumidityMetric());
        assertNotNull(dto.getWindSpeedMetric());
    }

    // returns temperature and humidity flag
    @Test
    void returnsMultipleMetricsWhenMultipleFlagsTrue() {
        LocalDateTime now = LocalDateTime.now();
        SensorReading r1 = new SensorReading(1L, 11L, 10.0, 20.0, 5.0, now);
        when(repository.findBySensorIdAndTimestampBetween(eq(11L), any(), any())).thenReturn(Collections.singletonList(r1));

        List<SensorAggregationResponseDTO> dtos = service.getMetricsAndTimePeriodWithConstraintAndStatistic(List.of(11L), true, true, false, now.minusDays(1), now, "avg");
        SensorAggregationResponseDTO dto = dtos.getFirst();

        assertNotNull(dto.getTemperatureMetric());
        assertNotNull(dto.getHumidityMetric());
        assertNull(dto.getWindSpeedMetric());
    }

    // check if inputs are set as null if false flag
    @Test
    void setsUnusedMetricsToNull() {
        LocalDateTime now = LocalDateTime.now();
        SensorReading r1 = new SensorReading(1L, 11L, 10.0, 20.0, 5.0, now);
        when(repository.findBySensorIdAndTimestampBetween(eq(11L), any(), any())).thenReturn(Collections.singletonList(r1));

        List<SensorAggregationResponseDTO> dtos = service.getMetricsAndTimePeriodWithConstraintAndStatistic(List.of(11L), true, false, false, now.minusDays(1), now, "avg");
        SensorAggregationResponseDTO dto = dtos.getFirst();

        assertNotNull(dto.getTemperatureMetric());
        assertNull(dto.getHumidityMetric());
        assertNull(dto.getWindSpeedMetric());
    }

    // multiple sensors
    @Test
    void getMetricsAndTimePeriodHandlesMultipleSensorIds() {
        LocalDateTime now = LocalDateTime.now();
        SensorReading r1 = new SensorReading(1L, 11L, 10.0, 20.0, 5.0, now);
        SensorReading r2 = new SensorReading(2L, 12L, 30.0, 40.0, 15.0, now);

        when(repository.findBySensorIdAndTimestampBetween(eq(11L), any(), any()))
                .thenReturn(Collections.singletonList(r1));
        when(repository.findBySensorIdAndTimestampBetween(eq(12L), any(), any()))
                .thenReturn(Collections.singletonList(r2));

        List<SensorAggregationResponseDTO> dtos = service.getMetricsAndTimePeriodWithConstraintAndStatistic(
                Arrays.asList(11L, 12L), true, true, true, now.minusDays(1), now, "avg");

        assertEquals(2, dtos.size());

        SensorAggregationResponseDTO dto1 = dtos.stream().filter(d -> d.getSensorId() == 11L).findFirst().orElseThrow();
        SensorAggregationResponseDTO dto2 = dtos.stream().filter(d -> d.getSensorId() == 12L).findFirst().orElseThrow();

        assertEquals(10.0, dto1.getTemperatureMetric());
        assertEquals(30.0, dto2.getTemperatureMetric());
    }

}
