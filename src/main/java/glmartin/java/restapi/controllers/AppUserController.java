package glmartin.java.restapi.controllers;

import java.util.List;
import java.util.stream.Collectors;

import glmartin.java.restapi.controllers.exceptions.ResourceNotFoundException;
import glmartin.java.restapi.entities.AppUser;
import glmartin.java.restapi.repositories.AppUserRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class AppUserController {

    private final AppUserRepository repository;
    private final AppUserModelAssembler assembler;

    public AppUserController(AppUserRepository repository, AppUserModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/app_users")
    CollectionModel<EntityModel<AppUser>> getAll() {

        // With hyperlinks that includes a URI and a rel property
        List<EntityModel<AppUser>> employees = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(employees,
                linkTo(methodOn(AppUserController.class)
                        .getAll())
                        .withSelfRel());
    }

    @PostMapping("/app_users")
    ResponseEntity<?> create(@RequestBody AppUser newAppUser) {
        EntityModel<AppUser> entityModel = assembler.toModel(repository.save(newAppUser));

        // return HTTP 201 Created with links to the new resource
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @GetMapping("/app_users/{id}")
    EntityModel<AppUser> get(@PathVariable Long id) {

        AppUser appUser = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("/app_users/" + id));

        // the assembler adds the links to the single entity resource
        return assembler.toModel(appUser);
    }

    @PutMapping("/app_users/{id}")
    ResponseEntity<?> update(@RequestBody AppUser newAppUser, @PathVariable Long id) {

        AppUser updatedAppUser = repository.findById(id)
                .map(employee -> {
                    employee.setFirstName(newAppUser.getFirstName());
                    employee.setLastName(newAppUser.getLastName());
                    employee.setEmail(newAppUser.getEmail());
                    return repository.save(employee);
                })
                .orElseGet(() -> {
                    newAppUser.setId(id);
                    return repository.save(newAppUser);
                });

        EntityModel<AppUser> entityModel = assembler.toModel(updatedAppUser);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/app_users/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}