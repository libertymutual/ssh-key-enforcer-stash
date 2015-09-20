package com.edwardawebb.stash.ssh;

import com.atlassian.stash.user.StashUser;

public interface EnterpriseKeyRepository {
    
    void createOrUpdateUserKey(StashUser user, String text, String label);
    
   // SshKeyEntity findCurrentValidKeyForUser(String username);
    
    boolean isValidKeyForUser(StashUser user, String text);

}
