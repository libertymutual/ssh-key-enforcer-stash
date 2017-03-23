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

import java.sql.Types;
import java.util.Date;

import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Mutator;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

import javax.persistence.Access;

@Table("ENTKEY")
public interface SshKeyEntity extends Entity{
    
    @Accessor("KEYID")
    Integer getKeyId();
    @NotNull
    @Accessor("TEXT")
    @StringLength(value=StringLength.UNLIMITED)
    String getText();
    @NotNull
    @Accessor("LABEL")
    String getLabel();
    @NotNull
    @Accessor("USERID")
    Integer getUserId();
    @NotNull
    @Accessor("CREATED")
    Date getCreatedDate();
    @NotNull
    @Accessor("TYPE")
    KeyType getKeyType();
    

    @Mutator("KEYID")
    void setKeyId(int keyId);
    @Mutator("TEXT")
    @StringLength(value=StringLength.UNLIMITED)
    void setText(String text);
    @Mutator("LABEL")
    void setLabel(String label);
    @Mutator("USERID")
    void setUserId(Integer id);
    @Mutator("CREATED")
    void setCreatedDate(Date created);
    @Mutator("TYPE")
    void setKeyType(KeyType type);
    
    public static enum KeyType{
        USER("USER","Generated by plugin on behalf of user."),
        BAMBOO("BAMBOO","Created by configured 'bambooUser' via native APIs"),
        BYPASS("BYPASS","Created by user in configured 'authorizedGroup' via native APIs");
        
        private final String name;
        private final String description;
        KeyType(String name, String description){
            this.name = name;
            this.description = description;
        }
        public String getName() {
            return name;
        }
        public String getDescription() {
            return description;
        }
        
        
    }
}
