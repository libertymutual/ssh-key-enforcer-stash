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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.stash.user.EscalatedSecurityContext;
import com.atlassian.stash.user.Permission;
import com.atlassian.stash.user.SecurityService;
import com.lmig.forge.stash.ssh.config.PluginSettingsService;

public class KeyRotationJobRunner implements JobRunner{

    final public static String ADMIN_ACCOUNT_NAME = "admin";
    final public static String PERMISSION_REASON = "Required by SSH Key Enforcer to purge expired keys";
    

    private final SecurityService securityService;
    private final KeyRotationOperation keyRotationOperation;
    private final PluginSettingsService pluginSettingService;
    private static final Logger log = LoggerFactory.getLogger(KeyRotationJobRunner.class);
    
    public KeyRotationJobRunner(KeyRotationOperation keyRotationOperation,SecurityService securityService, PluginSettingsService pluginSettingService) {
       
        this.securityService = securityService;
        this.keyRotationOperation = keyRotationOperation;
        this.pluginSettingService =pluginSettingService;
    }
    
    @Override
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        if( pluginSettingService.getDaysAllowedForUserKeys() > 0 ){
            log.debug("Key Expire Job Starting");
            EscalatedSecurityContext elevatedContext = securityService.withPermission(Permission.SYS_ADMIN,PERMISSION_REASON);
            try {
                elevatedContext.call(keyRotationOperation);
            } catch (Throwable e) {
                log.error("Key expiry failed!",e);
                return JobRunnerResponse.failed(e);
            }
            log.debug("Key Expire Job Complete");
        } else {
            log.debug("Key Expire Job Skipping since  '" + PluginSettingsService.SETTINGS_KEY_DAYS_KEEP_USERS + "' is 0/not set.");
        }
        return JobRunnerResponse.success();
    }

}
