package com.edwardawebb.stash.ssh.ao;

import java.util.Date;
import java.util.List;

import com.atlassian.stash.ssh.api.SshKey;
import com.atlassian.stash.user.StashUser;

public interface EnterpriseKeyRepository {
    
    SshKeyEntity createOrUpdateUserKey(StashUser user, String text, String label);
    
    boolean isValidKeyForUser(StashUser user, String text);

    List<SshKeyEntity> listOfExpiredKeyIds(Date oldestValidDate);

    void updateRecordWithKeyId(SshKeyEntity newRecord, SshKey newKey);

    void removeRecord(SshKeyEntity key);

}

