package glmartin.java.restapi.repositories;

import glmartin.java.restapi.entities.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationRepository  extends JpaRepository<Organization, Long> {
    Optional<Organization> findByName(String orgName);
}
