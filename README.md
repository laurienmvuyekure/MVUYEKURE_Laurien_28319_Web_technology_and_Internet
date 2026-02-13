# 📚 Library Book Management API

A  RESTful API for managing library book collections built with Spring Boot.

##  Features

- ✅ Complete CRUD operations for books
- ✅ Search books by title (case-insensitive)
- ✅ In-memory storage using ArrayList
- ✅ Proper HTTP status codes (200, 201, 204, 404)
- ✅ Clean architecture with separation of concerns
- ✅ Comprehensive error handling

## 📋 API Endpoints

|    Method |                       Endpoint                        |    Description          |           Status Code            |
|-----------|-------------------------------------------------------|-------------------------|----------------------------------|
| GET       | http://localhost:8082/api/books                       | Get all books           | 200 OK                           |

| GET       | http://localhost:8082/api/books/{3}                   | Get book by ID          | 200 (OK)/ 404 - NOT Found        |
| GET       | http://localhost:8082/api/books/search?title={Ngunda} | Search books by title   | 200 OK                           |
| POST      | http://localhost:8082)/api/books                      | Add new book            | 201 CREATED                      |
| DELETE    | http://localhost:8082/api/books/{3}                   | Delete book             | 204 No Content / 404 - NOT Found |

## 🛠️ Technology Stack

- Java 21
- Spring Boot 4.0.2
- Maven
- Tomcat

## 📦 Project Structure
