package glmartin.java.restapi.controllers;

import glmartin.java.restapi.entities.AppUser;
import glmartin.java.restapi.entities.Status;
import glmartin.java.restapi.entities.Organization;
import glmartin.java.restapi.services.OrganizationService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {

    private final OrganizationService orgService;
    private final OrganizationModelAssembler assembler;
    private final AppUserModelAssembler appUserAssembler;

    public OrganizationController(OrganizationService orgService, OrganizationModelAssembler assembler, AppUserModelAssembler appUserAssembler) {
        this.orgService = orgService;
        this.assembler = assembler;
        this.appUserAssembler = appUserAssembler;
    }

    @GetMapping
    CollectionModel<EntityModel<Organization>> getAll() {

        List<EntityModel<Organization>> organizations = orgService.getOrganizations().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(organizations,
                linkTo(methodOn(OrganizationController.class).getAll()).withSelfRel());
    }

    @GetMapping("/{id}")
    EntityModel<Organization> get(@PathVariable Long id) {
        return assembler.toModel(orgService.getOrganization(id));
    }

    @GetMapping("/{id}/app_users")
    CollectionModel<EntityModel<AppUser>> getOrgUsers(@PathVariable Long id) {
        List<EntityModel<AppUser>> networkUsers = orgService.getOrgUsers(id).stream()
                .map(appUserAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(networkUsers,
                linkTo(methodOn(AppUserController.class).getAll()).withSelfRel());
    }

    @PostMapping
    ResponseEntity<EntityModel<Organization>> create(@RequestBody Organization organization) {
        Organization newOrganization = orgService.createOrganization(organization);

        return ResponseEntity
                .created(linkTo(methodOn(OrganizationController.class).get(newOrganization.getId())).toUri())
                .body(assembler.toModel(newOrganization));
    }

    @DeleteMapping("/{id}")
    ResponseEntity.BodyBuilder delete(@PathVariable Long id) {
        orgService.deleteOrganization(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/deactivate")
    ResponseEntity<?> deactivate(@PathVariable Long id) {

        Organization organization = orgService.getOrganization(id);

        if (organization.getStatus() == Status.ACTIVE) {
            organization.setStatus(Status.INACTIVE);
            return ResponseEntity.ok(assembler.toModel(orgService.updateOrganization(id, organization)));
        }

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method not allowed")
                        .withDetail("You can't deactivate an organization that is in the " + organization.getStatus() + " status"));
    }

    @PutMapping("/{id}/activate")
    ResponseEntity<?> activate(@PathVariable Long id) {

        Organization order = orgService.getOrganization(id);

        if (order.getStatus() == Status.PENDING) {
            order.setStatus(Status.ACTIVE);
            return ResponseEntity.ok(assembler.toModel(orgService.updateOrganization(id, order)));
        }

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method not allowed")
                        .withDetail("You can't activate an organization that is in the " + order.getStatus() + " status"));
    }
}