package com.lmig.forge.stash.ssh.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.atlassian.sal.api.user.UserManager;
import com.atlassian.stash.user.StashAuthenticationContext;
import com.atlassian.stash.user.StashUser;
import com.lmig.forge.stash.ssh.config.PluginSettingsService;

/**
 * A resource of message.
 */
@Path("/config")
public class AdminConfigResource {

    private final UserManager userManager;
    private final StashAuthenticationContext stashAuthenticationContext;
    private final PluginSettingsService pluginSettingsService;
    
    
    public AdminConfigResource(UserManager userManager, StashAuthenticationContext stashAuthenticationContext,
            PluginSettingsService pluginSettingsService) {
        super();
        this.userManager = userManager;
        this.stashAuthenticationContext = stashAuthenticationContext;
        this.pluginSettingsService = pluginSettingsService;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getConfig()
    {
        StashUser user = stashAuthenticationContext.getCurrentUser();
        if (user == null || !userManager.isSystemAdmin(user.getName())){
          return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.ok( pluginSettingsService.getAdminConfigResourcesModel()).build();      
    }
    
    @PUT
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateConfig(AdminConfigResourceModel updatedModel)
    {
        StashUser user = stashAuthenticationContext.getCurrentUser();
        if (user == null || !userManager.isSystemAdmin(user.getName())){
          return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.ok( pluginSettingsService.updateAdminConfigResourcesModel(updatedModel)).build();      
    }
}