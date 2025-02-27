package com.example.serverapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // ✅ Create a new Task
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task savedTask = taskRepository.save(task);
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
    }

    // ✅ Get all Tasks
    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // ✅ Get a Task by ID
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable String id) {
        Optional<Task> task = taskRepository.findById(id);
        return task.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Find Tasks by Name
    @GetMapping("/findByName")
    public ResponseEntity<List<Task>> findTasksByName(@RequestParam String name) {
        List<Task> tasks = taskRepository.findByNameContaining(name);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // ✅ Update a Task
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable String id, @RequestBody Task updatedTask) {
        return taskRepository.findById(id).map(task -> {
            task.setName(updatedTask.getName());
            task.setOwner(updatedTask.getOwner());
            task.setCommand(updatedTask.getCommand());
            Task savedTask = taskRepository.save(task);
            return ResponseEntity.ok(savedTask);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Delete a Task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Execute a Task
    @PutMapping("/{id}/execute")
    public ResponseEntity<Task> executeTask(@PathVariable String id) {
        return taskRepository.findById(id).map(task -> {
            Instant startTime = Instant.now();
            // Simulate execution (for real, you'd execute a command)
            String output = "Hello World again!";
            Instant endTime = Instant.now();
            
            TaskExecution execution = new TaskExecution(startTime, endTime, output);
            task.addExecution(execution);

            Task updatedTask = taskRepository.save(task);
            return ResponseEntity.ok(updatedTask);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
