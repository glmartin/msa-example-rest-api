package glmartin.java.restapi.services;

import glmartin.java.restapi.entities.AppUser;
import glmartin.java.restapi.entities.Organization;
import glmartin.java.restapi.entities.Status;
import glmartin.java.restapi.exceptions.ResourceNotFoundException;
import glmartin.java.restapi.repositories.AppUserRepository;
import glmartin.java.restapi.repositories.OrganizationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository orgRepository;
    private final AppUserRepository appUserRepository;

    public OrganizationServiceImpl(OrganizationRepository orgRepository, AppUserRepository appUserRepository) {
        this.orgRepository = orgRepository;
        this.appUserRepository = appUserRepository;
    }

    @Override
    public Organization createOrganization(Organization organization) {
        organization.setStatus(Status.PENDING);
        return orgRepository.save(organization);
    }

    @Override
    public Organization updateOrganization(Organization organization) {
        return orgRepository.findById(organization.getId())
                .map(org -> {
                    org.setName(organization.getName());
                    org.setStatus(organization.getStatus());
                    return orgRepository.save(org);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Organization Not Found"));
    }

    @Override
    @Transactional
    public void deleteOrganization(Long id) {
        appUserRepository.deleteByOrganizationId(id);
        orgRepository.deleteById(id);
    }

    @Override
    public Collection<Organization> getOrganizations() {
        return orgRepository.findAll();
    }

    @Override
    public Organization getOrganization(Long id) {
        return orgRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization Not Found"));
    }

    @Override
    public Organization getOrganizationByName(String orgName) {
        return orgRepository.findByName(orgName)
                .orElseThrow(() -> new ResourceNotFoundException("Organization Not Found"));
    }

    @Override
    public Collection<AppUser> getOrgUsers(Long orgId) {
        return orgRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization Not Found"))
                .getAppUsers();
    }
}
