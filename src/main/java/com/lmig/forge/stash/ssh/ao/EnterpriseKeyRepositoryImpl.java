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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import net.java.ao.DBParam;
import net.java.ao.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.stash.ssh.api.SshKey;
import com.atlassian.stash.user.StashUser;
import com.google.common.collect.Lists;

public class EnterpriseKeyRepositoryImpl implements EnterpriseKeyRepository {
    
    
    private static final Logger log = LoggerFactory.getLogger(EnterpriseKeyRepositoryImpl.class);

    private final ActiveObjects ao;

    public EnterpriseKeyRepositoryImpl(ActiveObjects ao) {
        this.ao = ao;
    }

    @Override
    public SshKeyEntity createOrUpdateUserKey(StashUser user, String text, String label) {
        
        SshKeyEntity key = findSingleKey(user);
        if(null != key){
            key.setText(text);
            key.setLabel(label);
            key.setCreatedDate(new Date());
            key.save();
        }else{
            key = ao.create(SshKeyEntity.class, new DBParam("USERID", user.getId()), new DBParam("TEXT", text),new DBParam("LABEL",label),new DBParam("CREATED", new Date()));
        }
        return key;
        
    }

    private SshKeyEntity findSingleKey(StashUser user) {
        SshKeyEntity[] keys = ao.find(SshKeyEntity.class, Query.select().where("USERID = ?", user.getId()));
        if( null != keys && keys.length == 1 ){
            SshKeyEntity key = keys[0];
            return key;
        }else{
            return null;
        }
    }

    @Override
    public boolean isValidKeyForUser(StashUser user, String text) {
        SshKeyEntity key = findSingleKey(user);
        if(null != key){
            return key.getText().equals(text);
        }
        return false;
       
    }

    @Override
    public List<SshKeyEntity> listOfExpiredKeyIds(Date oldestValidDate) {
        final List<Integer> expiredIds = new ArrayList<Integer>();

        //using ao.stream returns valid OID but all other fields absent.
//        ao.stream(SshKeyEntity.class, Query.select().where("CREATED < ?",cal.getTime()),new EntityStreamCallback<SshKeyEntity, Integer>(){
//
//            @Override
//            public void onRowRead(SshKeyEntity t) {
//                log.debug("Reading row: " + t.getID() + "," + t.getLabel() + "," + t.getKeyId());
//                expiredIds.add(t.getKeyId());
//            }
//        
//        });
        SshKeyEntity[] results = ao.find(SshKeyEntity.class, Query.select().where("CREATED < ?",oldestValidDate));
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
    
    
}