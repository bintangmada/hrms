package com.hrms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// ==============================================================================
// SWAGGER OPENAPI CONFIGURATION
// ==============================================================================
// This class configures the metadata, title, and descriptions displayed
// on the public-facing Swagger UI API documentation page.
// ==============================================================================

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HRMS API Documentation")
                        .version("1.0.0")
                        .description("Official REST API Documentation for the Human Resource Management System (HRMS). You can test all the CRUD endpoints directly from this interface.")
                        .contact(new Contact()
                                .name("Bintang Mada")
                                .url("https://github.com/bintangmada")));
    }
}

