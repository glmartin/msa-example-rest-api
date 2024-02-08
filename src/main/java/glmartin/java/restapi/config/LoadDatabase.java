package glmartin.java.restapi.config;

import glmartin.java.restapi.entities.Organization;
import glmartin.java.restapi.entities.Status;
import glmartin.java.restapi.entities.AppUser;
import glmartin.java.restapi.repositories.OrganizationRepository;
import glmartin.java.restapi.repositories.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {

    @Value( "${data.preload.samples:false}" )
    private Boolean loadData;

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(AppUserRepository appUserRepository, OrganizationRepository orgRepository) {

        return args -> {
            if (loadData) {
                Organization o1 = orgRepository.save(new Organization("Google", Status.ACTIVE));
                Organization o2 = orgRepository.save(new Organization("Amazon", Status.PENDING));

                orgRepository.findAll().forEach(org -> {
                    log.info("Preloaded " + org);
                });

                appUserRepository.save(new AppUser("Clark", "Kent", "ckent@o1.org", o1));
                appUserRepository.save(new AppUser("Lois", "Lane", "llane@o1.org", o1));
                appUserRepository.save(new AppUser("Bruce", "Wayne", "bwayne@o1.org", o1));
                appUserRepository.save(new AppUser("Diana", "Prince", "dprince@o1.org", o1));
                appUserRepository.save(new AppUser("Lex", "Luthor", "lluthor@o2.org", o2));
                appUserRepository.save(new AppUser("General", "Zod", "gzod@o2.org", o2));
                appUserRepository.save(new AppUser("Dooms", "Day", "dday@o2.org", o2));

                appUserRepository.findAll().forEach(user -> log.info("Preloaded " + user));
            } else {
                log.info("Skipping Sample Data Preload");
            }
        };
    }
}
