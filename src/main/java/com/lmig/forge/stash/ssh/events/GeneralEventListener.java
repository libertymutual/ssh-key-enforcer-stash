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

package com.lmig.forge.stash.ssh.events;

import com.atlassian.bitbucket.i18n.I18nService;
import com.atlassian.bitbucket.ssh.SshAccessKey;
import com.atlassian.bitbucket.ssh.SshKey;
import com.atlassian.bitbucket.ssh.event.SshKeyCreatedEvent;
import com.atlassian.bitbucket.ssh.event.SshKeyDeletedEvent;
import com.atlassian.event.api.EventListener;
import com.lmig.forge.stash.ssh.keys.EnterpriseSshKeyService;
import org.apache.log4j.Logger;

public class GeneralEventListener {
    private final Logger logger = Logger.getLogger(GeneralEventListener.class);
    
    final private EnterpriseSshKeyService enterpriseSshKeyService;
    final private I18nService i18nService;

    public GeneralEventListener(EnterpriseSshKeyService enterpriseSshKeyService,I18nService i18nService) {
        this.enterpriseSshKeyService = enterpriseSshKeyService;
        this.i18nService = i18nService;
    }

    @EventListener
    public void createListener(SshKeyCreatedEvent stashEvent) {
        SshKey key = stashEvent.getKey();
        enterpriseSshKeyService.removeKeyIfNotLegal(key, stashEvent.getUser());
    }

    @EventListener
    public void deleteListener(SshKeyDeletedEvent stashEvent) {
        SshKey key = stashEvent.getKey();
        enterpriseSshKeyService.forgetDeletedKey(key);
    }

}
