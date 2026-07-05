package com.hrms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

// ==============================================================================
// TEST CONTROLLER: HELLO CONTROLLER
// ==============================================================================
// This controller is used specifically for testing initial API connectivity
// and verifying the success of the automated CI/CD pipeline.
// This endpoint does not require a database connection.
// ==============================================================================

@Tag(name = "Test Connection", description = "Endpoints for testing API connectivity")
@RestController // Marks this class as a REST Controller
@CrossOrigin(origins = "*") // Allow Cross-Origin Resource Sharing (CORS) from any origin
public class HelloController {

    // ENDPOINT: http://localhost:8020/api/v1/hello
    @Operation(summary = "Get Hello World Message", description = "A simple endpoint to verify that the server is up and CI/CD pipelines ran successfully.")
    @GetMapping("/api/v1/hello")
    public String sayHelloWorld() {
        return "Hello World! HRMS Backend is running successfully.";
    }
}
