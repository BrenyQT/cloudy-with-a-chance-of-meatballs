package com.weather.sensor_service.Exceptions;

public class SensorExceptions {


    // Thrown if sensor cannot be saved
    public static class SensorSaveException extends RuntimeException {
        public SensorSaveException(String message) {
            super("SensorSaveException : " + message);
        }
    }


    // Thrown if sensor records are not found
    public static class SensorNotFoundException extends RuntimeException {
        public SensorNotFoundException(String message) {
            super("SensorNotFoundException : " + message);
        }
    }

    // Thrown if calculation failure on metrics
    public static class MetricCalculationException extends RuntimeException {
        public MetricCalculationException(String message) {
            super("MetricCalculationException : " + message);
        }
    }
}
