package com.mercadona;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MercadonaApplication.class)
@ActiveProfiles("test")
class MercadonaApplicationTest {
    
    @Test
    void contextLoads() {
    }
    
    @Test
    void applicationHasCorrectConfiguration() {
    }
}
