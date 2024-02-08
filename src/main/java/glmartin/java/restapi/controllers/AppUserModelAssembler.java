package glmartin.java.restapi.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import glmartin.java.restapi.entities.AppUser;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

/**
 * Web Linking: https://datatracker.ietf.org/doc/html/rfc8288
 */
@Component
public class AppUserModelAssembler implements RepresentationModelAssembler<AppUser, EntityModel<AppUser>> {

    @Override
    public EntityModel<AppUser> toModel(AppUser appUser) {

        // used to create hyper-links in the resources
        return EntityModel.of(appUser,
                linkTo(methodOn(AppUserController.class).get(appUser.getId())).withSelfRel(),
                linkTo(methodOn(AppUserController.class).getAll()).withRel("app_users"));
    }
}
