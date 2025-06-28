package glmartin.java.restapi.services;

import glmartin.java.restapi.entities.AppUser;
import glmartin.java.restapi.entities.Organization;

import java.util.Collection;

public interface OrganizationService {
    public abstract Organization createOrganization(Organization organization);
    public abstract Organization updateOrganization(Organization organization);
    public abstract void deleteOrganization(Long id);
    public abstract Collection<Organization> getOrganizations();

    Organization getOrganization(Long id);
    Organization getOrganizationByName(String orgName);

    Collection<AppUser> getOrgUsers(Long orgId);
}
