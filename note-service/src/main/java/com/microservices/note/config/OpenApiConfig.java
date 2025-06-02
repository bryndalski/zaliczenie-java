package com.microservices.note.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI noteServiceOpenAPI() {
        Server server = new Server();
        server.setUrl("/api/notes");
        server.setDescription("Note Service REST API");
        
        return new OpenAPI()
                .servers(List.of(server))
                .components(new Components()
                    .addSecuritySchemes("Bearer Authentication", 
                        new SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description("Enter JWT token from Keycloak")))
                .info(new Info()
                        .title("Note Service REST API")
                        .description("Note management with Neo4j database and JWT auth - API docs are public")
                        .version("1.0.0"));
    }
}
