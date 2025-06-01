package com.microservices.user.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI userServiceOpenAPI() {
        Server server = new Server();
        server.setUrl("/api/users");
        server.setDescription("User Service API");
        
        return new OpenAPI()
                .servers(List.of(server))
                .info(new Info()
                        .title("User Service API")
                        .description("User management microservice")
                        .version("1.0.0"));
    }
}
