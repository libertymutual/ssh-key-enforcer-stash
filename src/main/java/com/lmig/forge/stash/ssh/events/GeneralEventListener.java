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
import com.atlassian.stash.event.AbstractAuthenticationEvent;
import com.atlassian.stash.event.StashEvent;
import com.atlassian.stash.event.permission.ProjectPermissionGrantRequestedEvent;
import com.atlassian.stash.event.permission.RepositoryPermissionGrantRequestedEvent;
import com.atlassian.stash.i18n.I18nService;
import com.atlassian.stash.ssh.api.SshKey;
import com.atlassian.stash.ssh.api.SshKeyService;
import com.atlassian.stash.user.UserType;
import com.lmig.forge.stash.ssh.keys.EnterpriseSshKeyService;

public class GeneralEventListener {
    private final Logger logger = Logger.getLogger(GeneralEventListener.class);
    private final static String SSH_KEY_CREATED_EVENT_CLASS = "com.atlassian.stash.ssh.SshKeyCreatedEvent";

    final private EnterpriseSshKeyService enterpriseSshKeyService;
    private I18nService i18nService;
    final private SshKeyService sshKeyService;

    public GeneralEventListener(SshKeyService sshKeyService, EnterpriseSshKeyService enterpriseSshKeyService,I18nService i18nService) {
        this.enterpriseSshKeyService = enterpriseSshKeyService;
        this.i18nService = i18nService;
        this.sshKeyService = sshKeyService;
    }

    @EventListener
    public void mylistener(ProjectPermissionGrantRequestedEvent permissionRequestEvent) {
        if (UserType.SERVICE == permissionRequestEvent.getAffectedUser().getType()) {
            logger.warn("Rejected Permission Access attempt against project: " + permissionRequestEvent.getProject());
            permissionRequestEvent
                    .cancel(i18nService.createKeyedMessage("customplugin.pullrequest.cancel"));
        }
    }

    @EventListener
    public void mylistener(RepositoryPermissionGrantRequestedEvent permissionRequestEvent) {
        if (UserType.SERVICE == permissionRequestEvent.getAffectedUser().getType()) {
            logger.warn("Rejected Permission Access attempt against repository: " + permissionRequestEvent.getRepository());
            permissionRequestEvent
                    .cancel(i18nService.createKeyedMessage("customplugin.pullrequest.cancel"));
        }
    }

    
    

    @EventListener
    public void mylistener(StashEvent stashEvent) {

        // IF YOU READ THIS,  I'm sorry.
        // I know reflection is BS and brittle, but it was the only way to get
        // at the public "SshKey" using a non-prublic event.
        // Reflection works using our own class that mimics Atlassian object.
        // BUT this cannot be casted to the object (ours or atlassians)
        // https://developer.atlassian.com/static/javadoc/stash/3.11.2/ssh/reference/com/atlassian/stash/ssh/SshKeyCreatedEvent.html
        // https://maven.atlassian.com/#nexus-search;classname~com.atlassian.stash.ssh.SshKeyCreatedEvent
        // <pre>Note: in Stash 2.9, SshKeyEvent and all of it's subclasses will
        // never be published. The classes are not OSGI exported and can
        // therefore not be published. Instead, the events are translated into
        // AuditEvents, which are published.</pre>
        // -https://developer.atlassian.com/static/javadoc/stash/3.7.1/ssh/reference/com/atlassian/stash/internal/key/ssh/SshKeyAccessRevokedEvent.html
        // Dependency included as provided compiles, but fails run time with no
        // class def
        // Dependency included as compile and it compiles, but fails at run time
        // with some other class failing..

        if (SSH_KEY_CREATED_EVENT_CLASS.equals(stashEvent.getClass().getCanonicalName())) {
            if (logger.isDebugEnabled()) {
                logger.debug("SSH key was created by" + stashEvent.getUser().getDisplayName() + " from "
                        + stashEvent.getSource());
            }
            try {
                Method method = stashEvent.getClass().getMethod("getKey");
                SshKey key = (SshKey) method.invoke(stashEvent);

                if (logger.isDebugEnabled()) {
                    logger.debug("The SSH Key is: " + key.getLabel() + "|with value: " + key.getText() + "|with user: "
                            + key.getUser());
                }
                enterpriseSshKeyService.removeKeyIfNotLegal(key, stashEvent.getUser());
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } 

    }

}
