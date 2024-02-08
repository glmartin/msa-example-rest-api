package glmartin.java.restapi.controllers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;

import glmartin.java.restapi.entities.Organization;
import glmartin.java.restapi.entities.Status;
import glmartin.java.restapi.repositories.AppUserRepository;
import glmartin.java.restapi.repositories.OrganizationRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrganizationControllerTest {

    @LocalServerPort
    private Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // set the DB connection properties from the postgres testcontainers image
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    OrganizationRepository orgRepository;

    @Autowired
    AppUserRepository appUserRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        appUserRepository.deleteAll();
        orgRepository.deleteAll();
    }

    @Test
    void shouldGetAllOrgs() {
        List<Organization> orgs = List.of(
                new Organization("Google", Status.ACTIVE),
                new Organization("Amazon", Status.PENDING)
        );
        orgRepository.saveAll(orgs);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/organizations")
                .then()
                .statusCode(200)
                .body("_embedded.organizationList", hasSize(2));
    }

    @Test
    void shouldGetASingleOrg() {
        List<Organization> orgs = List.of(
                new Organization("Google", Status.ACTIVE),
                new Organization("Amazon", Status.PENDING)
        );
        orgRepository.saveAll(orgs);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/organizations/1")
                .then()
                .statusCode(200)
                .body("_embedded.organizationList", hasSize(2));
    }
}