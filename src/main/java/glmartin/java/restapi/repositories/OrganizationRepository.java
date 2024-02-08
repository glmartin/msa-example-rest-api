package glmartin.java.restapi.repositories;

import glmartin.java.restapi.entities.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository  extends JpaRepository<Organization, Long> {
}
