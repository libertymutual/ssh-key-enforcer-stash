package com.edwardawebb.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.atlassian.stash.user.StashAuthenticationContext;
import com.atlassian.stash.user.StashUser;
import com.atlassian.stash.user.UserService;
import com.edwardawebb.stash.ssh.EnterpriseKeyGenerationException;
import com.edwardawebb.stash.ssh.EnterpriseSshKeyService;

/**
 * A resource of message.
 */
@Path("/keys")
public class KeyDetailsResource {
    private final EnterpriseSshKeyService enterpriseKeyService;
    private final StashAuthenticationContext stashAuthenticationContext;


    public KeyDetailsResource(EnterpriseSshKeyService enterpriseKeyService,StashAuthenticationContext stashAuthenticationContext) {
        this.enterpriseKeyService = enterpriseKeyService;
        this.stashAuthenticationContext = stashAuthenticationContext;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getMessage()
    {
       return Response.ok(new KeyDetailsResourceModel("Hello World")).build();
    }
    
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response generateNewPair()
    {   
        
        StashUser user = stashAuthenticationContext.getCurrentUser();
        KeyPairResourceModel keyPair;
        try{
           keyPair = enterpriseKeyService.generateNewKeyPairFor(user);
        }catch(EnterpriseKeyGenerationException e){
            return Response.serverError().build();
        }
        
       return Response.ok(keyPair).build();
    }
}