package com.weather.sensor_service.Repository;

import com.weather.sensor_service.Entity.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {

    // Find a particular sensor using Sensor ID
    List<SensorReading> findBySensorId(String sensorId);

    // Find list of MIN or MAX values for a particular sensor with a specific parameter (Humidity/Temperature/WindSpeed)



    // Finds the statistics of a single sensor between time periods.
    List<SensorReading> findBySensorIdAndTimestampBetween(
            String sensorId,
            LocalDateTime start,
            LocalDateTime end
    );


    // Finds the statistics of a multiple sensors between time periods.
    List<SensorReading> findBySensorIdInAndTimestampBetween(
            List<String> sensorIds,
            LocalDateTime start,
            LocalDateTime end
    );
}






