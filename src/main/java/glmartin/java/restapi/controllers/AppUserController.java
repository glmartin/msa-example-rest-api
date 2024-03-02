package glmartin.java.restapi.controllers;

import java.util.List;
import java.util.stream.Collectors;

import glmartin.java.restapi.exceptions.ResourceNotFoundException;
import glmartin.java.restapi.entities.AppUser;
import glmartin.java.restapi.repositories.AppUserRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/app_users")
public class AppUserController {

    private final AppUserRepository repository;
    private final AppUserModelAssembler assembler;

    public AppUserController(AppUserRepository repository, AppUserModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping
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

    @PostMapping
    ResponseEntity<?> create(@RequestBody AppUser newAppUser) {
        EntityModel<AppUser> entityModel = assembler.toModel(repository.save(newAppUser));

        // return HTTP 201 Created with links to the new resource
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @GetMapping("/{id}")
    EntityModel<AppUser> get(@PathVariable Long id) {

        AppUser appUser = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("/app_users/" + id));

        // the assembler adds the links to the single entity resource
        return assembler.toModel(appUser);
    }

    @PutMapping("/{id}")
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

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}