package glmartin.java.restapi.integrationtesting;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Map;

import static org.testcontainers.containers.PostgreSQLContainer.POSTGRESQL_PORT;

/**
 * Parent class for running Integration Tests. This class contains the code to start up the PostgreSQL DB
 * and run the migrations for testing the controller using HTTP requests.
 */
public abstract class RestIntegrationTester {
    private static final String PSQL_IMAGE = "postgres:15-alpine";
    private static final String DB_MIG_IMAGE = "local/msa-example-db-mgmt:1.0.1";
    private static final String TEST_DB_NAME = "testdb";

    // Network for the containers to communicate on
    private static final Network network = Network.newNetwork();
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(PSQL_IMAGE)
            .withNetwork(network)
            .withNetworkAliases(TEST_DB_NAME)
            .withNetworkMode(network.getId())
            .waitingFor(
                    new WaitAllStrategy(WaitAllStrategy.Mode.WITH_MAXIMUM_OUTER_TIMEOUT)
                            .withStartupTimeout(Duration.ofSeconds(90))
                            .withStrategy(Wait.forLogMessage(".*database system is ready to accept connections.*\\s", 2))
                            .withStrategy(Wait.forListeningPort())
            )
            .withExposedPorts(POSTGRESQL_PORT);

    @BeforeAll
    static void beforeAll() {
        // start the PostgreSQL container
        postgres.start();

        // Start the DB Migrations container (this needs to be run after the postgres container starts)
        // This container will stop on its own
        new GenericContainer<>(DockerImageName.parse(DB_MIG_IMAGE))
                .withEnv(
                        Map.of(
                                "DB_NAME", postgres.getDatabaseName(),
                                "DB_PORT", String.valueOf(POSTGRESQL_PORT),
                                "DB_HOST", TEST_DB_NAME,
                                "DB_USERNAME", postgres.getUsername(),
                                "DB_PASSWORD", postgres.getPassword()
                        )
                )
                .withNetwork(network)
                .dependsOn(postgres)
                .waitingFor(Wait.forLogMessage(".*Database Migrations Complete.*\\n", 1))
                .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("DBMigrations")))
                .start();
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
}
