# Smart Campus — Sensor & Room Management API

**Author:** K.G. Okitha Dintharu
**Student ID:** w2120049 / 20240578
**Module:** 5COSC022W.2 Client-Server Architectures (2025/26)
**Institution:** Informatics Institute of Technology (IIT), Sri Lanka
**Affiliated University:** University of Westminster

---

## Overview

A RESTful web service for managing rooms and sensors in a university's "Smart Campus" infrastructure. Built as a JAX-RS application deployed on GlassFish Server.

The API provides CRUD operations for rooms, sensors, and their historical readings — with full exception handling, request/response logging, and HATEOAS-style discovery.

## Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 8 |
| JAX-RS Implementation | Jersey 2.35 |
| JSON Processing | Jackson 2.13.4 |
| Build Tool | Maven |
| Web Server | GlassFish 5.1.0 |
| Data Store | In-memory `ConcurrentHashMap` (no database) |

## Build & Run

Base URL once deployed: `http://localhost:8080/SmartCampusAPI/api/v1`

## Status

🚧 Under active development.

---

*Full report, endpoint documentation, and curl examples will be added as the project progresses.*