package com.minishop.minishopspring;

import com.minishop.minishopspring.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MinishopSpringApplicationTests extends BaseIntegrationTest {

    @Test
    void contextLoads() {
    }

}
