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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.atlassian.event.api.EventListener;
import com.atlassian.stash.event.StashEvent;
import com.atlassian.stash.i18n.I18nService;
import com.atlassian.stash.ssh.api.SshKey;
import com.lmig.forge.stash.ssh.keys.EnterpriseSshKeyService;

public class GeneralEventListener {
    private final Logger logger = Logger.getLogger(GeneralEventListener.class);
    private final static String SSH_KEY_CREATED_EVENT_CLASS = "com.atlassian.stash.ssh.SshKeyCreatedEvent";
    private final static String SSH_KEY_DELETED_EVENT_CLASS = "com.atlassian.stash.ssh.SshKeyDeletedEvent";
    
    
    final private EnterpriseSshKeyService enterpriseSshKeyService;
    final private I18nService i18nService;

    public GeneralEventListener(EnterpriseSshKeyService enterpriseSshKeyService,I18nService i18nService) {
        this.enterpriseSshKeyService = enterpriseSshKeyService;
        this.i18nService = i18nService;
    }

    @EventListener
    public void mylistener(StashEvent stashEvent) {       
        if (SSH_KEY_CREATED_EVENT_CLASS.equals(stashEvent.getClass().getCanonicalName())) {
            try {
                Method method = stashEvent.getClass().getMethod("getKey");
                SshKey key = (SshKey) method.invoke(stashEvent);                
                enterpriseSshKeyService.removeKeyIfNotLegal(key, stashEvent.getUser());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }else if (SSH_KEY_DELETED_EVENT_CLASS.equals(stashEvent.getClass().getCanonicalName())) {
            try {
                Method method = stashEvent.getClass().getMethod("getKey");
                SshKey key = (SshKey) method.invoke(stashEvent);                
                enterpriseSshKeyService.forgetDeletedKey(key);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    

}
