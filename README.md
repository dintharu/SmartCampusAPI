# Smart Campus Sensor & Room Management API

**Module:** 5COSC022W.2 Client-Server Architectures (2025/26)  
**Author:** K.G. Okitha Dintharu  
**Student ID:** w2120049 / 20240578  
**Institution:** Informatics Institute of Technology (IIT), affiliated with the University of Westminster (UK)

A JAX-RS (Jersey) RESTful web service for managing rooms, sensors, and sensor readings on a university smart-campus. Built in Java 8, deployed on Apache Tomcat 9, backed entirely by in-memory `ConcurrentHashMap` storage — no database, per coursework specification.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Technology Stack](#technology-stack)
3. [Project Structure](#project-structure)
4. [Seeded Sample Data](#seeded-sample-data)
5. [API Endpoints](#api-endpoints)
6. [HTTP Status Codes](#http-status-codes)
7. [How to Build and Run](#how-to-build-and-run)
8. [Sample curl Commands](#sample-curl-commands)
9. [Report — Answers to the 10 Coursework Questions](#report)

---

## Project Overview

This API exposes the Smart Campus domain through five cohesive feature areas:

- **Discovery / HATEOAS index** — `GET /api/v1` returns a self-describing JSON payload with links to every primary resource
- **Rooms** — full CRUD with referential-integrity protection on delete
- **Sensors** — full CRUD, type filtering via `?type=`, linked-resource validation against the Rooms store
- **Sensor Readings** — modelled as a JAX-RS sub-resource of `/sensors/{id}/readings`, with per-sensor history and a `currentValue` side-effect on the parent sensor
- **Error handling** — five custom exceptions each mapped to a precise HTTP status with a structured `ErrorMessage` JSON body, plus a catch-all `GlobalExceptionMapper` that hides stack traces from clients

A cross-cutting `LoggingFilter` records every request and response along with elapsed time.

---

## Technology Stack

| Layer | Technology | Version |
|---|---|---|
| Language | Java | 8 (source/target) |
| JAX-RS Implementation | Jersey | 2.35 |
| JSON Serialization | Jackson | 2.13.4 |
| Build Tool | Apache Maven | 3.9.x |
| Web Server | Apache Tomcat | 9.0.100 |
| Storage | In-memory `ConcurrentHashMap` + `CopyOnWriteArrayList` (thread-safe singletons) | — |
| Package namespace | `javax.*` (not `jakarta.*`) | — |

---

## Project Structure
SmartCampusAPI/
├── pom.xml
├── README.md
├── src/main/webapp/WEB-INF/web.xml
├── src/main/webapp/META-INF/context.xml
└── src/main/java/com/smartcampus/w2120049_okithadintharu_5cosc022w/
├── ApiApplication.java                 # @ApplicationPath("/api/v1")
├── AppBootstrap.java                   # @WebListener — seeds sample data at startup
├── model/
│   ├── Room.java
│   ├── Sensor.java
│   ├── SensorReading.java
│   └── ErrorMessage.java               # structured JSON error DTO
├── repository/
│   ├── RoomRepository.java             # ConcurrentHashMap singleton
│   ├── SensorRepository.java           # ConcurrentHashMap singleton
│   └── SensorReadingRepository.java    # Map<sensorId, CopyOnWriteArrayList<Reading>>
├── exception/
│   ├── ResourceNotFoundException.java
│   ├── RoomNotEmptyException.java
│   ├── LinkedResourceNotFoundException.java
│   ├── SensorUnavailableException.java
│   ├── InvalidInputException.java
│   └── mapper/
│       ├── ResourceNotFoundExceptionMapper.java        # → 404
│       ├── RoomNotEmptyExceptionMapper.java            # → 409
│       ├── LinkedResourceNotFoundExceptionMapper.java  # → 422
│       ├── SensorUnavailableExceptionMapper.java       # → 403
│       ├── InvalidInputExceptionMapper.java            # → 400
│       └── GlobalExceptionMapper.java                  # → 500 catch-all
├── resource/
│   ├── DiscoveryResource.java          # @Path("/")   HATEOAS index
│   ├── RoomResource.java               # /rooms       CRUD + orphan block
│   ├── SensorResource.java             # /sensors     CRUD + ?type filter + sub-resource locator
│   └── SensorReadingResource.java      # sub-resource returned by the locator
└── filter/
└── LoggingFilter.java              # ContainerRequest + ContainerResponse filters

---

## Seeded Sample Data

On deployment, `AppBootstrap` (a `@WebListener`) seeds the in-memory stores with realistic data so graders and API consumers see content immediately — no POSTs required before any GETs return something meaningful.

**Rooms (3):**

| ID | Name | Capacity | Sensors |
|---|---|---|---|
| `LIB-301` | Library Quiet Study | 40 | TEMP-001, OCC-001, CO2-001 |
| `LEC-12` | Lecture Theatre 12 | 120 | CO2-002, OCC-002 |
| `LAB-5` | Robotics Lab | 25 | *(none — deliberate for DELETE demo)* |

**Sensors (5):**

| ID | Type | Status | Current Value | Room |
|---|---|---|---|---|
| `TEMP-001` | Temperature | ACTIVE | 21.5 | LIB-301 |
| `OCC-001` | Occupancy | ACTIVE | 18 | LIB-301 |
| `CO2-001` | CO2 | ACTIVE | 612 | LIB-301 |
| `CO2-002` | CO2 | **MAINTENANCE** | 0 | LEC-12 *(used to demo 403 Forbidden)* |
| `OCC-002` | Occupancy | ACTIVE | 87 | LEC-12 |

**Readings (3):** three historic temperature readings attached to `TEMP-001` spanning the last 10 minutes.

---

## API Endpoints

Base URL when running locally: `http://localhost:8080/SmartCampusAPI/api/v1`

| Method | Path | Purpose | Success |
|---|---|---|---|
| `GET` | `/` | Discovery / HATEOAS index | 200 |
| `GET` | `/rooms` | List all rooms | 200 |
| `GET` | `/rooms/{id}` | Get one room | 200 |
| `POST` | `/rooms` | Create a room | 201 + Location |
| `DELETE` | `/rooms/{id}` | Delete a room (fails if sensors linked) | 204 |
| `GET` | `/sensors` | List all sensors | 200 |
| `GET` | `/sensors?type=CO2` | Filter sensors by type (case-insensitive) | 200 |
| `GET` | `/sensors/{id}` | Get one sensor | 200 |
| `POST` | `/sensors` | Register a sensor (validates `roomId` link) | 201 + Location |
| `DELETE` | `/sensors/{id}` | Decommission a sensor | 204 |
| `GET` | `/sensors/{id}/readings` | Reading history (via sub-resource) | 200 |
| `POST` | `/sensors/{id}/readings` | Append a reading; rejected if sensor MAINTENANCE | 201 |

---

## HTTP Status Codes

Every error is returned as structured JSON: `{ "status": <int>, "error": "<reason phrase>", "message": "<human-readable>" }`.

| Code | Meaning | When this API uses it |
|---|---|---|
| 200 | OK | Successful GET |
| 201 | Created | Successful POST (with Location header) |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | `InvalidInputException` — missing/empty required field |
| 403 | Forbidden | `SensorUnavailableException` — sensor in MAINTENANCE |
| 404 | Not Found | `ResourceNotFoundException` — room or sensor ID does not exist |
| 409 | Conflict | `RoomNotEmptyException` — DELETE room that still has sensors |
| 415 | Unsupported Media Type | Request body is not `application/json` (enforced by `@Consumes`) |
| 422 | Unprocessable Entity | `LinkedResourceNotFoundException` — valid payload but `roomId` doesn't exist |
| 500 | Internal Server Error | `GlobalExceptionMapper` catch-all — stack trace hidden, logged server-side only |

---

## How to Build and Run

### Prerequisites

- JDK 8 or newer (source/target is Java 8, but the code runs on later JVMs)
- Apache Maven 3.6 or newer
- Apache Tomcat 9.0.x
- (Optional) Apache NetBeans 17+ for IDE workflow

### Build

```bash
cd SmartCampusAPI
mvn clean package
```

This produces `target/SmartCampusAPI.war`.

### Deploy manually

Copy the WAR into Tomcat's `webapps/` directory and start Tomcat:

```bash
# Linux / macOS
cp target/SmartCampusAPI.war $CATALINA_HOME/webapps/
$CATALINA_HOME/bin/startup.sh

# Windows
copy target\SmartCampusAPI.war %CATALINA_HOME%\webapps\
%CATALINA_HOME%\bin\startup.bat
```

### Deploy via NetBeans

1. Open the project in NetBeans.
2. Right-click the project → **Properties** → **Run** → set **Server** to `Apache Tomcat or TomEE`.
3. Right-click the project → **Run**. NetBeans will build, deploy, start Tomcat, and open a browser automatically.

### Verify it's running

```bash
curl http://localhost:8080/SmartCampusAPI/api/v1
```

You should see the Discovery JSON with a `_links` map pointing to `/rooms` and `/sensors`. A successful startup is also confirmed in Tomcat's log by the message:

---

## Sample curl Commands

### 1. Discovery endpoint (HATEOAS)

```bash
curl http://localhost:8080/SmartCampusAPI/api/v1
```

### 2. List all rooms

```bash
curl http://localhost:8080/SmartCampusAPI/api/v1/rooms
```

### 3. Create a new room (201 + Location header)

```bash
curl -i -X POST -H "Content-Type: application/json" \
  -d '{"id":"ENG-101","name":"Engineering Auditorium","capacity":200,"sensorIds":[]}' \
  http://localhost:8080/SmartCampusAPI/api/v1/rooms
```

### 4. Filter sensors by type (case-insensitive)

```bash
curl "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=CO2"
```

### 5. Register a sensor with valid roomId

```bash
curl -X POST -H "Content-Type: application/json" \
  -d '{"id":"TEMP-002","type":"Temperature","status":"ACTIVE","currentValue":22.5,"roomId":"LEC-12"}' \
  http://localhost:8080/SmartCampusAPI/api/v1/sensors
```

### 6. Register a sensor with INVALID roomId (→ 422 Unprocessable Entity)

```bash
curl -i -X POST -H "Content-Type: application/json" \
  -d '{"id":"FAIL-1","type":"Temperature","status":"ACTIVE","roomId":"FAKE-ROOM"}' \
  http://localhost:8080/SmartCampusAPI/api/v1/sensors
```

### 7. Delete a room with linked sensors (→ 409 Conflict)

```bash
curl -i -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301
```

### 8. Post a reading to TEMP-001 (updates the sensor's currentValue as a side-effect)

```bash
curl -X POST -H "Content-Type: application/json" \
  -d '{"value":23.7}' \
  http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings
```

### 9. Post a reading to a MAINTENANCE sensor (→ 403 Forbidden)

```bash
curl -i -X POST -H "Content-Type: application/json" \
  -d '{"value":450}' \
  http://localhost:8080/SmartCampusAPI/api/v1/sensors/CO2-002/readings
```

### 10. Read history for a sensor

```bash
curl http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings
```

---

## Report

Detailed answers to the 10 coursework questions are provided in the next section below, each grounded in references to the specific classes and line-level design choices in this repository.
