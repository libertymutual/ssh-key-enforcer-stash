package com.edwardawebb.rest;

import java.security.KeyPair;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    private final UserService userService;

    public KeyDetailsResource(EnterpriseSshKeyService enterpriseKeyService,UserService userService) {
        this.enterpriseKeyService = enterpriseKeyService;
        this.userService = userService;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getMessage()
    {
       return Response.ok(new KeyDetailsResourceModel("Hello World")).build();
    }
    
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response generateNewPair(@QueryParam(value = "user") String username)
    {   
        StashUser user = userService.getUserByName(username);
        KeyPairResourceModel keyPair;
        try{
           keyPair = enterpriseKeyService.generateNewKeyPairFor(user);
        }catch(EnterpriseKeyGenerationException e){
            return Response.serverError().build();
        }
        
       return Response.ok(keyPair).build();
    }
}