package com.weather.sensor_service.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class SensorAggregationResponseDTO {

    private final Long sensorId;
    private final String metricName;
    private  Double temperatureMetric;
    private  Double windSpeedMetric;
    private  Double humidityMetric;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;


    public SensorAggregationResponseDTO(Long sensorId, String metricName,  double temperatureMetric, double windSpeedMetric, double humidityMetric, LocalDateTime startDate, LocalDateTime endDate) {
        this.sensorId = sensorId;
        this.metricName = metricName;
        this.temperatureMetric = temperatureMetric;
        this.windSpeedMetric = windSpeedMetric;
        this.humidityMetric = humidityMetric;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters
    public Long getSensorId() {return sensorId;}
    public String getMetricName() {return metricName;}

    public Double getTemperatureMetric() {
        return temperatureMetric;
    }

    public Double getWindSpeedMetric() {
        return windSpeedMetric;
    }

    public Double getHumidityMetric() {
        return humidityMetric;
    }
    public LocalDateTime getStartDate() {
        return startDate;
    }
    public LocalDateTime getEndDate() {
        return endDate;
    }

    // Setters
    public void setTemperatureMetric(Double temperatureMetric) {
        this.temperatureMetric = temperatureMetric;
    }

    public void setHumidityMetric(Double humidityMetric) {
        this.humidityMetric = humidityMetric;
    }

    public void setWindSpeedMetric(Double windSpeedMetric) {
        this.windSpeedMetric = windSpeedMetric;
    }
}
