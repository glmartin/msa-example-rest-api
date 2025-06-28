package glmartin.java.restapi.services;

import glmartin.java.restapi.entities.AppUser;
import glmartin.java.restapi.entities.Organization;
import glmartin.java.restapi.entities.Status;
import glmartin.java.restapi.exceptions.ResourceNotFoundException;
import glmartin.java.restapi.repositories.AppUserRepository;
import glmartin.java.restapi.repositories.OrganizationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collection;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class OrganizationServiceImplTest {

    @Autowired
    AppUserRepository appUserRepository;

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    OrganizationService organizationService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    private Organization o1;
    private Organization o2;
    private Organization o3;

    @BeforeEach
    public void setup() {
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition(TransactionDefinition.withDefaults());
        transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionTemplate = new TransactionTemplate(transactionManager, transactionDefinition);

        transactionTemplate.executeWithoutResult(status -> {
            o1 = organizationRepository.save(new Organization("Google", Status.ACTIVE));
            appUserRepository.save(new AppUser("Clark", "Kent", "ckent@o1.org", o1));
            appUserRepository.save(new AppUser("Lois", "Lane", "llane@o1.org", o1));
            appUserRepository.save(new AppUser("Bruce", "Wayne", "bwayne@o1.org", o1));
            o2 = organizationRepository.save(new Organization("Amazon", Status.PENDING));
            appUserRepository.save(new AppUser("Diana", "Prince", "dprince@o1.org", o2));
            appUserRepository.save(new AppUser("Lex", "Luthor", "lluthor@o2.org", o2));
            o3 = organizationRepository.save(new Organization("FourV", Status.INACTIVE));
        });
    }

    @AfterEach
    public void cleanUp() {
        transactionTemplate.executeWithoutResult(status -> {
            appUserRepository.deleteAll();
            organizationRepository.deleteAll();
        });
    }

    @Test
    void createOrganization() {
        Organization newOrg = new Organization("Opaq");
        Organization createdOrg = organizationService.createOrganization(newOrg);
        assertThat(createdOrg).isNotNull();
        assertThat(createdOrg.getName()).isEqualTo("Opaq");
        assertThat(createdOrg.getStatus()).isEqualTo(Status.PENDING);
    }

    @Test
    void updateOrganization() {
        o2.setName("FourV Systems");
        o2.setStatus(Status.ACTIVE);
        Organization updatedOrg = organizationService.updateOrganization(o2);
        assertThat(updatedOrg).isNotNull();
        assertThat(updatedOrg.getName()).isEqualTo("FourV Systems");
        assertThat(updatedOrg.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    void updateOrganizationNotExist() {
        Organization newOrg = new Organization("Microsoft");
        newOrg.setId(1111111L);
        assertThrows(ResourceNotFoundException.class, () ->
           organizationService.updateOrganization(newOrg)
        );
    }

    @Test
    void deleteOrganization() {
        Collection<Organization> orgs = organizationService.getOrganizations();
        int initialSize = orgs.size();
        organizationService.deleteOrganization(o1.getId());
        orgs = organizationService.getOrganizations();
        assertThat(orgs.size()).isEqualTo(initialSize-1);
    }

    @Test
    void getOrganizations() {
        Collection<Organization> orgs = organizationService.getOrganizations();
        assertThat(orgs).isNotNull();
        assertThat(orgs.size()).isEqualTo(3);
    }

    @Test
    void getOrganization() {
        Organization org = organizationService.getOrganization(o1.getId());
        assertThat(org).isNotNull();
        assertThat(org.getName()).isEqualTo("Google");
        assertThat(org.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    void getOrganizationByName() {
        Organization org = organizationService.getOrganizationByName("Google");
        assertThat(org).isNotNull();
        assertThat(org.getName()).isEqualTo("Google");
        assertThat(org.getStatus()).isEqualTo(Status.ACTIVE);
    }

    private static Stream<Arguments> testOrgUserArgs() {
        return Stream.of(
                arguments("Google",3),
                arguments("Amazon",2),
                arguments("FourV",0)
        );
    }

    @ParameterizedTest
    @MethodSource("testOrgUserArgs")
    void getOrgUsers(String orgName, int expectedUserSize) {
        transactionTemplate.executeWithoutResult(status -> {
            Organization org = organizationService.getOrganizationByName(orgName);
            Collection<AppUser> users = organizationService.getOrgUsers(org.getId());
            assertThat(users.size()).isEqualTo(expectedUserSize);
        });
    }
}