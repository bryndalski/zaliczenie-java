package com.microservices.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI authServiceOpenAPI() {
        Server server = new Server();
        server.setUrl("/api/auth");
        server.setDescription("Auth Service API");
        
        return new OpenAPI()
                .servers(List.of(server))
                .info(new Info()
                        .title("Auth Service API")
                        .description("Authentication and authorization microservice")
                        .version("1.0.0"));
    }
}
