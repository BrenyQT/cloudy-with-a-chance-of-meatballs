package com.weather.sensor_service.Repository;

import com.weather.sensor_service.Entity.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {

    List<SensorReading> findBySensorId(Long sensorId);

    List<SensorReading> findBySensorIdAndTimestampBetween(Long sensorId, LocalDateTime startDate, LocalDateTime endDate);


}






