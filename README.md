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

     
|    Method |                       Endpoint                        |    Description          | Status Code            |
|-----------|-------------------------------------------------------|-------------------------|----------------------------------|
| 1. GET       | http://localhost:8082/api/books                       | Get all books           | 200 OK                       | 
 
 Screenshot: ![image alt](https://github.com/laurienmvuyekure/MVUYEKURE_Laurien_28319_Web_technology_and_Internet/blob/01b261fa0ef79c8a7e1706d7742d89a83820235f/Getting%20books.jpg) 

 

|    Method |                       Endpoint                        |    Description          | Status Code            |
|-----------|-------------------------------------------------------|-------------------------|----------------------------------|
| 2. GET       | http://localhost:8082/api/books/{3}               | Get book by ID          | 200 (OK)/ 404 - NOT Found       |  

Screenshot: ![image alt](https://github.com/laurienmvuyekure/MVUYEKURE_Laurien_28319_Web_technology_and_Internet/blob/a919002db1bf6e47f1d0fab64c9d89a11d8aab45/Getting%20book%20by%20ID.jpg)

|    Method |                       Endpoint                        |    Description          | Status Code            |
|-----------|-------------------------------------------------------|-------------------------|----------------------------------|
| 3. GET       | http://localhost:8082/api/books/search?title={Ngunda} | Search books by title   | 200 OK                      | 

Screenshot: ![image alt](https://github.com/laurienmvuyekure/MVUYEKURE_Laurien_28319_Web_technology_and_Internet/blob/fd92090dbbf2415140bdcbaf1b9dc8979dee2893/Getting%20book%20by%20searching%20title.jpg)

|    Method |                       Endpoint                        |    Description          | Status Code            |
|-----------|-------------------------------------------------------|-------------------------|----------------------------------|
| 4. POST      | http://localhost:8082)/api/books                      | Add new book            | 201 CREATED                 |  

Screenshot: ![image alt](https://github.com/laurienmvuyekure/MVUYEKURE_Laurien_28319_Web_technology_and_Internet/blob/c97342e7925e80fd264be9da5cadb0983af3525d/Add%20new%20book.jpg)

|    Method |                       Endpoint                        |    Description          | Status Code            |
|-----------|-------------------------------------------------------|-------------------------|----------------------------------|
| 5. DELETE    | http://localhost:8082/api/books/{3}             | Delete book         | 204 No Content / 404 - NOT Found |  

Screenshot: ![image alt](https://github.com/laurienmvuyekure/MVUYEKURE_Laurien_28319_Web_technology_and_Internet/blob/6ca2bd959c8b2d9710ba4e036867395437453b89/Delete%20a%20book%20.jpg)

## 🛠️ Technology Stack

- Java 21
- Spring Boot 4.0.2
- Maven
- Tomcat

## 📦 Project Structure
 Project Structure:
 ![image alt](https://github.com/laurienmvuyekure/MVUYEKURE_Laurien_28319_Web_technology_and_Internet/blob/dea27dda93a7f1dc0139ff94640e52cf23d9168d/Project%20Structure.jpg)
