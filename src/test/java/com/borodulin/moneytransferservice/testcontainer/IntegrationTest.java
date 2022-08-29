package com.borodulin.moneytransferservice.testcontainer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {
    @Container
    private final static PostgreSQLContainer<?> postgresDB = new PostgreSQLContainer<>("postgres:latest")
            .withExposedPorts(5432, 5432)
            .withUsername("postgres")
            .withPassword("myPassword");

    @Autowired
    private TestRestTemplate restTemplate;

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresDB::getJdbcUrl);
        registry.add("spring.datasource.username", postgresDB::getUsername);
        registry.add("spring.datasource.password", postgresDB::getPassword);

    }

    @Test
    public void transfer() {
        String body = "{" +
                "  \"cardFromNumber\":\"20200222222222\"," +
                "  \"cardFromValidTill\":\"10-10-2022\"," +
                "  \"cardFromCVV\":\"123\"," +
                "  \"cardToNumber\":\"20204444444444\"," +
                "  \"amount\": {" +
                "    \"value\":100," +
                "    \"currency\":\"RUR\"" +
                "  }" +
                "}";
        String excepted = "{" +
                "  \"operationId\": \"1\"" +
                "}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> forEntity = restTemplate.postForEntity(
                "/transfer",
                requestEntity,
                String.class
        );

        assertEquals(excepted, forEntity.getBody());
    }

    @Test
    public void confirm() {
        String body = "{" +
                "  \"operationId\": \"17\"," +
                "  \"code\": \"успех\"" +
                "}";
        String excepted = "{" +
                "  \"operationId\": \"17\"" +
                "}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> forEntity = restTemplate.postForEntity(
                "/confirmOperation",
                requestEntity,
                String.class
        );

        assertEquals(excepted, forEntity.getBody());
    }
}
