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
    private String sensorId;

    // Time in which scan is submitted
    private LocalDateTime timestamp;

    // Temperature amount
    private Double temperature;

    // Humidity amount
    private Double humidity;

    // WindSpeed amount
    private Double windSpeed;


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

    public String getSensorId() {
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

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }
}
