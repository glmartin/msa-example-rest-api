package glmartin.java.restapi.controllers;

import glmartin.java.restapi.controllers.exceptions.ResourceNotFoundException;
import glmartin.java.restapi.entities.AppUser;
import glmartin.java.restapi.entities.Status;
import glmartin.java.restapi.entities.Organization;
import glmartin.java.restapi.repositories.OrganizationRepository;
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
public class OrganizationController {

    private final OrganizationRepository orgRepository;
    private final OrganizationModelAssembler assembler;
    private final AppUserModelAssembler appUserAssembler;

    public OrganizationController(OrganizationRepository orgRepository, OrganizationModelAssembler assembler, AppUserModelAssembler appUserAssembler) {
        this.orgRepository = orgRepository;
        this.assembler = assembler;
        this.appUserAssembler = appUserAssembler;
    }

    @GetMapping("/organizations")
    CollectionModel<EntityModel<Organization>> getAll() {

        List<EntityModel<Organization>> organizations = orgRepository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(organizations,
                linkTo(methodOn(OrganizationController.class).getAll()).withSelfRel());
    }

    @GetMapping("/organizations/{id}")
    EntityModel<Organization> get(@PathVariable Long id) {

        Organization order = orgRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("/organizations/" + id));

        return assembler.toModel(order);
    }

    @GetMapping("/organizations/{id}/app_users")
    CollectionModel<EntityModel<AppUser>> getOrgUsers(@PathVariable Long id) {

        Organization org = orgRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("/organizations/" + id + "/app_users"));

        List<EntityModel<AppUser>> networkUsers = org.getAppUsers().stream()
                .map(appUserAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(networkUsers,
                linkTo(methodOn(AppUserController.class).getAll()).withSelfRel());
    }

    @PostMapping("/organizations")
    ResponseEntity<EntityModel<Organization>> create(@RequestBody Organization order) {

        order.setStatus(Status.PENDING);
        Organization newOrganization = orgRepository.save(order);

        return ResponseEntity
                .created(linkTo(methodOn(OrganizationController.class).get(newOrganization.getId())).toUri())
                .body(assembler.toModel(newOrganization));
    }

    @DeleteMapping("/organizations/{id}/deactivate")
    ResponseEntity<?> deactivate(@PathVariable Long id) {

        Organization order = orgRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("/organizations/" + id + "/deactivate"));

        if (order.getStatus() == Status.ACTIVE) {
            order.setStatus(Status.DEFUNCT);
            return ResponseEntity.ok(assembler.toModel(orgRepository.save(order)));
        }

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method not allowed")
                        .withDetail("You can't deactivate an order that is in the " + order.getStatus() + " status"));
    }

    @PutMapping("/organizations/{id}/activate")
    ResponseEntity<?> activate(@PathVariable Long id) {

        Organization order = orgRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("/organizations/" + id + "/activate"));

        if (order.getStatus() == Status.PENDING) {
            order.setStatus(Status.ACTIVE);
            return ResponseEntity.ok(assembler.toModel(orgRepository.save(order)));
        }

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method not allowed")
                        .withDetail("You can't complete an order that is in the " + order.getStatus() + " status"));
    }
}