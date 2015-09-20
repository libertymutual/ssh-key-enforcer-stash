package com.edwardawebb.stash.ssh;

import com.atlassian.stash.ssh.SshKeyCreatedEvent;
import com.atlassian.stash.ssh.api.SshKey;
import com.atlassian.stash.user.StashUser;
import com.edwardawebb.stash.GeneralEventListener;

public interface EnterpriseSshKeyService {

    /**
     * is the key in question the registered key for the current period?
     * @param key
     * @param stashUser
     * @return
     */
    boolean isKeyValidForUser(SshKey key, StashUser stashUser);


  

    /**
     * Called on {@link SshKeyCreatedEvent} by {@link GeneralEventListener} to block 
     * user attempts to create their keys outside the enterprise services.
     * @param key
     * @param user
     */
    void removeKeyIfNotLegal(SshKey key, StashUser user);

}