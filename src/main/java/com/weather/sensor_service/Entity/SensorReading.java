package com.weather.sensor_service.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_readings")
public class SensorReading {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // ID of Sensor scan.
    private Long id;

    // ID of sensor which has scanned.
    private Long sensorId;

    // Time in which scan is submitted
    private LocalDateTime timestamp;

    // Temperature amount
    private Double temperature;

    // Humidity amount
    private Double humidity;

    // WindSpeed amount
    private Double windSpeed;

    public SensorReading() {

    }

    public SensorReading(Long id, Long sensorId, Double temperature, Double humidity, Double windSpeed, LocalDateTime timestamp) {
        this.id = id;
        this.sensorId = sensorId;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.timestamp = timestamp;
    }

    // GETTERS

    public Long getId() {
        return id;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public Double getHumidity() {
        return humidity;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Long getSensorId() {
        return sensorId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }


    // SETTERS

    public void setId(Long id) {
        this.id = id;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setSensorId(Long sensorId) {
        this.sensorId = sensorId;
    }
}
