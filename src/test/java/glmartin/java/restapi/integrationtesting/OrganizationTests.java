package glmartin.java.restapi.integrationtesting;

import glmartin.java.restapi.entities.Organization;
import glmartin.java.restapi.entities.Status;
import glmartin.java.restapi.repositories.AppUserRepository;
import glmartin.java.restapi.repositories.OrganizationRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrganizationTests extends RestIntegrationTester {

    @LocalServerPort
    private Integer port;

    @Autowired
    private OrganizationRepository orgRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    private Organization o1;
    private Organization o2;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        appUserRepository.deleteAll();
        orgRepository.deleteAll();

        o1 = orgRepository.save(new Organization("Google", Status.ACTIVE));
        o2 = orgRepository.save(new Organization("Amazon", Status.PENDING));

//        List<Organization> orgs = List.of(
//                new Organization("Google", Status.ACTIVE),
//                new Organization("Amazon", Status.PENDING)
//        );
//        orgRepository.saveAll(orgs);
    }

    @Test
    void shouldGetAllOrgs() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/organizations")
                .then()
                .statusCode(200)
                .body("_embedded.organizationList", hasSize(2));
    }

    @Test
    void shouldGetSingleOrg() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/organizations/" + o1.getId())
                .then()
                .statusCode(200)
                .body("name", equalTo("Google"))
                .body("status", equalTo("ACTIVE"));
    }

    @Test
    void shouldReturnNotFound() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/organizations/111")
                .then()
                .statusCode(404)
                .body("message", equalTo("Could not find resource: /organizations/111"));
    }
}