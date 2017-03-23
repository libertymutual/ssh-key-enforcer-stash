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

package com.lmig.forge.stash.ssh.rest;

import com.atlassian.bitbucket.user.ApplicationUser;
import com.lmig.forge.stash.ssh.ao.SshKeyEntity;
import com.lmig.forge.stash.ssh.ao.SshKeyEntity.KeyType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name = "keyDetails")
@XmlAccessorType(XmlAccessType.FIELD)
public class KeyDetailsResourceModel {


    private String message;
    private String publicKey;
    private String label;
    private UserDetails user;
    private int stashKeyId;
    private KeyType keyType;
    private Date created;

    public KeyDetailsResourceModel() {
    }

    public String getMessage() {
        return message;
    }

    public KeyDetailsResourceModel setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public KeyDetailsResourceModel setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public KeyDetailsResourceModel setLabel(String label) {
        this.label = label;
        return this;
    }

    public UserDetails getUser() {
        return user;
    }

    public KeyDetailsResourceModel setUser(ApplicationUser user) {
        this.user = new UserDetails(user);
        return this;
    }

    public int getStashKeyId() {
        return stashKeyId;
    }

    public KeyDetailsResourceModel setStashKeyId(int stashKeyId) {
        this.stashKeyId = stashKeyId;
        return this;
    }

    public KeyType getKeyType() {
        return keyType;
    }

    public KeyDetailsResourceModel setKeyType(KeyType keyType) {
        this.keyType = keyType;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public KeyDetailsResourceModel setCreated(Date created) {
        this.created = created;
        return this;
    }

    public KeyDetailsResourceModel(String message) {
        this.message = message;
    }
    
    public static KeyDetailsResourceModel from(SshKeyEntity keyEntity,ApplicationUser user){
        KeyDetailsResourceModel result = new KeyDetailsResourceModel();
        result.keyType = keyEntity.getKeyType();
        result.publicKey = keyEntity.getText();
        result.label = keyEntity.getLabel();
        result.user = new UserDetails(user);
        result.stashKeyId = keyEntity.getKeyId();
        result.created = keyEntity.getCreatedDate();
        return result;
    }
}