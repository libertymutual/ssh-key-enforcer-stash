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

package com.lmig.forge.stash.ssh.keys;

import java.util.List;

import com.atlassian.bitbucket.ssh.SshKey;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.lmig.forge.stash.ssh.ao.SshKeyEntity;
import com.lmig.forge.stash.ssh.events.GeneralEventListener;
import com.lmig.forge.stash.ssh.rest.KeyDetailsResourceModel;
import com.lmig.forge.stash.ssh.rest.KeyPairResourceModel;

public interface EnterpriseSshKeyService {

    /**
     * is the key in question the registered key for the current period?
     * @param key
     * @param stashUser
     * @return
     */
    boolean isKeyValidForUser(SshKey key, ApplicationUser stashUser);


  

    /**
     * Called by {@link GeneralEventListener} to block
     * user attempts to create their keys outside the enterprise services.
     * @param key
     * @param user
     */
    void removeKeyIfNotLegal(SshKey key, ApplicationUser user);



    /**
     * returns a pre-persisted key pair for the user (we only saved the public one though..)
     * Operation should return both values to user for saving offline.
     * @param authorizedUser
     * @return
     */
    KeyPairResourceModel generateNewKeyPairFor(ApplicationUser authorizedUser);




    void replaceExpiredKeysAndNotifyUsers();



    /**
     * Remove our meta for any keys deleted via traditional UI/API
     * @param key
     */
    void forgetDeletedKey(SshKey key);




    List<SshKeyEntity> getKeysForUser(String username);

}