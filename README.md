# Overview 

This is a service which recieves weather data from various sensors which report metrics such as temperature, humidity and windspeed. 

The service offers many features : 
- Add a new sensor record to the database.
- Retrieve all readings for all sensors from the database.
- Retrieve all readings for a sensorId.
- Retrieve specific metrics for a sensorId.
- Retrieve all metrics for a sensorId between startDate and endDate.
- Retrieve specific metrics for a sensorId between startDate and endDate.
- Retrieve specific metrics for a sensorId between startDate and endDate using a specific statistic (MAX, MIN, AVG, SUM).

# Architecture
  
  ## Controllers
  - SensorController : Holds all endpoints for service.
  - GlobalExceptionController : When a custom exception is hit this controller returns the error message through a HTTP request back to the user.
 
  ## SensorReading Entity
  - Holds a SensorReading Object used to save and return readings to and from my database.
  - UUID, sensorId, timestamp, temperature, humidty, windSpeed.
 
  ## SensorAggregationResponseDTO
  - Request object for "/get-metrics-and-time-period-with-constraint"
 
  ## SensorExceptions
  - Custom exceptions for common errors which could occour.
  - Gracefully bubbles expception when triggered up to user.

  ## SensorReadingRepository
  - Uses JPA ORM to retrieve database rows and map them to objects.

  ## SensorService
  - Contains all business logic for service.

  ## SensorServiceApplicationTests
  - Holds all unit tests for the service.
   
    
