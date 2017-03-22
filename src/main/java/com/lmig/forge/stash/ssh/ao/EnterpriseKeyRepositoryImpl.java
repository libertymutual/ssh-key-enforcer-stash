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

package com.lmig.forge.stash.ssh.ao;

import java.util.Date;
import java.util.List;

import net.java.ao.DBParam;
import net.java.ao.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.bitbucket.ssh.SshKey;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.google.common.collect.Lists;
import com.lmig.forge.stash.ssh.ao.SshKeyEntity.KeyType;

public class EnterpriseKeyRepositoryImpl implements EnterpriseKeyRepository {
    
    
    private static final Logger log = LoggerFactory.getLogger(EnterpriseKeyRepositoryImpl.class);

    private final ActiveObjects ao;

    public EnterpriseKeyRepositoryImpl(ActiveObjects ao) {
        this.ao = ao;
    }

    @Override
    public SshKeyEntity createOrUpdateUserKey(ApplicationUser user, String text, String label) {
        return ao.executeInTransaction(new TransactionCallback<SshKeyEntity>() {

            @Override
            public SshKeyEntity doInTransaction() {
                SshKeyEntity key = findSingleUserKey(user);
                if (null != key) {
                    key.setText(text);
                    key.setLabel(label);
                    key.setCreatedDate(new Date());
                    key.save();
                } else {
                    key = ao.create(SshKeyEntity.class, new DBParam("TYPE", SshKeyEntity.KeyType.USER), new DBParam("USERID", user.getId()), new DBParam("TEXT", text), new DBParam("LABEL", label), new DBParam("CREATED", new Date()));
                }
                return key;
            }
        });
    }
    

    @Override
    public SshKeyEntity saveExternallyGeneratedKeyDetails(SshKey key, ApplicationUser user, KeyType keyType) {
        SshKeyEntity entity =ao.create(SshKeyEntity.class, new DBParam("TYPE",keyType), new DBParam("USERID", user.getId()),  new DBParam("KEYID",key.getId()), new DBParam("TEXT", key.getText()),new DBParam("LABEL",key.getLabel()),new DBParam("CREATED", new Date()));
        return entity;
    }

    @Override
    public SshKeyEntity findSingleUserKey(ApplicationUser user) {
        SshKeyEntity[] keys = ao.find(SshKeyEntity.class, Query.select().where("USERID = ? AND TYPE = ?", user.getId(), KeyType.USER));
        if( null != keys && keys.length == 1 ){
            SshKeyEntity key = keys[0];
            return key;
        }else{
            return null;
        }
    }

    @Override
    public List<SshKeyEntity> listOfExpiredKeys(Date oldestValidDate, KeyType keyType) {
        SshKeyEntity[] results = ao.find(SshKeyEntity.class, Query.select().where("CREATED < ? and TYPE = ?",oldestValidDate,keyType));
        return Lists.newArrayList(results);
    }

    @Override
    public void updateRecordWithKeyId(final SshKeyEntity newRecord, final SshKey stashKey ) {
        ao.executeInTransaction(new TransactionCallback<Void>() {

            @Override
            public Void doInTransaction() {
                SshKeyEntity updated = ao.get(SshKeyEntity.class, newRecord.getID());
                updated.setKeyId(stashKey.getId());
                updated.save();
                return null;
            }
        });
    }

    @Override
    public void removeRecord(SshKeyEntity key) {
        //SshKeyEntity[] recordToDelete = ao.find(SshKeyEntity.class, Query.select().where("KEYID = ?",stashKeyId));
        ao.delete(key);
    }

    @Override
    public void forgetRecordMatching(SshKey key) {
       int recordsDeleted = ao.deleteWithSQL(SshKeyEntity.class, "KEYID = ?", key.getId());
       log.warn("Deleted " + recordsDeleted + " meta record related to key: " + key.getId());
        
    }

    @Override
    public List<SshKeyEntity> keysForUser(ApplicationUser user) {
        SshKeyEntity[] results = ao.find(SshKeyEntity.class,Query.select().where("USERID = ?", user.getId()));
        return Lists.newArrayList(results);
    }

    
    
}