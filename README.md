# Smart Campus Sensor & Room Management API

> JAX-RS (Jersey) RESTful web service for managing rooms, sensors, and sensor-reading history on a university smart campus.

![Module](https://img.shields.io/badge/Module-5COSC022W.2-blue)
![Language](https://img.shields.io/badge/Java-8-orange)
![Server](https://img.shields.io/badge/Tomcat-9.0.100-yellow)
![JAX-RS](https://img.shields.io/badge/Jersey-2.35-brightgreen)

---

**Module:** 5COSC022W.2 Client-Server Architectures (2025/26)  
**Author:** K.G. Okitha Dintharu  
**UoW ID:** w2120049  
**IIT ID:** 20240578  
**Institution:** Informatics Institute of Technology (IIT), affiliated with the University of Westminster (UK)  
**Submission Date:** 24 April 2026

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Technology Stack](#2-technology-stack)
3. [Project Structure](#3-project-structure)
4. [Seeded Sample Data](#4-seeded-sample-data)
5. [API Endpoints](#5-api-endpoints)
6. [HTTP Status Codes](#6-http-status-codes)
7. [How to Build and Run](#7-how-to-build-and-run)
8. [Sample curl Commands](#8-sample-curl-commands)
9. [Report — Answers to Coursework Questions](#9-report--answers-to-coursework-questions)

---

## 1. Project Overview

This API exposes the Smart Campus domain through five cohesive feature areas:

- **Discovery / HATEOAS index** — `GET /api/v1` returns a self-describing JSON payload with links to every primary resource
- **Rooms** — full CRUD with referential-integrity protection on delete
- **Sensors** — full CRUD, type filtering via `?type=`, linked-resource validation against the Rooms store
- **Sensor Readings** — modelled as a JAX-RS sub-resource of `/sensors/{id}/readings`, with per-sensor history and a `currentValue` side-effect on the parent sensor
- **Error handling** — five custom exceptions each mapped to a precise HTTP status with a structured `ErrorMessage` JSON body, plus a catch-all `GlobalExceptionMapper` that hides stack traces from clients

A cross-cutting `LoggingFilter` records every request and response along with elapsed time.

All data is stored in-memory using `ConcurrentHashMap` and `CopyOnWriteArrayList` — no database technology is used, in line with the coursework specification.

---

## 2. Technology Stack

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

## 3. Project Structure

```
SmartCampusAPI/
├── pom.xml
├── README.md
├── Report.pdf
├── src/
│   └── main/
│       ├── webapp/
│       │   ├── WEB-INF/
│       │   │   └── web.xml
│       │   └── META-INF/
│       │       └── context.xml
│       └── java/
│           └── com/smartcampus/w2120049_okithadintharu_5cosc022w/
│               ├── ApiApplication.java
│               ├── AppBootstrap.java
│               │
│               ├── model/
│               │   ├── Room.java
│               │   ├── Sensor.java
│               │   ├── SensorReading.java
│               │   └── ErrorMessage.java
│               │
│               ├── repository/
│               │   ├── RoomRepository.java
│               │   ├── SensorRepository.java
│               │   └── SensorReadingRepository.java
│               │
│               ├── exception/
│               │   ├── ResourceNotFoundException.java
│               │   ├── RoomNotEmptyException.java
│               │   ├── LinkedResourceNotFoundException.java
│               │   ├── SensorUnavailableException.java
│               │   ├── InvalidInputException.java
│               │   └── mapper/
│               │       ├── ResourceNotFoundExceptionMapper.java
│               │       ├── RoomNotEmptyExceptionMapper.java
│               │       ├── LinkedResourceNotFoundExceptionMapper.java
│               │       ├── SensorUnavailableExceptionMapper.java
│               │       ├── InvalidInputExceptionMapper.java
│               │       └── GlobalExceptionMapper.java
│               │
│               ├── resource/
│               │   ├── DiscoveryResource.java
│               │   ├── RoomResource.java
│               │   ├── SensorResource.java
│               │   └── SensorReadingResource.java
│               │
│               └── filter/
│                   └── LoggingFilter.java
```

### Key Files at a Glance

| File | Purpose |
|---|---|
| `ApiApplication.java` | `@ApplicationPath("/api/v1")` — JAX-RS entry point |
| `AppBootstrap.java` | `@WebListener` — seeds sample data on startup |
| `DiscoveryResource.java` | `@Path("/")` — HATEOAS discovery index |
| `RoomResource.java` | `/rooms` CRUD + orphan block on DELETE |
| `SensorResource.java` | `/sensors` CRUD + `?type` filter + sub-resource locator |
| `SensorReadingResource.java` | `/sensors/{id}/readings` — sub-resource returned by the locator |
| `LoggingFilter.java` | Request + response filter for request/response logging |
| `GlobalExceptionMapper.java` | Catch-all — hides stack traces from clients |

---

## 4. Seeded Sample Data

On deployment, `AppBootstrap` (a `@WebListener`) seeds the in-memory stores with realistic data so graders and API consumers see content immediately — no POSTs required before any GETs return something meaningful.

### Rooms (3)

| ID | Name | Capacity | Sensors |
|---|---|---|---|
| `LIB-301` | Library Quiet Study | 40 | TEMP-001, OCC-001, CO2-001 |
| `LEC-12` | Lecture Theatre 12 | 120 | CO2-002, OCC-002 |
| `LAB-5` | Robotics Lab | 25 | *(none — deliberate for DELETE demo)* |

### Sensors (5)

| ID | Type | Status | Current Value | Room |
|---|---|---|---|---|
| `TEMP-001` | Temperature | ACTIVE | 21.5 | LIB-301 |
| `OCC-001` | Occupancy | ACTIVE | 18 | LIB-301 |
| `CO2-001` | CO2 | ACTIVE | 612 | LIB-301 |
| `CO2-002` | CO2 | **MAINTENANCE** | 0 | LEC-12 *(used to demo 403 Forbidden)* |
| `OCC-002` | Occupancy | ACTIVE | 87 | LEC-12 |

### Readings (3)

Three historic temperature readings attached to `TEMP-001` spanning the last 10 minutes.

---

## 5. API Endpoints

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

## 6. HTTP Status Codes

Every error is returned as structured JSON:

```json
{
    "status": 404,
    "error": "Not Found",
    "message": "Room with id 'XYZ' does not exist."
}
```

| Code | Meaning | When this API uses it |
|---|---|---|
| 200 | OK | Successful GET |
| 201 | Created | Successful POST (with `Location` header) |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | `InvalidInputException` — missing / empty required field |
| 403 | Forbidden | `SensorUnavailableException` — sensor in MAINTENANCE |
| 404 | Not Found | `ResourceNotFoundException` — room or sensor ID does not exist |
| 409 | Conflict | `RoomNotEmptyException` — DELETE room that still has sensors |
| 415 | Unsupported Media Type | Request body is not `application/json` (enforced by `@Consumes`) |
| 422 | Unprocessable Entity | `LinkedResourceNotFoundException` — valid payload but `roomId` doesn't exist |
| 500 | Internal Server Error | `GlobalExceptionMapper` catch-all — stack trace hidden, logged server-side only |

---

## 7. How to Build and Run

### Prerequisites

- **JDK 8** or newer (source/target is Java 8, but the code runs on later JVMs)
- **Apache Maven** 3.6 or newer
- **Apache Tomcat** 9.0.x
- *(Optional)* **Apache NetBeans** 17+ for IDE workflow

### Option A: Command line build and deploy

```bash
# 1. Clone the repository
git clone https://github.com/dintharu/SmartCampusAPI.git
cd SmartCampusAPI

# 2. Build the WAR
mvn clean package
# Produces target/SmartCampusAPI.war

# 3. Deploy to Tomcat (Linux / macOS)
cp target/SmartCampusAPI.war $CATALINA_HOME/webapps/
$CATALINA_HOME/bin/startup.sh

# 3. Deploy to Tomcat (Windows)
copy target\SmartCampusAPI.war %CATALINA_HOME%\webapps\
%CATALINA_HOME%\bin\startup.bat
```

### Option B: NetBeans workflow

1. **File** → **Open Project** → select the `SmartCampusAPI` folder.
2. Right-click the project → **Properties** → **Run** → set **Server** to **Apache Tomcat or TomEE** → **OK**.
3. Right-click the project → **Clean and Build** — expect `BUILD SUCCESS`.
4. Right-click the project → **Run** — NetBeans will deploy the WAR and start Tomcat automatically.

### Verifying the server started correctly

Once deployed, the Tomcat log should show:

```
INFO: SmartCampusAPI seeded successfully: 3 rooms, 5 sensors.
```

Open a browser (or curl) and hit the discovery endpoint:

```bash
curl http://localhost:8080/SmartCampusAPI/api/v1
```

You should see the Discovery JSON with a `_links` map pointing to `/rooms` and `/sensors`.

---

## 8. Sample curl Commands

### 1. Discovery endpoint (HATEOAS)

```bash
curl http://localhost:8080/SmartCampusAPI/api/v1
```

### 2. List all rooms

```bash
curl http://localhost:8080/SmartCampusAPI/api/v1/rooms
```

### 3. Create a new room (201 Created + Location header)

```bash
curl -i -X POST -H "Content-Type: application/json" \
  -d '{"id":"ENG-101","name":"Engineering Auditorium","capacity":200,"sensorIds":[]}' \
  http://localhost:8080/SmartCampusAPI/api/v1/rooms
```

### 4. Filter sensors by type (case-insensitive)

```bash
curl "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=CO2"
```

### 5. Register a sensor with valid roomId (201 Created)

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

## 9. Report — Answers to Coursework Questions

The following answers correspond to the questions posed in the coursework specification, one per task section. Each answer is grounded in the specific classes and design decisions made in this repository.

> The same answers are also provided in the accompanying `Report.pdf` file at the repository root.

---

### Part 1.1 — JAX-RS Resource Lifecycle and Data Synchronization

> **Question.** *Explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures to prevent data loss or race conditions.*

The JAX-RS specification defines resource classes as **per-request scoped by default**. Every time Jersey receives an HTTP request matching one of my resources — for example `RoomResource`, `SensorResource`, or `SensorReadingResource` — it instantiates a fresh object, invokes the matched method, serialises the response, then allows that instance to be garbage-collected. A second request, even from the same client milliseconds later, receives a brand-new instance with no memory of the previous one.

This lifecycle has a direct and non-obvious consequence for in-memory state: **anything stored as an instance field on a resource class is effectively request-local**. If I had declared `private Map<String, Room> rooms = new HashMap<>();` on `RoomResource`, a POST to `/rooms` would populate that instance's map, but the next GET — serviced by a freshly-constructed `RoomResource` — would see an empty map. Every write would appear to vanish immediately.

To solve this, I moved the storage into three **singleton repositories** that live for the lifetime of the web application rather than the lifetime of a single request: `RoomRepository`, `SensorRepository`, and `SensorReadingRepository`. Each one uses the classic eager-initialisation pattern:

```java
private static final RoomRepository INSTANCE = new RoomRepository();
private RoomRepository() { }
public static RoomRepository getInstance() { return INSTANCE; }
```

The private constructor prevents external instantiation; the `INSTANCE` field is initialised once, by the JVM class loader, in a thread-safe manner. Every resource class that needs access calls `RoomRepository.getInstance()` in a field initialiser — so regardless of which request-scoped resource instance is handling the current call, they all read from and write to the same underlying map.

The second half of the challenge is **concurrency**. Tomcat handles multiple HTTP requests in parallel, each on its own worker thread. Two simultaneous POSTs to `/rooms` could both try to mutate the same map at the same instant. A plain `HashMap` would corrupt its internal structure under this kind of contention — in the worst case looping infinitely, in the usual case silently losing entries. My repositories therefore use `ConcurrentHashMap`, which is engineered for exactly this scenario: atomic `put`, `get`, and `remove`, non-blocking reads, and fine-grained locking on writes. For `SensorReadingRepository`, which holds a per-sensor list of readings, I use a `Map<String, CopyOnWriteArrayList<SensorReading>>` — the inner `CopyOnWriteArrayList` handles concurrent appends to the history without requiring external synchronisation.

The coursework specification permits only `HashMap` and `ArrayList` family data structures. `ConcurrentHashMap` and `CopyOnWriteArrayList` are concurrent implementations within those families (they implement `Map` and `List` respectively), so they fit the allowed toolkit while correctly addressing the race-condition risks that arise from JAX-RS's per-request lifecycle.

---

### Part 1.2 — HATEOAS and Hypermedia as the Engine of Application State

> **Question.** *Why is the provision of hypermedia (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?*

HATEOAS — "Hypermedia As The Engine Of Application State" — is the principle that a REST response should carry, as data, the links a client needs to continue navigating the API. It is the difference between handing a visitor a street address on paper (static documentation) versus handing them a live GPS that recomputes directions as conditions change (HATEOAS).

My `DiscoveryResource` implements this principle at the API's entry point. A `GET /api/v1` returns a JSON document that is intentionally more than a version banner: it includes a `_links` object containing absolute URLs for every primary resource collection in the system:

```java
Map<String, String> links = new LinkedHashMap<>();
links.put("self",    uriInfo.getAbsolutePath().toString());
links.put("rooms",   uriInfo.getBaseUriBuilder().path("rooms").build().toString());
links.put("sensors", uriInfo.getBaseUriBuilder().path("sensors").build().toString());
```

Crucially, those URLs are not hard-coded — they are derived at runtime from Jersey's injected `UriInfo`, which reflects the **current** deployment's scheme, host, port, and base path. If the API is redeployed from a developer laptop to a cloud host with a different hostname, the discovery document automatically reflects the new URLs without any code change.

Three concrete benefits flow from this:

1. **Reduced coupling.** A client that follows the links it receives doesn't need to know that rooms live at `/rooms` — it just needs to know to look up the `"rooms"` key in the discovery document. If I later reorganise the URLs (say, to `/api/v1/facilities/rooms`), clients that follow links continue to function; clients that hard-coded paths break.

2. **Self-documenting API.** Static documentation goes out of date the moment the code changes. The discovery endpoint **is** the documentation; it cannot drift from reality because it is generated from the same code that serves the requests.

3. **Lower onboarding cost for client developers.** A new developer pointing their tool at `GET /api/v1` gets a map of the entire API in one response. They don't need to dig through a PDF or a Confluence page to find out what's available.

The cost is additional payload size and a small amount of server-side URL-building work. For a discovery endpoint that is hit sparingly, this cost is negligible; the benefit to maintainability is significant.

---

### Part 2.1 — Returning IDs vs. Full Resource Objects in Collections

> **Question.** *When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client-side processing.*

This is a classic bandwidth-vs-round-trip trade-off. I chose to return **full room objects** from `GET /rooms` in `RoomResource.listAllRooms()`:

```java
List<Room> rooms = roomRepository.findAll();
return Response.ok(rooms).build();
```

Returning only IDs would produce a smaller response (just a `["LIB-301","LEC-12","LAB-5"]` array) but triggers the well-known **N+1 request problem**: any client that actually needs to render the rooms has to follow up with N individual `GET /rooms/{id}` calls, one per ID. For three rooms that is four round trips (one for the list, three for details). For three hundred rooms it is 301 round trips — cripplingly slow on a high-latency mobile connection, and much more server CPU than strictly necessary.

Returning full objects is the right default for this API for two domain-specific reasons:

- The Smart Campus use case is **dashboard-oriented**: the primary consumer is a facilities-management UI that needs name, capacity, and sensor assignments for every room on a single page. Giving back IDs would force every dashboard page-load to issue dozens of additional requests, negating the benefits of caching and compounding network latency.
- Room objects are **small** — three or four fields and a short ID list — so the payload size penalty is modest compared to the request-overhead savings.

The approach is not without costs, which I would mitigate in a production variant:

- **Pagination** for genuinely large collections (hundreds of rooms) would keep the payload bounded.
- **Partial responses / field selection** (e.g. `?fields=id,name`) would let bandwidth-conscious clients opt in to smaller payloads.
- **Conditional requests** (`ETag` + `If-None-Match`) would let clients skip re-downloading unchanged lists.

For a campus with a few dozen rooms, the "full objects by default" model delivers the best developer experience while staying honest about the trade-offs involved.

---

### Part 2.2 — DELETE Idempotency

> **Question.** *Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.*

Yes — my `RoomResource.deleteRoom()` implementation is idempotent **with respect to server state**, which is the property HTTP actually guarantees for DELETE. The definition of idempotence in RFC 9110 is: *repeating the request has the same effect on server state as making it once*. It does not require the response to be identical on repeat calls.

Walk through the scenario the question poses. A client sends:

```
DELETE /api/v1/rooms/LAB-5
```

- **First call:** the method checks `roomRepository.exists("LAB-5")` (true), confirms via `sensorRepository.findByRoomId("LAB-5")` that no sensors reference it (true, LAB-5 is seeded empty), calls `roomRepository.deleteById("LAB-5")`, and returns **204 No Content**. LAB-5 is now gone.
- **Second call** (same request): the method checks `roomRepository.exists("LAB-5")` — now returns false — and throws `ResourceNotFoundException`, which the mapper converts to **404 Not Found**.
- **Third call, fourth call, hundredth call:** all identical to the second — they all return 404.

The **server state after one DELETE is identical to the state after a thousand DELETEs**: LAB-5 does not exist. That is idempotence. The response code varies (204 on the first, 404 on subsequent) but that variation is a truthful report of the server's state at the moment of each request, not a change to the state itself.

This property matters in practice because of network unreliability. Imagine a client sends DELETE, the server processes it successfully, but the TCP connection drops before the 204 response arrives. The client retries. With an idempotent DELETE, the retry is safe — it sees 404 and can treat that as "the resource is deleted, my intent is fulfilled." Without idempotence, retries could accidentally delete a newly-created resource with the same ID, or count deletions, or charge a customer twice. REST's idempotency guarantees are what make retry logic — and therefore distributed systems — possible.

Contrast this with POST, which is intentionally **not** idempotent: `POST /rooms` with the same body twice creates two rooms (in systems that auto-generate IDs) or returns 409 Conflict the second time (in systems like mine with client-supplied IDs). Neither behaviour matches "same effect each time," and that's correct — POST semantics are "create, and I don't assume the result."

---

### Part 3.1 — @Consumes Media Type Negotiation

> **Question.** *You explicitly use the `@Consumes(MediaType.APPLICATION_JSON)` annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as `text/plain` or `application/xml`. How does JAX-RS handle this mismatch?*

Every resource class in this API declares `@Consumes(MediaType.APPLICATION_JSON)` at the class level, meaning every method that reads a request body restricts itself to JSON input. When a client sends a request with, say, `Content-Type: application/xml`, Jersey's content-negotiation layer intercepts the request **before** my code runs and decides it cannot be dispatched. It responds automatically with **HTTP 415 Unsupported Media Type**, and the resource method body never executes.

This is Jersey implementing a deliberate design guarantee: **the business logic of `createRoom`, `createSensor`, or `addReading` never runs on data it doesn't understand**. The validation happens at the framework layer, not inside my method — which means I don't have to remember to add a "check if this is JSON" block at the top of every POST method, and I cannot forget. The 415 is generated uniformly and automatically.

Three concrete benefits follow:

1. **Security.** A client trying to smuggle in XML (e.g. hoping to exploit a historic XML External Entity vulnerability in some parser) gets shut down at the HTTP layer. My code never sees the payload.

2. **Failure signalling.** 415 is the correct semantic for this situation — it tells the client precisely what went wrong and what would have worked. Returning 400 would be less specific; 500 would be actively misleading.

3. **Separation of concerns.** Content negotiation is not business logic. Pushing it into annotations keeps my resource methods focused on what they're actually for: manipulating rooms and sensors.

The symmetric annotation is `@Produces(MediaType.APPLICATION_JSON)`, which lets Jersey negotiate outgoing responses in the same spirit: a client that sends `Accept: application/xml` on a request to my API will receive **406 Not Acceptable**, because my resources only know how to speak JSON. Together, `@Consumes` and `@Produces` form a tight media-type contract at the edge of the API.

---

### Part 3.2 — @QueryParam vs. @PathParam for Filtering

> **Question.** *You implemented filtering using `@QueryParam`. Contrast this with an alternative design where the type is part of the URL path (e.g. `/api/v1/sensors/type/CO2`). Why is the query parameter approach generally considered superior for filtering and searching collections?*

The two approaches look similar but express fundamentally different things in REST's vocabulary of identifiers.

- **`@PathParam` is for identity.** When a piece of the URL appears in the path, it is asserting that the URL identifies *that specific resource*. `GET /sensors/TEMP-001` means "the one sensor whose ID is TEMP-001" — the `TEMP-001` segment is part of the resource's address.

- **`@QueryParam` is for refinement.** A query parameter attached to a collection URL does not change *what* is being addressed; it refines *which subset* of that collection is returned. `GET /sensors?type=CO2` still addresses the sensors collection — it just asks for a filtered view.

My implementation uses the refinement model:

```java
@GET
public Response listAllSensors(@QueryParam("type") String type) {
    List<Sensor> sensors;
    if (type == null || type.trim().isEmpty()) {
        sensors = sensorRepository.findAll();
    } else {
        sensors = sensorRepository.findByType(type.trim());
    }
    return Response.ok(sensors).build();
}
```

A client calling `GET /sensors` with no query string still works — they get the full list. A client calling `GET /sensors?type=CO2` gets a filtered view. Neither client has to choose a different endpoint; the filter is an optional refinement.

Consider the alternative design — `GET /sensors/type/CO2`. Four problems emerge:

1. **It misrepresents resource hierarchy.** This URL implies `/sensors/type` is a collection and `CO2` is a specific entity within it. Neither is true. Sensor types are not resources; they're a property of sensors.

2. **It doesn't compose.** What URL handles filtering by both type *and* status? `/sensors/type/CO2/status/ACTIVE`? The path-nesting approach collapses under multiple simultaneous filters. Query strings compose trivially: `?type=CO2&status=ACTIVE`.

3. **The empty case is awkward.** With `@QueryParam`, a client omits the parameter to get the full list — one endpoint, optional filter. With a path-based design, `/sensors/type/` is not a meaningful URL, so "no filter" needs its own endpoint, and now there are two things to maintain.

4. **It violates RFC 3986 intent.** The URL spec is explicit that query strings exist for "non-hierarchical data" — exactly the case here.

My implementation also keeps the matching **case-insensitive** via `equalsIgnoreCase()` in `SensorRepository.findByType()`, so `?type=co2`, `?type=CO2`, and `?type=Co2` all work identically — a small API-friendliness choice that doesn't change the architectural story but saves clients from debugging case mismatches.

---

### Part 4.1 — Sub-Resource Locator Pattern

> **Question.** *Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path in one massive controller class?*

JAX-RS distinguishes two kinds of resource methods by presence of an HTTP-verb annotation. A method with `@GET`, `@POST`, `@PUT`, or `@DELETE` is a **terminal resource method** — it produces a response. A method with only `@Path` and no verb is a **sub-resource locator** — it returns another resource object, and Jersey delegates further routing to that returned object.

My implementation uses this pattern to cleanly separate sensors from their readings:

```java
// In SensorResource:
@Path("/{sensorId}/readings")
public SensorReadingResource readings(@PathParam("sensorId") String sensorId) {
    if (!sensorRepository.exists(sensorId)) {
        throw new ResourceNotFoundException("Sensor with id '" + sensorId + "' does not exist.");
    }
    return new SensorReadingResource(sensorId);
}
```

A URL like `GET /sensors/TEMP-001/readings` is routed to `SensorResource`, which matches the `/{sensorId}/readings` locator, validates that TEMP-001 exists, and delegates to a freshly-constructed `SensorReadingResource` carrying the sensor ID. The child resource then handles the actual GET or POST on `/readings`.

Four concrete benefits fall out of this design:

1. **Single Responsibility per class.** `SensorResource` is responsible for sensors and sensors only. `SensorReadingResource` is responsible for reading history and nothing else. Putting both responsibilities in one class would produce a sprawling controller — a classic "God object" — where a change to reading-handling logic risks breaking sensor code and vice versa.

2. **Parent validation in one place.** The "does this sensor exist?" check happens once, in the locator. Every operation under `/sensors/{id}/readings/...` — current ones like GET and POST, and future ones like DELETE or a PUT for marking a reading reviewed — inherits that validation automatically. There is no risk of me forgetting to re-check in a new method.

3. **Constructor-injected context.** The sensor ID becomes a final field on `SensorReadingResource`:

    ```java
    private final String sensorId;
    public SensorReadingResource(String sensorId) { this.sensorId = sensorId; }
    ```

    Every method in the child resource already knows which sensor it's operating on. Method signatures stay clean — no `@PathParam("sensorId")` repeated on every method.

4. **Natural scaling.** Adding a second sub-resource, say `/sensors/{id}/diagnostics`, is a matter of one more locator method returning a new `SensorDiagnosticsResource` class. The existing `SensorReadingResource` is unaffected. Compare that with the monolithic alternative where every new nested path adds methods to the already-crowded controller — each addition increases the blast radius of any change.

The pattern does have a trade-off: Jersey creates a new `SensorReadingResource` instance per request (it follows the same per-request lifecycle as terminal resources), which adds a trivial amount of allocation overhead. For a system of this scale the cost is immeasurable, and the readability gain is substantial.

---

### Part 5.2 — HTTP 422 vs. 404 for Missing References

> **Question.** *Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?*

The distinction hinges on *where* the missing thing is. HTTP status codes describe the fate of the **request**, and "what went wrong" means something different depending on which part of the request the server couldn't handle.

Consider two superficially similar failures:

**Scenario A.** A client sends `GET /api/v1/rooms/FAKE-999`. The URL itself points at something that doesn't exist. Jersey routes the request to my handler, my handler looks up FAKE-999, finds nothing, and the correct response is **HTTP 404 Not Found**. The word "Not Found" refers to the resource *at the URL*. This is implemented in `RoomResource.getRoomById()` via `throw new ResourceNotFoundException(...)`, which the mapper converts to 404.

**Scenario B.** A client sends `POST /api/v1/sensors` with body `{"id":"X","type":"CO2","status":"ACTIVE","roomId":"FAKE-999"}`. Everything about this request is superficially correct: the URL `/api/v1/sensors` exists and accepts POSTs, the Content-Type is `application/json`, the JSON is syntactically valid, and the object has all required fields. **The problem is not at the URL — it is inside the body.** The roomId is pointing at something that doesn't exist. Returning 404 here would mislead the client into thinking `/api/v1/sensors` itself is missing.

RFC 4918 defines **HTTP 422 Unprocessable Entity** for exactly this situation:

> *"The server understands the content type of the request entity, and the syntax of the request entity is correct, but it was unable to process the contained instructions."*

Every phrase in that definition matches Scenario B. My implementation throws `LinkedResourceNotFoundException` at the line `if (!roomRepository.exists(sensor.getRoomId()))` in `SensorResource.createSensor()`, and `LinkedResourceNotFoundExceptionMapper` translates it to 422 with a structured body:

```json
{
    "status": 422,
    "error": "Unprocessable Entity",
    "message": "Cannot create sensor: referenced room 'FAKE-999' does not exist."
}
```

Why does this semantic precision matter? Because it determines what a reasonable client does next. Seeing a 404, a client concludes "the sensor endpoint doesn't exist; I have nothing useful to do; escalate to an ops channel." Seeing a 422, the client concludes "my endpoint is fine, but my payload referenced a room I need to create first — let me create the room and retry." That is a dramatically different — and dramatically more useful — diagnosis.

The parallel distinction in my API is `ResourceNotFoundException` (404) vs. `LinkedResourceNotFoundException` (422). Having two separate exception classes forces me to think about which failure mode I am signalling, and the two mappers guarantee that the wire-level response matches that intent.

---

### Part 5.4 — Security Risks of Exposing Stack Traces

> **Question.** *From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?*

An unhandled stack trace returned to a client is a textbook information-disclosure vulnerability, ranked as "Security Misconfiguration" in the OWASP Top 10. The trace reveals four categories of sensitive information that an attacker can harvest to plan subsequent attacks:

1. **Architecture and internal structure.** Class names like `com.smartcampus.w2120049_okithadintharu_5cosc022w.resource.SensorResource` expose the project's package layout, the naming conventions for controllers, and which classes handle which URLs. An attacker now knows where to focus further probing.

2. **Exact library versions.** A trace line like `at org.glassfish.jersey.server.ServerRuntime$1.run(ServerRuntime.java:255)` pins down the Jersey version being used. The attacker takes that version, searches the CVE database, and checks whether any known remote-code-execution or denial-of-service bugs apply. Version disclosure is the first step in a targeted exploit.

3. **Line numbers and code paths.** A trace like `at SensorResource.createSensor(SensorResource.java:71)` tells the attacker the exact source line that failed. Combined with the public compiled class name, this narrows down the attack surface and may reveal validation gaps. An attacker probing with different malicious inputs can correlate line-number changes with code branches.

4. **Server environment.** Traces occasionally leak absolute file paths (`/home/ubuntu/SmartCampusAPI/...`), JVM flags, and configuration values — any of which can be used to plan a more sophisticated intrusion.

My `GlobalExceptionMapper` is deliberately structured to eliminate this leak entirely. The critical section is:

```java
LOGGER.log(Level.SEVERE, "Unhandled exception while processing request", ex);
ErrorMessage body = new ErrorMessage(
    500,
    "Internal Server Error",
    "An unexpected error occurred. Please contact the system administrator."
);
return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(body).build();
```

Two things happen in sequence, and their order matters:

- **The full trace is logged server-side via the `Logger`.** Nothing is lost from my ability to diagnose issues — the Tomcat logs retain the complete stack for developer debugging.
- **The client receives a deliberately uninformative response.** A status code (500), a reason phrase ("Internal Server Error"), and a neutral sentence. No class names. No line numbers. No hint of the underlying framework.

The mapper also handles a subtler case: `WebApplicationException` subtypes thrown by Jersey itself (e.g. 404 for unmatched routes, 415 for wrong Content-Type, 405 for wrong method). These carry legitimate status codes that *should* propagate to the client, so the mapper extracts and preserves them rather than collapsing everything to 500. The code separates "Jersey told me this is a 4xx" from "something I didn't anticipate happened, collapse to 500."

This separation — **verbose logs for us, minimal responses for them** — is the right security boundary for any production API.

---

### Part 5.5 — JAX-RS Filters vs. Manual Logger Calls

> **Question.** *Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting `Logger.info()` statements inside every single resource method?*

Logging is what software architecture calls a **cross-cutting concern** — a capability that every request needs, orthogonal to what any individual endpoint does. The two ways to implement a cross-cutting concern are: (a) repeat the same call in every method that needs it, or (b) express it once in a dedicated component that intercepts every request. My `LoggingFilter` takes approach (b) by implementing both `ContainerRequestFilter` and `ContainerResponseFilter`:

```java
@Override
public void filter(ContainerRequestContext requestContext) throws IOException {
    long startTime = System.currentTimeMillis();
    requestContext.setProperty(START_TIME_PROPERTY, startTime);
    LOGGER.info(String.format("--> %s %s",
        requestContext.getMethod(), requestContext.getUriInfo().getPath()));
}

@Override
public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
    long elapsed = System.currentTimeMillis() - (Long) requestContext.getProperty(START_TIME_PROPERTY);
    LOGGER.info(String.format("<-- %s %s  [status=%d, elapsed=%d ms]",
        requestContext.getMethod(), requestContext.getUriInfo().getPath(),
        responseContext.getStatus(), elapsed));
}
```

Contrast this with the manual approach — sprinkling `Logger.info("handling GET /rooms")` at the top and `Logger.info("responding 200")` at the bottom of every resource method. Four specific problems make the manual approach strictly worse:

1. **Completeness by default.** With a filter, every single request is logged automatically — including error paths, including requests that 415 out before reaching any method. With manual calls, I have to remember to add them to every new endpoint, and the coverage will inevitably drift: a new method, a refactored method, a new developer who doesn't know the convention — and suddenly part of the traffic goes un-logged, invisible.

2. **DRY format control.** The log format is defined once, in one class. When I later want to add a request-ID header, rename a field, or switch to structured JSON logs, I edit one file. Manual calls would require touching every resource method — fifty or more in a realistic system — and any missed update produces log lines that don't match the new schema, breaking downstream log-parsing tools.

3. **Business logic stays clean.** `createSensor()` exists to validate input and persist a sensor; it should not also be a place where I worry about observability. The manual approach conflates concerns: every method starts to carry seven or eight lines of cross-cutting code (logging, auth, metrics, tracing) that have nothing to do with its business purpose. A filter pushes all that to the edges, leaving the method itself focused on what it actually does.

4. **Access to the full request/response lifecycle.** The filter sees both the incoming request and the outgoing response — so it can compute elapsed time by stashing a start timestamp on the `ContainerRequestContext` and reading it back in the response filter. It can also log the outgoing status code, which a manual `Logger.info("returning 200")` at the end of a method only captures for the happy path; if the method throws, nothing logs the actual 500 that went back. My filter's response phase runs regardless of how the method exits.

This isn't just an academic preference — it's the same pattern every enterprise framework uses. Spring has `HandlerInterceptor`. Servlets have `javax.servlet.Filter`. Micronaut has `HttpFilter`. All of them exist because the same realisation keeps hitting the same wall: cross-cutting code belongs outside the business logic, not sprinkled through it.

---

*End of Report.*

---

**Repository:** [https://github.com/dintharu/SmartCampusAPI](https://github.com/dintharu/SmartCampusAPI)  
**Author:** K.G. Okitha Dintharu (w2120049 / 20240578)  
**Submitted:** 24 April 2026
