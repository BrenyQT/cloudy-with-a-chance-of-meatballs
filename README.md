# Overview

This is a service which recieves weather data from various sensors which report metrics such as temperature, humidity and windspeed. 

The service offers many features : 
- Add a new sensor record to the database.
- Retrieve all readings for all sensors from the database.
- Retrieve specific metrics for a sensorId between startDate and endDate.
- Retrieve specific metrics for a list of sensorId's between startDate and endDate using a specific statistic (MAX, MIN, AVG, SUM).

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

  ## Dependancies 
  - Gradle : Dependancy Manager. 
  - PostgreSQL : Service Database.
  - H2 : in memory database for unit tests.
  - Spring boot JPA - Spring boot ORM.
  - Spring boot WEB - allows me to create RESTful API's
   
# Testing

## Build and Test Pipeline 
- Github workflow setup to simulate building and testing the entire service.
- A PR cannot be merged if pipeline fails.

## Unit Tests (SensorServiceApplicationTests)
- Run everytime the project is built (tests servicelayer)
- Tests setup to test the service layer of the service

## Postman API Testing 
- Enpoint testing to check Happy Path and exception bubbling. 

# Endpoints 
(Notice exception bubling on failing requests)

## sensors/create-reading/
- Stores a new sensor reading to database.
  
Happy Path. 
  <img width="1383" height="873" alt="image" src="https://github.com/user-attachments/assets/7d0cab2c-9efd-4356-9bd7-ef10cf7eb544" />
  
No sensorId provided.
  <img width="1390" height="878" alt="image" src="https://github.com/user-attachments/assets/b89baba2-5650-4959-8c12-a03367be1329" />
  
Missing a metric.
  <img width="1390" height="878" alt="image" src="https://github.com/user-attachments/assets/f3c66bca-17f2-4cc1-8880-c4db3d3eb3cd" />

## sensors/get-all/
- Returns all readings in the database.

Happy Path.
<img width="1381" height="879" alt="image" src="https://github.com/user-attachments/assets/ab2ca8c7-3dbe-4371-804d-5aecf3912821" />

No Records in database.
<img width="1387" height="877" alt="image" src="https://github.com/user-attachments/assets/0c9fe69f-de91-499b-b718-1ca3d31f9c90" />

## sensors/get-metrics-and-time-period/
- Returns sensorReadings with specific metrics within a time span

Happy Path.
<img width="1388" height="883" alt="image" src="https://github.com/user-attachments/assets/f9751f18-8b3f-4626-8710-f1aaf582939d" />

One metric selected
<img width="1388" height="880" alt="image" src="https://github.com/user-attachments/assets/02171855-38a1-4707-aee6-6136025ed4d0" />

No time span selected (All time)
<img width="1392" height="879" alt="image" src="https://github.com/user-attachments/assets/bc087405-f828-4dc8-aca5-7a50ff9cc4c7" />

No records available
<img width="1391" height="883" alt="image" src="https://github.com/user-attachments/assets/4527e442-f2cf-4f60-a915-c8ae0632ac3d" />

## sensors/get-metrics-and-time-period-with-constraint/
No records available 
<img width="1389" height="871" alt="image" src="https://github.com/user-attachments/assets/35af7c20-f45b-42d0-8a4b-c9053d0fa6af" />





