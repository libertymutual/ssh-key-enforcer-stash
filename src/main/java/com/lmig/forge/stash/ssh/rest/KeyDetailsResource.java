/*
 * Copyright 2015, Liberty Mutual Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lmig.forge.stash.ssh.rest;

import com.atlassian.annotations.PublicApi;
import com.atlassian.bitbucket.auth.AuthenticationContext;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.bitbucket.user.UserService;
import com.lmig.forge.stash.ssh.EnterpriseKeyGenerationException;
import com.lmig.forge.stash.ssh.ao.SshKeyEntity;
import com.lmig.forge.stash.ssh.keys.EnterpriseSshKeyService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * A resource of message.
 */
@Path("/keys")
@PublicApi
public class KeyDetailsResource {
    private final EnterpriseSshKeyService enterpriseKeyService;
    private final AuthenticationContext stashAuthenticationContext;
    private final UserService userService;

    public KeyDetailsResource(EnterpriseSshKeyService enterpriseKeyService,
                              AuthenticationContext stashAuthenticationContext, UserService userService) {
        this.enterpriseKeyService = enterpriseKeyService;
        this.stashAuthenticationContext = stashAuthenticationContext;
        this.userService = userService;
    }


    /**
     * Generate a new Key for this user per rules of service (1 active key, etc)
     */
    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response generateNewPair() {

        ApplicationUser user = stashAuthenticationContext.getCurrentUser();
        KeyPairResourceModel keyPair;
        try {
            keyPair = enterpriseKeyService.generateNewKeyPairFor(user);
        } catch (EnterpriseKeyGenerationException e) {
            return Response.serverError().build();
        }

        return Response.ok(keyPair).build();
    }

    @GET
    @Path("/user/{username}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getAllKeysForUser(@PathParam("username") String username) {
        ApplicationUser user = userService.getUserBySlug(username);
        try {
            List<SshKeyEntity> keyEntities = enterpriseKeyService.getKeysForUser(username);
            List<KeyDetailsResourceModel> keyResources = new ArrayList<KeyDetailsResourceModel>();
            for (SshKeyEntity keyEntity : keyEntities) {
                keyResources.add(KeyDetailsResourceModel.from(keyEntity,user));
            }
            return Response.ok(keyResources).build();
        } catch (EnterpriseKeyGenerationException e) {
            return Response.serverError().build();
        }

    }

}