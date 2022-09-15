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
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
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
        var containerDelegate = new JdbcDatabaseDelegate(postgresDB, "");
        ScriptUtils.runInitScript(containerDelegate, "init-card.sql");
        String body = "{" +
                "  \"cardFromNumber\":\"777777777777\"," +
                "  \"cardFromValidTill\":\"10-10-2022\"," +
                "  \"cardFromCVV\":\"123\"," +
                "  \"cardToNumber\":\"88888888888\"," +
                "  \"amount\": {" +
                "    \"value\":100," +
                "    \"currency\":\"RUR\"" +
                "  }" +
                "}";
        String excepted = "{" +
                "\"operationId\":\"1\"" +
                "}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> forEntity = restTemplate.postForEntity(
                "/transfer",
                requestEntity,
                String.class
        );

        assertEquals(excepted, forEntity.getBody());
    }

    @Test
    public void confirm() {
        var containerDelegate = new JdbcDatabaseDelegate(postgresDB, "");
        ScriptUtils.runInitScript(containerDelegate, "init-transfer.sql");
        String body = "{" +
                "  \"operationId\": \"-1\"," + // указан -1 что бы тесты всегда выполнялись, иначе при совпадении идентификаторов будет ошибка
                "  \"code\": \"0000\"" +
                "}";
        String excepted = "{" +
                "\"operationId\":\"-1\"" +
                "}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> forEntity = restTemplate.postForEntity(
                "/confirmOperation",
                requestEntity,
                String.class
        );

        assertEquals(excepted, forEntity.getBody());
    }
}
