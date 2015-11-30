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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.lmig.forge.stash.ssh.ao.SshKeyEntity;
import com.lmig.forge.stash.ssh.ao.SshKeyEntity.KeyType;
@XmlRootElement(name = "keyDetails")
@XmlAccessorType(XmlAccessType.FIELD)
public class KeyDetailsResourceModel {


    private String message;
    private String publicKey;
    private String label;
    private int userId;
    private int stashKeyId;
    private KeyType keyType;

    public KeyDetailsResourceModel() {
    }

    public KeyDetailsResourceModel(String message) {
        this.message = message;
    }
    
    public static KeyDetailsResourceModel from(SshKeyEntity keyEntity){
        KeyDetailsResourceModel result = new KeyDetailsResourceModel();
        result.keyType = keyEntity.getKeyType();
        result.publicKey = keyEntity.getText();
        result.label = keyEntity.getLabel();
        result.userId = keyEntity.getUserId();
        result.stashKeyId = keyEntity.getKeyId();
        return result;
    }
}