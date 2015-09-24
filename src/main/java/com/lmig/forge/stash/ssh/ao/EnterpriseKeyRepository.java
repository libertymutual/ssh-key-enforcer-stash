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

import com.atlassian.stash.ssh.api.SshKey;
import com.atlassian.stash.user.StashUser;

public interface EnterpriseKeyRepository {
    
    SshKeyEntity createOrUpdateUserKey(StashUser user, String text, String label);
    
    boolean isValidKeyForUser(StashUser user, String text);

    List<SshKeyEntity> listOfExpiredKeyIds(Date oldestValidDate);

    void updateRecordWithKeyId(SshKeyEntity newRecord, SshKey newKey);

    void removeRecord(SshKeyEntity key);

}

