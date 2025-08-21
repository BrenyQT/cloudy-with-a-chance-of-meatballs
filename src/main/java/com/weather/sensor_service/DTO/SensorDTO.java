package com.weather.sensor_service.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class SensorDTO {

    private final Long   uuid;
    private final Double temperature;
    private final Double humidity;
    private final Double windSpeed;

    public SensorDTO(Long uuid, Double temperature, Double humidity, Double windSpeed) {
        this.uuid = uuid;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }

    // Getters
    public Long getUuid() {return uuid;}
    public Double getTemperature() { return temperature; }
    public Double getHumidity() { return humidity; }
    public Double getWindSpeed() { return windSpeed; }

}
