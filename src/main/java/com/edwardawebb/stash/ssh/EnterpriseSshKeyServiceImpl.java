package com.edwardawebb.stash.ssh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.stash.ssh.api.SshKey;
import com.atlassian.stash.ssh.api.SshKeyService;
import com.atlassian.stash.user.StashUser;

public class EnterpriseSshKeyServiceImpl implements EnterpriseSshKeyService{
    
    final private SshKeyService sshKeyService;
    final private EnterpriseKeyRepository enterpriseKeyRepository;
    
    private static final Logger log = LoggerFactory.getLogger(EnterpriseSshKeyServiceImpl.class);

    
    public EnterpriseSshKeyServiceImpl(SshKeyService sshKeyService,EnterpriseKeyRepository enterpriseKeyRepository) {
        this.sshKeyService = sshKeyService;
        this.enterpriseKeyRepository = enterpriseKeyRepository;
    }

    @Override
    public boolean isKeyValidForUser(SshKey key, StashUser stashUser) {
        return  enterpriseKeyRepository.isValidKeyForUser(stashUser, key.getText());
    }


    @Override
    public void removeKeyIfNotLegal(SshKey key, StashUser user) {
        if (isKeyValidForUser(key, user)){
            return;
        }else{
            sshKeyService.remove(key.getId());
            log.warn("Invalid or illegal key removed for user " + user.getId());
            // TODO issue custom audit event
        }
    }
 


}