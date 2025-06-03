package com.microservices.note;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableNeo4jRepositories
public class NoteServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NoteServiceApplication.class, args);
    }
}
