package auca.ac.rw.question2_student_api.controller;

import auca.ac.rw.question2_student_api.model.Student;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * REST Controller for managing student registration
 * Provides CRUD operations and filtering capabilities
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    // In-memory storage for students using ArrayList
    private final List<Student> students = new ArrayList<>();

    // AtomicLong for generating unique IDs in a thread-safe manner
    private final AtomicLong idGenerator = new AtomicLong(5);

    /**
     * Initialize sample data with 5 diverse students when controller is created
     */
    public StudentController() {
        initializeSampleStudents();
    }

    /**
     * Creates five sample students with different majors and GPAs for testing
     */
    private void initializeSampleStudents() {
        students.add(new Student(1L, "Zackaria", "Abdoulaye ",
                "zackaria.abdoulaye@auca.ac.rw", "Software Engineering", 3.8));
        students.add(new Student(2L, "Jane", "KIRABO",
                "janekirabo@gmail.com", "Management Information", 3.0));
        students.add(new Student(3L, "Tom", "Kayijuka",
                "bobkayijuka@gmail.com", "Finance", 3.5));
        students.add(new Student(4L, "Alice", "NAMAHORO",
                "alicenamahoro@auca.ac.rw", "Network Engineering", 3.7));
        students.add(new Student(5L, "Patrick", "RUKUNDO",
                "rukundopatrick@auca.ac.rw", "Software Engineering", 2.8));
    }

    /**
     * GET /api/students
     * Retrieves all registered students
     * 
     * @return ResponseEntity containing list of all students with HTTP status 200
     *         OK
     */
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    /**
     * GET /api/students/{studentId}
     * Retrieves a specific student by their ID
     * 
     * @param studentId - Student ID to search for
     * @return ResponseEntity containing the student if found with HTTP status 200
     *         OK,
     *         otherwise HTTP status 404 NOT FOUND
     */
    @GetMapping("/{studentId}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long studentId) {
        return students.stream()
                .filter(student -> student.getStudentId().equals(studentId))
                .findFirst()
                .map(student -> new ResponseEntity<>(student, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET /api/students/major/{major}
     * Retrieves all students enrolled in a specific major
     * 
     * @param major - Major to filter by (case-insensitive)
     * @return ResponseEntity containing list of students in that major with HTTP
     *         status 200 OK
     */
    @GetMapping("/major/{major}")
    public ResponseEntity<List<Student>> getStudentsByMajor(@PathVariable String major) {
        List<Student> studentsByMajor = students.stream()
                .filter(student -> student.getMajor().equalsIgnoreCase(major))
                .collect(Collectors.toList());

        return new ResponseEntity<>(studentsByMajor, HttpStatus.OK);
    }

    /**
     * GET /api/students/filter?gpa={minGpa}
     * Filters students with GPA greater than or equal to the minimum specified
     * 
     * @param minGpa - Minimum GPA threshold
     * @return ResponseEntity containing list of students meeting GPA criteria with
     *         HTTP status 200 OK
     */
    @GetMapping("/filter")
    public ResponseEntity<List<Student>> filterStudentsByGpa(@RequestParam Double gpa) {
        List<Student> filteredStudents = students.stream()
                .filter(student -> student.getGpa() >= gpa)
                .collect(Collectors.toList());

        return new ResponseEntity<>(filteredStudents, HttpStatus.OK);
    }

    /**
     * POST /api/students
     * Registers a new student in the system
     * 
     * @param student - Student object from request body (without ID)
     * @return ResponseEntity containing the created student with generated ID
     *         and HTTP status 201 CREATED
     */
    @PostMapping
    public ResponseEntity<Student> registerStudent(@RequestBody Student student) {
        // Validate email format (basic check)
        if (!isValidEmail(student.getEmail())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Check if email already exists
        if (isEmailExists(student.getEmail())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        // Generate new ID for the student
        student.setStudentId(idGenerator.incrementAndGet());
        students.add(student);
        return new ResponseEntity<>(student, HttpStatus.CREATED);
    }

    /**
     * PUT /api/students/{studentId}
     * Updates information for an existing student
     * 
     * @param studentId      - ID of the student to update
     * @param updatedStudent - Updated student object from request body
     * @return ResponseEntity containing the updated student with HTTP status 200
     *         OK,
     *         HTTP status 404 NOT FOUND if student doesn't exist,
     *         or HTTP status 400 BAD REQUEST if validation fails
     */
    @PutMapping("/{studentId}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long studentId,
            @RequestBody Student updatedStudent) {
        // Find the student to update
        for (Student existingStudent : students) {
            if (existingStudent.getStudentId().equals(studentId)) {
                // Validate email if it's being changed
                if (!existingStudent.getEmail().equals(updatedStudent.getEmail())) {
                    if (!isValidEmail(updatedStudent.getEmail())) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                    if (isEmailExists(updatedStudent.getEmail())) {
                        return new ResponseEntity<>(HttpStatus.CONFLICT);
                    }
                }

                // Update student information
                existingStudent.setFirstName(updatedStudent.getFirstName());
                existingStudent.setLastName(updatedStudent.getLastName());
                existingStudent.setEmail(updatedStudent.getEmail());
                existingStudent.setMajor(updatedStudent.getMajor());
                existingStudent.setGpa(updatedStudent.getGpa());

                return new ResponseEntity<>(existingStudent, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Helper method to validate email format
     * 
     * @param email - Email to validate
     * @return true if email is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * Helper method to check if email already exists
     * 
     * @param email - Email to check
     * @return true if email exists, false otherwise
     */
    private boolean isEmailExists(String email) {
        return students.stream()
                .anyMatch(student -> student.getEmail().equalsIgnoreCase(email));
    }
}
