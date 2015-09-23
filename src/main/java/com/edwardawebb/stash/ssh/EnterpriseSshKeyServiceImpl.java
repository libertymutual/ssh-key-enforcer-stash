package com.edwardawebb.stash.ssh;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.stash.ssh.api.SshKey;
import com.atlassian.stash.ssh.api.SshKeyService;
import com.atlassian.stash.user.StashUser;
import com.edwardawebb.rest.KeyPairResourceModel;
import com.edwardawebb.stash.ssh.ao.EnterpriseKeyRepository;
import com.edwardawebb.stash.ssh.ao.SshKeyEntity;
import com.edwardawebb.stash.ssh.crypto.SshKeyPairGenerator;

public class EnterpriseSshKeyServiceImpl implements EnterpriseSshKeyService{
    
    final private SshKeyService sshKeyService;
    final private EnterpriseKeyRepository enterpriseKeyRepository;
    final private SshKeyPairGenerator sshKeyPairGenerator;
    
    private static final Logger log = LoggerFactory.getLogger(EnterpriseSshKeyServiceImpl.class);
   
    
    public EnterpriseSshKeyServiceImpl(SshKeyService sshKeyService,EnterpriseKeyRepository enterpriseKeyRepository,SshKeyPairGenerator sshKeyPairGenerator) {
        this.sshKeyService = sshKeyService;
        this.enterpriseKeyRepository = enterpriseKeyRepository;
        this.sshKeyPairGenerator = sshKeyPairGenerator;
        
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

    @Override
    public KeyPairResourceModel generateNewKeyPairFor(StashUser user) {
        String keyComment =  "SYSTEM GENERATED"; 
        KeyPairResourceModel result = sshKeyPairGenerator.generateKeyPair( keyComment);
        //must add to our repo before calling stash SSH service since audit listener will otherwise revoke it.
        SshKeyEntity newRecord = enterpriseKeyRepository.createOrUpdateUserKey(user, result.getPublicKey(), keyComment);
        sshKeyService.removeAllForUser(user);
        SshKey newKey = sshKeyService.addForUser(user, result.getPublicKey());
        enterpriseKeyRepository.updateRecordWithKeyId(newRecord,newKey);
        return result;
    }

    @Override
    public void replaceExpiredKeysAndNotifyUsers() {
       List<Integer> expiredStashKeys = enterpriseKeyRepository.listOfExpiredKeyIds();
        
       for (Integer integer : expiredStashKeys) {
           sshKeyService.remove(integer);
           log.warn("Expired Key: " + integer);
           
       }
    }
 


}