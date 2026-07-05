package com.hrms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

// ==============================================================================
// TEST CONTROLLER: HELLO CONTROLLER
// ==============================================================================
// Controller ini digunakan khusus untuk melakukan testing koneksi awal API 
// dan verifikasi suksesnya alur pipeline CI/CD Anda.
// Endpoint ini tidak memerlukan koneksi ke database.
// ==============================================================================

@RestController // Menandakan bahwa ini adalah REST Controller
@CrossOrigin(origins = "*") // Mengizinkan akses dari domain/port mana saja (CORS)
public class HelloController {

    // ENDPOINT: http://localhost:8020/api/v1/hello
    @GetMapping("/api/v1/hello")
    public String sayHelloWorld() {
        return "Hello World! HRMS Backend is running successfully.";
    }
}
