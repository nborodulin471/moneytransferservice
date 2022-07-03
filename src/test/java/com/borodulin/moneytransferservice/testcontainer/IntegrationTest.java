package com.borodulin.moneytransferservice.testcontainer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {
    @Container
    private final static PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:latest")
            .withExposedPorts(5432, 5432)
            .withUsername("postgres")
            .withPassword("myPassword")
            .waitingFor(Wait.forListeningPort());

    @Container
    private final static GenericContainer<?> MONEY_TRANSFER_SERVICE = new GenericContainer<>(DockerImageName.parse("moneytransferservice:latest"))
            .withExposedPorts(5500)
            .dependsOn(POSTGRES)
            .waitingFor(Wait.forListeningPort());

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    public static void setUp() {
        System.setProperty("spring.datasource.url", POSTGRES.getJdbcUrl());
        System.setProperty("spring.datasource.username", POSTGRES.getUsername());
        System.setProperty("spring.datasource.password", POSTGRES.getPassword());
    }

    @Test
    public void transfer() {
        String body = "{" +
                "  \"cardFromNumber\":\"1231231123123123\"," +
                "  \"cardFromValidTill\":\"101012020\"," +
                "  \"cardFromCVV\":\"123\"," +
                "  \"cardToNumber\":\"123\"," +
                "  \"amount\": {" +
                "    \"value\":100," +
                "    \"currency\":\"RUR\"" +
                "  }" +
                "}";
        String excepted = "{" +
                "  \"operationId\": \"1\"" +
                "}";

        ResponseEntity<String> forEntity = restTemplate.postForEntity(
                "http://localhost:" + MONEY_TRANSFER_SERVICE.getMappedPort(5500) + "/transfer",
                body,
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

        ResponseEntity<String> forEntity = restTemplate.postForEntity(
                "http://localhost:" + MONEY_TRANSFER_SERVICE.getMappedPort(5500) + "/confirmOperation",
                body,
                String.class
        );

        assertEquals(excepted, forEntity.getBody());
    }
}
