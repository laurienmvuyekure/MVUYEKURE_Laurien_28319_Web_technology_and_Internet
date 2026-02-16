package auca.ac.rw.question5_task_api.controller;

import auca.ac.rw.question5_task_api.model.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * REST Controller for managing tasks in a to-do list application
 * Provides comprehensive task management functionality
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    // In-memory storage for tasks using ArrayList
    private final List<Task> tasks = new ArrayList<>();

    // AtomicLong for generating unique IDs in a thread-safe manner
    private final AtomicLong idGenerator = new AtomicLong(6);

    /**
     * Initialize sample data with 6 diverse tasks
     */
    public TaskController() {
        initializeSampleTasks();
    }

    /**
     * Creates six sample tasks with different priorities and completion statuses
     */
    private void initializeSampleTasks() {
        // High priority tasks
        tasks.add(new Task(1L, "Complete project proposal",
                "Write and submit the Q2 project proposal to management",
                false, "HIGH", "2024-03-20"));

        tasks.add(new Task(2L, "Fix critical production bug",
                "Address the login authentication issue reported by customers",
                true, "HIGH", "2024-03-15"));

        // Medium priority tasks
        tasks.add(new Task(3L, "Update documentation",
                "Refresh API documentation with new endpoints",
                false, "MEDIUM", "2024-03-25"));

        tasks.add(new Task(4L, "Team meeting",
                "Weekly sprint planning and review meeting",
                true, "MEDIUM", "2024-03-16"));

        // Low priority tasks
        tasks.add(new Task(5L, "Organize inbox",
                "Clean up and categorize emails",
                false, "LOW", "2024-03-30"));

        tasks.add(new Task(6L, "Update profile picture",
                "Change profile picture on company website",
                false, "LOW", "2024-04-01"));
    }

    /**
     * GET /api/tasks
     * Retrieves all tasks
     * 
     * @return ResponseEntity containing list of all tasks with HTTP status 200 OK
     */
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    /**
     * GET /api/tasks/{taskId}
     * Retrieves a specific task by its ID
     * 
     * @param taskId - Task ID to search for
     * @return ResponseEntity containing the task if found with HTTP status 200 OK,
     *         otherwise HTTP status 404 NOT FOUND
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long taskId) {
        return tasks.stream()
                .filter(task -> task.getTaskId().equals(taskId))
                .findFirst()
                .map(task -> new ResponseEntity<>(task, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET /api/tasks/status?completed={true/false}
     * Retrieves tasks by their completion status
     * 
     * @param completed - Boolean value to filter by (true for completed, false for
     *                  pending)
     * @return ResponseEntity containing list of filtered tasks
     */
    @GetMapping("/status")
    public ResponseEntity<List<Task>> getTasksByStatus(@RequestParam boolean completed) {
        List<Task> filteredTasks = tasks.stream()
                .filter(task -> task.isCompleted() == completed)
                .collect(Collectors.toList());

        return new ResponseEntity<>(filteredTasks, HttpStatus.OK);
    }

    /**
     * GET /api/tasks/priority/{priority}
     * Retrieves tasks by their priority level
     * 
     * @param priority - Priority level (LOW, MEDIUM, HIGH)
     * @return ResponseEntity containing list of tasks with specified priority
     */
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Task>> getTasksByPriority(@PathVariable String priority) {
        // Validate priority
        if (!isValidPriority(priority)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Task> tasksByPriority = tasks.stream()
                .filter(task -> task.getPriority().equalsIgnoreCase(priority))
                .collect(Collectors.toList());

        return new ResponseEntity<>(tasksByPriority, HttpStatus.OK);
    }

    /**
     * POST /api/tasks
     * Creates a new task
     * 
     * @param task - Task object from request body (without ID)
     * @return ResponseEntity containing the created task with generated ID
     *         and HTTP status 201 CREATED
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        // Validate required fields
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Validate priority
        if (!isValidPriority(task.getPriority())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Validate date format (basic check)
        if (!isValidDateFormat(task.getDueDate())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Generate new ID for the task
        task.setTaskId(idGenerator.incrementAndGet());
        tasks.add(task);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    /**
     * PUT /api/tasks/{taskId}
     * Updates an existing task completely
     * 
     * @param taskId      - ID of the task to update
     * @param updatedTask - Updated task object
     * @return ResponseEntity with updated task or appropriate error status
     */
    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(@PathVariable Long taskId,
            @RequestBody Task updatedTask) {
        // Validate updated task
        if (updatedTask.getTitle() == null || updatedTask.getTitle().trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!isValidPriority(updatedTask.getPriority())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!isValidDateFormat(updatedTask.getDueDate())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        for (Task existingTask : tasks) {
            if (existingTask.getTaskId().equals(taskId)) {
                // Update task information
                existingTask.setTitle(updatedTask.getTitle());
                existingTask.setDescription(updatedTask.getDescription());
                existingTask.setCompleted(updatedTask.isCompleted());
                existingTask.setPriority(updatedTask.getPriority());
                existingTask.setDueDate(updatedTask.getDueDate());

                return new ResponseEntity<>(existingTask, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * PATCH /api/tasks/{taskId}/complete
     * Marks a task as completed
     * 
     * @param taskId - ID of the task to mark as completed
     * @return ResponseEntity with updated task or error status
     */
    @PatchMapping("/{taskId}/complete")
    public ResponseEntity<Task> markTaskAsCompleted(@PathVariable Long taskId) {
        for (Task task : tasks) {
            if (task.getTaskId().equals(taskId)) {
                task.setCompleted(true);
                return new ResponseEntity<>(task, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * DELETE /api/tasks/{taskId}
     * Deletes a task
     * 
     * @param taskId - ID of the task to delete
     * @return ResponseEntity with HTTP status 204 NO CONTENT if successful,
     *         HTTP status 404 NOT FOUND if task doesn't exist
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        boolean removed = tasks.removeIf(task -> task.getTaskId().equals(taskId));

        if (removed) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Helper method to validate priority
     * 
     * @param priority - Priority to validate
     * @return true if priority is valid, false otherwise
     */
    private boolean isValidPriority(String priority) {
        return priority != null &&
                (priority.equalsIgnoreCase("LOW") ||
                        priority.equalsIgnoreCase("MEDIUM") ||
                        priority.equalsIgnoreCase("HIGH"));
    }

    /**
     * Helper method to validate date format (basic check)
     * 
     * @param date - Date string to validate
     * @return true if date matches YYYY-MM-DD format, false otherwise
     */
    private boolean isValidDateFormat(String date) {
        return date != null && date.matches("\\d{4}-\\d{2}-\\d{2}");
    }
}
