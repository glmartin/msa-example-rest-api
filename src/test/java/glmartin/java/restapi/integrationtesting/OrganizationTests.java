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
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.with;
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
    private Organization o3;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        appUserRepository.deleteAll();
        orgRepository.deleteAll();

        o1 = orgRepository.save(new Organization("Google", Status.ACTIVE));
        o2 = orgRepository.save(new Organization("Amazon", Status.PENDING));
        o3 = orgRepository.save(new Organization("FourV", Status.INACTIVE));

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
                .body("_embedded.organizationList", hasSize(3));
    }

    @Test
    void shouldGetSingleOrg() {
        given()
                .log().all()
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

    @Test
    void shouldCreateNewOrg() {
        with().body(new Organization("NewOrg"))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .request("POST", "/organizations")
                .then()
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/organizations")
                .then()
                .statusCode(200)
                .body("_embedded.organizationList", hasSize(4));
    }

    @Test
    void shouldDeactivateAnOrg() {
        given()
                .log().all()
                .when()
                .delete("/organizations/" + o1.getId() + "/deactivate")
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/organizations/" + o1.getId())
                .then()
                .statusCode(200)
                .body("status", equalTo("INACTIVE"));
    }

    @Test
    void shouldNotDeactivateADeactiveOrg() {
        given()
                .log().all()
                .when()
                .delete("/organizations/" + o3.getId() + "/deactivate")
                .then()
                .log().all()
                .statusCode(HttpStatus.METHOD_NOT_ALLOWED.value())
                .body("title", equalTo("Method not allowed"))
                .body("detail", equalTo("You can't deactivate an organization that is in the INACTIVE status"))
        ;
    }

    @Test
    void shouldActivateAnOrg() {
        given()
                .log().all()
                .when()
                .put("/organizations/" + o2.getId() + "/activate")
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/organizations/" + o2.getId())
                .then()
                .statusCode(200)
                .body("status", equalTo("ACTIVE"));
    }

    @Test
    void shouldNotActivateAnActiveOrg() {
        given()
                .log().all()
                .when()
                .put("/organizations/" + o1.getId() + "/activate")
                .then()
                .log().all()
                .statusCode(HttpStatus.METHOD_NOT_ALLOWED.value())
                .body("title", equalTo("Method not allowed"))
                .body("detail", equalTo("You can't activate an organization that is in the ACTIVE status"))
                ;
    }
}