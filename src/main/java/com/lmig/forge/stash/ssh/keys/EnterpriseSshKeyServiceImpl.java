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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.stash.ssh.api.SshKey;
import com.atlassian.stash.ssh.api.SshKeyService;
import com.atlassian.stash.user.StashUser;
import com.atlassian.stash.user.UserService;
import com.lmig.forge.stash.ssh.ao.EnterpriseKeyRepository;
import com.lmig.forge.stash.ssh.ao.SshKeyEntity;
import com.lmig.forge.stash.ssh.crypto.SshKeyPairGenerator;
import com.lmig.forge.stash.ssh.notifications.NotificationService;
import com.lmig.forge.stash.ssh.rest.KeyPairResourceModel;

public class EnterpriseSshKeyServiceImpl implements EnterpriseSshKeyService {
    final private static int NINETY_DAYS = 90;
    final public static String ALLOWED_SSH_GROUP ="ssh-gods";
    final private SshKeyService sshKeyService;
    final private EnterpriseKeyRepository enterpriseKeyRepository;
    final private SshKeyPairGenerator sshKeyPairGenerator;
    final private NotificationService notificationService;
    final private UserService userService;


    private static final Logger log = LoggerFactory.getLogger(EnterpriseSshKeyServiceImpl.class);

    public EnterpriseSshKeyServiceImpl(SshKeyService sshKeyService, EnterpriseKeyRepository enterpriseKeyRepository,
            SshKeyPairGenerator sshKeyPairGenerator, NotificationService notificationService, UserService userService) {
        this.sshKeyService = sshKeyService;
        this.enterpriseKeyRepository = enterpriseKeyRepository;
        this.sshKeyPairGenerator = sshKeyPairGenerator;
        this.notificationService = notificationService;
        this.userService = userService;

    }

    @Override
    public boolean isKeyValidForUser(SshKey key, StashUser stashUser) {
        //allow bamboo <> stash keys for system accounts in special group.
        if( userService.existsGroup(ALLOWED_SSH_GROUP) && userService.isUserInGroup(stashUser, ALLOWED_SSH_GROUP)){
            return true;
        }else{
            //otherwise user must have gone through our wrapper
            return enterpriseKeyRepository.isValidKeyForUser(stashUser, key.getText());
        }
    }

    @Override
    public void removeKeyIfNotLegal(SshKey key, StashUser user) {
        if (isKeyValidForUser(key, user)) {
            return;
        } else {
            sshKeyService.remove(key.getId());
            log.warn("Invalid or illegal key removed for user " + user.getId());
            // TODO issue custom audit event
        }
    }

    @Override
    public KeyPairResourceModel generateNewKeyPairFor(StashUser user) {
        String keyComment = "SYSTEM GENERATED";
        KeyPairResourceModel result = sshKeyPairGenerator.generateKeyPair(keyComment);
        // must add to our repo before calling stash SSH service since audit
        // listener will otherwise revoke it.
        SshKeyEntity newRecord = enterpriseKeyRepository.createOrUpdateUserKey(user, result.getPublicKey(), keyComment);
        sshKeyService.removeAllForUser(user);
        SshKey newKey = sshKeyService.addForUser(user, result.getPublicKey());
        enterpriseKeyRepository.updateRecordWithKeyId(newRecord, newKey);
        return result;
    }

    @Override
    public void replaceExpiredKeysAndNotifyUsers() {
        Date today = new Date();
        Calendar cal = new GregorianCalendar();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_YEAR, -NINETY_DAYS);
        //cal.add(Calendar.MINUTE, -1); //live demo in UI. 
        List<SshKeyEntity> expiredStashKeys = enterpriseKeyRepository.listOfExpiredKeyIds(cal.getTime());
   
        
        for (SshKeyEntity keyRecord : expiredStashKeys) {
            try{
                log.info("Removing Key for user " + keyRecord.getUserId());
                sshKeyService.remove(keyRecord.getKeyId());
                enterpriseKeyRepository.removeRecord(keyRecord);
                notificationService.notifyUserOfExpiredKey(keyRecord.getUserId());
                log.info("Key Removed");
            }catch(Exception e){
                log.error("Key removal failed for user: " + keyRecord.getUserId());
                e.printStackTrace();
            }
        }
    }
    
   

}