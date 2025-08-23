package com.weather.sensor_service.Controller;

import com.weather.sensor_service.Exceptions.SensorExceptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// Whenever a custom exception is hit I return the exception to the user
@ControllerAdvice
public class GlobalExceptionHandler {


    // Save exception Response
    @ExceptionHandler(SensorExceptions.SensorSaveException.class)
    public ResponseEntity<Map<String, Object>> handleSaveException(SensorExceptions.SensorSaveException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put("Timestamp", LocalDateTime.now());
        body.put("error", "Sensor Save Error");
        body.put("message", exception.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    // Sensor not found exception response
    @ExceptionHandler(SensorExceptions.SensorNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(SensorExceptions.SensorNotFoundException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put("Timestamp", LocalDateTime.now());
        body.put("error", "Sensor Not Found");
        body.put("message", exception.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }


    // Calculation error exception response
    @ExceptionHandler(SensorExceptions.MetricCalculationException.class)
    public ResponseEntity<Map<String, Object>> handleMetricException(SensorExceptions.MetricCalculationException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put("Timestamp", LocalDateTime.now());
        body.put("Error", "Metric Calculation Error");
        body.put("Message", exception.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
