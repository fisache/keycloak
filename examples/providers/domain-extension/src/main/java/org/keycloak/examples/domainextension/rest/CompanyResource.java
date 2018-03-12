package org.keycloak.examples.domainextension.rest;

import org.apache.http.HttpStatus;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.examples.domainextension.CompanyRepresentation;
import org.keycloak.examples.domainextension.spi.ExampleService;
import org.keycloak.forms.account.freemarker.Templates;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.theme.BrowserSecurityHeaderSetup;
import org.keycloak.theme.FreeMarkerException;
import org.keycloak.theme.FreeMarkerUtil;
import org.keycloak.theme.Theme;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CompanyResource {
    private static final Logger logger = Logger.getLogger(CompanyResource.class);

	private final KeycloakSession session;
	private final FreeMarkerUtil freeMarker;
	private final UserModel user;
	
	public CompanyResource(KeycloakSession session, FreeMarkerUtil freeMarker, UserModel user) {
		this.session = session;
		this.freeMarker = freeMarker;
		this.user = user;
	}

//    @GET
//    @Path("")
//    @NoCache
//    @Produces(MediaType.APPLICATION_JSON)
//    public List<CompanyRepresentation> getCompanies() {
//        return session.getProvider(ExampleService.class).listCompanies();
//    }


    @GET
    @Path("")
    @NoCache
    @Produces(MediaType.TEXT_HTML)
    public Response getCompanies() {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("user", user);

        attributes.put("companies", session.getProvider(ExampleService.class).listCompanies());

        Theme theme;
        try {
            theme = getTheme();
        } catch (IOException e) {
            logger.error("Failed to create theme", e);
            return Response.serverError().build();
        }

        Locale locale = session.getContext().resolveLocale(user);
        return processTemplate(theme, attributes, locale);
    }

    @POST
    @Path("")
    @NoCache
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCompany(CompanyRepresentation rep) {
        session.getProvider(ExampleService.class).addCompany(rep);
        return Response.created(session.getContext().getUri().getAbsolutePathBuilder().path(rep.getId()).build()).build();
    }

    @GET
    @NoCache
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public CompanyRepresentation getCompany(@PathParam("id") final String id) {
        return session.getProvider(ExampleService.class).findCompany(id);
    }

    protected Response processTemplate(Theme theme, Map<String, Object> attributes, Locale locale) {
        try {
            String result = freeMarker.processTemplate(attributes, "company.ftl", theme);
            Response.ResponseBuilder builder = Response.status(Response.Status.OK).type(org.keycloak.utils.MediaType.TEXT_HTML_UTF_8_TYPE).language(locale).entity(result);
            BrowserSecurityHeaderSetup.headers(builder, session.getContext().getRealm());
            return builder.build();
        } catch (FreeMarkerException e) {
            logger.error("Failed to process template", e);
            return Response.serverError().build();
        }
    }

    protected Theme getTheme() throws IOException {
        return session.theme().getTheme(Theme.Type.ACCOUNT);
    }

}