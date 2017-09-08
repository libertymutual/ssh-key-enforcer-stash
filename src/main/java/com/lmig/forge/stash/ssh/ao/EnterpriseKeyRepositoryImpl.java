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

import com.atlassian.bitbucket.IntegrityException;
import com.atlassian.bitbucket.i18n.KeyedMessage;
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
    /**
     * Only used for USER type keys of which an ID may only ever have 1 (by rules enforced here)
     */
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
                    log.debug("Updated existing key for user");
                } else {
                    key = ao.create(SshKeyEntity.class, new DBParam("TYPE", SshKeyEntity.KeyType.USER), new DBParam("USERID", user.getId()), new DBParam("TEXT", text), new DBParam("LABEL", label), new DBParam("CREATED", new Date()));
                    log.debug("created new key for user");
                }

                return key;
            }
        });
    }
    @Override
    public SshKeyEntity findSingleUserKey(ApplicationUser user) {
        SshKeyEntity[] keys = ao.find(SshKeyEntity.class, Query.select().where("USERID = ? AND TYPE = ?", user.getId(), KeyType.USER));
        if (null != keys && keys.length == 1) {
            SshKeyEntity key = keys[0];
            return key;
        } else {
            return null;
        }
    }

    @Override
    /**
     * Captures meta dat for any keys created directly in bamboo that did not pass through us initially, but was allowed afterwards
     */
    public SshKeyEntity saveExternallyGeneratedKeyDetails(SshKey key, ApplicationUser user, KeyType keyType) {
        return ao.executeInTransaction(new TransactionCallback<SshKeyEntity>() {
            @Override
            public SshKeyEntity doInTransaction() {
                SshKeyEntity entity = ao.create(SshKeyEntity.class, new DBParam("TYPE", keyType), new DBParam("USERID", user.getId()), new DBParam("KEYID", key.getId()), new DBParam("TEXT", key.getText()), new DBParam("LABEL", key.getLabel()), new DBParam("CREATED", new Date()));
                log.debug("Key with ID {} created", key.getId());
                return entity;
            }
        });
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
         ao.delete(key);
    }

    @Override
    public void forgetRecordMatching(SshKey key) {
       int recordsDeleted = ao.deleteWithSQL(SshKeyEntity.class, "KEYID = ?", key.getId());
       log.debug("Deleted " + recordsDeleted + " meta record related to key: " + key.getId());
    }

    @Override
    public SshKeyEntity updateKey(SshKeyEntity key) {
        return ao.executeInTransaction(new TransactionCallback<SshKeyEntity>() {
            @Override
            public SshKeyEntity doInTransaction() {
                key.save();
                return key;
            }
        });
    }

    @Override
    public SshKeyEntity findKeyByText(String keyText) {
        return ao.executeInTransaction(new TransactionCallback<SshKeyEntity>() {
            @Override
            public SshKeyEntity doInTransaction() {
                SshKeyEntity[] results = ao.find(SshKeyEntity.class, Query.select().where("TEXT = ?", keyText));
                if (results.length > 1) {
                    String message = "Data integrity issue, more than 1 record exists for Key with TEXT: " + keyText;
                    throw new IntegrityException(new KeyedMessage("duplicate-key", message, message));
                }
                if (results.length < 1) {
                    return null;
                }
                return results[0];
            }
        });
    }


    @Override
    public List<SshKeyEntity> keysForUser(ApplicationUser user) {
        SshKeyEntity[] results = ao.find(SshKeyEntity.class,Query.select().where("USERID = ?", user.getId()));
        return Lists.newArrayList(results);
    }



    }