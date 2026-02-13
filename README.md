# 🎓 Student Registration API

A comprehensive RESTful API for managing student registrations and information. Built with Spring Boot.

This complete solution provides:
1. ✅ All required endpoints
2. ✅ 5 Sample students with different majors and GPAs
3. ✅ Proper package structure (controller.registration, model.registration)
4. ✅ In-memory storage using ArrayList
5. ✅ HTTP status codes (200, 201, 400, 404, 409)
6. ✅ Email validation
7. ✅ Comprehensive documentation
8. ✅ Test scenarios for Postman

## 📋 API Endpoints

### 1. GET All Students
**1. Retrieves a list of all registered students.**

**Request:** 
|    Method |                       Endpoint                        |    Description          | Status Code            |
|-----------|-------------------------------------------------------|-------------------------|----------------------------------|
| 1. GET       | http://localhost:8083/api/students                       | Get all students           | 200 OK                       | 
 
 Screenshot: ![image alt](https://github.com/laurienmvuyekure/MVUYEKURE_Laurien_28319_Web_technology_and_Internet/blob/2a9912b84291aaef731b969ecef834ccf2e2359f/Get%20all%20students.jpg)

**2. Retrieves a list of registered students by using ID.**

**Request**: 
|    Method |                       Endpoint                        |    Description          | Status Code            |
|-----------|-------------------------------------------------------|-------------------------|----------------------------------|
| 1. GET       | http://localhost:8083/api/students/1                      | Get all students           | 200 OK                       | 

Screenshot: ![image alt](https://github.com/laurienmvuyekure/MVUYEKURE_Laurien_28319_Web_technology_and_Internet/blob/3580946cc25ba0657193d1617a0478c106c5cfe2/Get%20student%20by%20ID.jpg)
