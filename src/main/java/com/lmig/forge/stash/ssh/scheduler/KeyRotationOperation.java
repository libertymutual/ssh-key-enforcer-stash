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
package com.lmig.forge.stash.ssh.scheduler;

import com.atlassian.stash.util.UncheckedOperation;
import com.lmig.forge.stash.ssh.keys.EnterpriseSshKeyService;

public class KeyRotationOperation implements UncheckedOperation {
    private final EnterpriseSshKeyService enterpriseKeyService;
    
    public KeyRotationOperation(EnterpriseSshKeyService enterpriseKeyService) {
        this.enterpriseKeyService = enterpriseKeyService;
    }

    @Override
    public Void perform()  {
        enterpriseKeyService.replaceExpiredKeysAndNotifyUsers();
        return null;
    }

}
