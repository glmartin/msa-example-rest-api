package glmartin.java.restapi.controllers;

import glmartin.java.restapi.entities.Status;
import glmartin.java.restapi.entities.Organization;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrganizationModelAssembler implements RepresentationModelAssembler<Organization, EntityModel<Organization>> {

    @Override
    public EntityModel<Organization> toModel(Organization org) {

        // Add links to the all GET requests
        EntityModel<Organization> orgModel = EntityModel.of(org,
                linkTo(methodOn(OrganizationController.class).get(org.getId())).withSelfRel(),
                linkTo(methodOn(OrganizationController.class).getOrgUsers(org.getId())).withRel("users"),
                linkTo(methodOn(OrganizationController.class).getAll()).withRel("organizations"));

        // when the state is IN_PROGRESS, only show cancel and complete links
        if (org.getStatus() == Status.PENDING) {
            orgModel.add(linkTo(methodOn(OrganizationController.class).deactivate(org.getId())).withRel("deactivate"));
            orgModel.add(linkTo(methodOn(OrganizationController.class).activate(org.getId())).withRel("activate"));
        }

        return orgModel;
    }
}