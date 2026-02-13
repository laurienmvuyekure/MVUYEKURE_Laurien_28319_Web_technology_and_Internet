# 🎓 Student Registration API

A comprehensive RESTful API for managing student registrations and information. Built with Spring Boot.

✅ Key Features Implemented
✓ CRUD operations for student management
✓ Filter students by major (path variable)
✓ Filter students by minimum GPA (query parameter)
✓ Email validation
✓ Duplicate email prevention
✓ Proper HTTP status codes
✓ 5 sample students with diverse majors and GPAs
✓ Clean package structure (controller/model with inner packages)
✓ In-memory storage using ArrayList
✓ Thread-safe ID generation
✓ Comprehensive error handling

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
| 2. GET       | http://localhost:8083/api/students/1                      | Get students by using ID         | 200 OK                       | 

Screenshot: ![image alt](https://github.com/laurienmvuyekure/MVUYEKURE_Laurien_28319_Web_technology_and_Internet/blob/3580946cc25ba0657193d1617a0478c106c5cfe2/Get%20student%20by%20ID.jpg)

**3. Retrieves a list of registered students by using Major.**

**Request**: 
|    Method |                       Endpoint                        |    Description          | Status Code            |
|-----------|-------------------------------------------------------|-------------------------|----------------------------------|
| 3. GET       | http://localhost:8083/api/students/major/{major}                      | Get students by filter for Mojar           | 200 OK                       | 

Screenshot: ![image alt](https://github.com/laurienmvuyekure/MVUYEKURE_Laurien_28319_Web_technology_and_Internet/blob/31cefe288dcd12b9d54017ec136a104dba031676/Get%20all%20students%20by%20majo.jpg)

**4. Retrieves a list of registered students by filtering students with GPA greater than
or equal to minimum.**

**Request**: 
|    Method |                       Endpoint                        |    Description          | Status Code            |
|-----------|-------------------------------------------------------|-------------------------|----------------------------------|
| 4. GET       | http://localhost:8083/api/students/filter?gpa=3.5                    | Get students by filter for Mojar           | 200 OK                       | 

Screenshot: ![image alt](https://github.com/laurienmvuyekure/MVUYEKURE_Laurien_28319_Web_technology_and_Internet/blob/03873ff6195bd9b487e364e4e8ba150163e94569/Filter%20by%20GPA.jpg)

**5. Create new students** 

**Request**: 
|    Method |                       Endpoint                        |    Description          | Status Code            |
|-----------|-------------------------------------------------------|-------------------------|----------------------------------|
| 5. POST       | http://localhost:8083/api/students/                   | Get students by filter for Mojar           | 201 CREATED                       | 

Screenshot: ![image alt](https://github.com/laurienmvuyekure/MVUYEKURE_Laurien_28319_Web_technology_and_Internet/blob/e941a0b2869c53b0f2ed62628a8d94537786a4aa/Post%20new%20student.jpg)

**6. Update information of student by using ID** 

**Request**: 
|    Method |                       Endpoint                        |    Description          | Status Code            |
|-----------|-------------------------------------------------------|-------------------------|----------------------------------|
| 5. PUT       | http://localhost:8083/api/students/7                   | Upadate informations of student by using ID           | 200 OK                       | 

Screenshot: ![image alt](https://github.com/laurienmvuyekure/MVUYEKURE_Laurien_28319_Web_technology_and_Internet/blob/65fe51cd9e602090598c41a074fc2d11171176dc/Update%20info.jpg)

#  After Update and Create new students

Screenshot: ![image alt](https://github.com/laurienmvuyekure/MVUYEKURE_Laurien_28319_Web_technology_and_Internet/blob/e052a03f6dc5ea1e4983539330c3b5c9b6439fbc/After%20post%20and%20update.jpg)

# 🛠️ Technology Stack
Java 21

Spring Boot 3.x

Maven

Tomcat

# Project structure
![image alt](https://github.com/laurienmvuyekure/MVUYEKURE_Laurien_28319_Web_technology_and_Internet/blob/816d5b91137c5c591f83e0f15968511517ba21b3/Project%20structure.jpg)
