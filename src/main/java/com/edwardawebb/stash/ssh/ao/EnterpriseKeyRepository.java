package com.edwardawebb.stash.ssh.ao;

import java.util.List;

import com.atlassian.stash.ssh.api.SshKey;
import com.atlassian.stash.user.StashUser;

public interface EnterpriseKeyRepository {
    
    SshKeyEntity createOrUpdateUserKey(StashUser user, String text, String label);
    
   // SshKeyEntity findCurrentValidKeyForUser(String username);
    
    boolean isValidKeyForUser(StashUser user, String text);

    List<Integer> listOfExpiredKeyIds();

    void updateRecordWithKeyId(SshKeyEntity newRecord, SshKey newKey);

}
