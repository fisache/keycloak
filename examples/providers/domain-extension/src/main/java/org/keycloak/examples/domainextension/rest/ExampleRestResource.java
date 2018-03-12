package org.keycloak.examples.domainextension.rest;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.theme.FreeMarkerUtil;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Path;

public class ExampleRestResource {

	private final KeycloakSession session;
    private final AuthenticationManager.AuthResult auth;
    private final FreeMarkerUtil freeMarker;
	
	public ExampleRestResource(KeycloakSession session, FreeMarkerUtil freeMarker) {
		this.session = session;
		this.freeMarker = freeMarker;
//        this.auth = new AppAuthManager().authenticateBearerToken(session, session.getContext().getRealm());
        this.auth = new AppAuthManager().authenticateIdentityCookie(session, session.getContext().getRealm());
	}
	
    @Path("companies")
    public CompanyResource getCompanyResource() {
        return new CompanyResource(session, freeMarker, auth.getUser());
    }

    // Same like "companies" endpoint, but REST endpoint is authenticated with Bearer token and user must be in realm role "admin"
    // Just for illustration purposes
    @Path("companies-auth")
    public CompanyResource getCompanyResourceAuthenticated() {
//        checkRealmAdmin();
        return new CompanyResource(session, freeMarker, auth.getUser());
    }

    private void checkRealmAdmin() {
        if (auth == null) {
            throw new NotAuthorizedException("Bearer");
        } else if (auth.getToken().getRealmAccess() == null || !auth.getToken().getRealmAccess().isUserInRole("admin")) {
            throw new ForbiddenException("Does not have realm admin role");
        }
    }

}
