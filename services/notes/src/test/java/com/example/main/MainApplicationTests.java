package com.example.main;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/test",
    "spring.mongodb.embedded.version=4.0.21"
})
class MainApplicationTests {

    @Test
    void contextLoads() {
    }

}
