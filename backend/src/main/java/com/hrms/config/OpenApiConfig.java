package com.hrms.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

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
        // Define production server using HTTPS to prevent Mixed Content errors in the browser
        Server productionServer = new Server()
                .url("https://api.bintangmada.web.id")
                .description("Production Server (HTTPS)");

        // Define local server for local debugging
        Server localServer = new Server()
                .url("http://localhost:8020")
                .description("Local Development Server (HTTP)");

        return new OpenAPI()
                .info(new Info()
                        .title("HRMS API Documentation")
                        .version("1.0.0")
                        .description("Official REST API Documentation for the Human Resource Management System (HRMS). You can test all the CRUD endpoints directly from this interface.")
                        .contact(new Contact()
                                .name("Bintang Mada")
                                .url("https://github.com/bintangmada")))
                .servers(List.of(productionServer, localServer))
                // Add Authorization option in Swagger UI
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }
}

