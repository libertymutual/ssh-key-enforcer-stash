package com.edwardawebb.stash.ssh;

import java.util.Date;

import net.java.ao.DBParam;
import net.java.ao.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.stash.user.StashUser;

public class EnterpriseKeyRepositoryImpl implements EnterpriseKeyRepository {
    private static final Logger log = LoggerFactory.getLogger(EnterpriseKeyRepositoryImpl.class);

    private final ActiveObjects ao;

    public EnterpriseKeyRepositoryImpl(ActiveObjects ao) {
        this.ao = ao;
    }

    @Override
    public void createOrUpdateUserKey(StashUser user, String text, String label) {
        
        SshKeyEntity key = findSingleKey(user);
        if(null != key){
            key.setText(text);
            key.setLabel(label);
            key.setCreatedDate(new Date());
            key.save();
        }else{
            ao.create(SshKeyEntity.class, new DBParam("USERID", user.getId()), new DBParam("TEXT", text),new DBParam("LABEL",label),new DBParam("CREATED", new Date()));
           
        }
        
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
    
    
}