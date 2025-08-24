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
  - Request object for "sensors/get-metrics-and-time-period-with-constraint"
 
  ## SensorExceptions
  - Custom exceptions for common errors which could occour.
  - Gracefully bubbles expception when triggered up to user.

  ## SensorReadingRepository
  - Uses JPA ORM to retrieve database rows and map them to objects.

  ## SensorService
  - Contains all business logic for service.

  ## SensorServiceApplicationTests
  - Holds all unit tests for the service.

  ## Database 
  - PostgreSQL
  - Spring JPA used to map objects (No SQL Queries)

  ## Dependancies 
  - Gradle : Dependancy Manager. 
  - PostgreSQL : Service Database.
  - H2 : in memory database for unit tests.
  - Spring boot JPA : Spring boot ORM.
  - Spring boot WEB : allows me to create RESTful API's
   
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

## Highlights 
Lock : Maintains data integrity for concurrent requests. 
<img width="628" height="269" alt="image" src="https://github.com/user-attachments/assets/97456d3d-8367-46ee-add5-350dc91ddf01" />


## sensors/get-all/
- Returns all readings in the database.

Happy Path.
<img width="1381" height="879" alt="image" src="https://github.com/user-attachments/assets/ab2ca8c7-3dbe-4371-804d-5aecf3912821" />

No Records in database.
<img width="1387" height="877" alt="image" src="https://github.com/user-attachments/assets/0c9fe69f-de91-499b-b718-1ca3d31f9c90" />

## Highlights 
Uses Spring JPA (No SQL needed).
<img width="786" height="258" alt="image" src="https://github.com/user-attachments/assets/512e91a2-8346-480c-b01d-7b5db5107c2b" />


## sensors/get-metrics-and-time-period/
- Returns sensorReadings with specific metrics within a time span.

Happy Path.
<img width="1388" height="883" alt="image" src="https://github.com/user-attachments/assets/f9751f18-8b3f-4626-8710-f1aaf582939d" />

One metric selected.
<img width="1388" height="880" alt="image" src="https://github.com/user-attachments/assets/02171855-38a1-4707-aee6-6136025ed4d0" />

No time span selected (All time).
<img width="1392" height="879" alt="image" src="https://github.com/user-attachments/assets/bc087405-f828-4dc8-aca5-7a50ff9cc4c7" />

No records available.
<img width="1391" height="883" alt="image" src="https://github.com/user-attachments/assets/4527e442-f2cf-4f60-a915-c8ae0632ac3d" />

## Highlights 

Retreives all readings between specific time. 
<img width="1034" height="412" alt="image" src="https://github.com/user-attachments/assets/10aa2da0-6ebd-4bc0-ae47-559519cf530d" />

Returns sensorId selected metrics for specified time period. 
<img width="1230" height="554" alt="image" src="https://github.com/user-attachments/assets/ec68d5f9-910e-4d9f-a9b9-6d2ad83b3eb4" />

## sensors/get-metrics-and-time-period-with-constraint/

Happy Path (Multiple Sensors).
<img width="1387" height="880" alt="image" src="https://github.com/user-attachments/assets/296c87bd-88b7-4362-9653-da3809affe43" />

Happy Path (Single Sensor).
<img width="1384" height="877" alt="image" src="https://github.com/user-attachments/assets/a4edf616-92a0-4675-a441-dbd5c223a815" />

Only two metrics passed (no statistic).
<img width="1387" height="880" alt="image" src="https://github.com/user-attachments/assets/e942faad-8a15-4eb2-9843-5ccf066ce14e" />

No statistic or timespan (All time, one metric).
<img width="1390" height="874" alt="image" src="https://github.com/user-attachments/assets/1e14e7e2-9dc5-47af-b40f-923f875999ba" />

No records available. 
<img width="1389" height="871" alt="image" src="https://github.com/user-attachments/assets/35af7c20-f45b-42d0-8a4b-c9053d0fa6af" />

## Highlights 

Validates and normalises time.
<img width="958" height="379" alt="image" src="https://github.com/user-attachments/assets/2e2c9a77-377e-443a-818c-42485a60d633" />

Switch case used to choose which statistic is calculated. 
<img width="953" height="510" alt="image" src="https://github.com/user-attachments/assets/c37430c2-0b4c-42f4-927f-0c059da5f92c" />

If the metric is set as true, calculate.

<img width="712" height="822" alt="image" src="https://github.com/user-attachments/assets/f5b3cb6d-5ff7-4d50-9b75-b04b229e9e33" />









