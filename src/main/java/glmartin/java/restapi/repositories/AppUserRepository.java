package glmartin.java.restapi.repositories;

import glmartin.java.restapi.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
}
