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

package com.lmig.forge.stash.ssh.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.stash.i18n.I18nService;
import com.atlassian.stash.mail.MailMessage;
import com.atlassian.stash.mail.MailService;
import com.atlassian.stash.mail.SoyMailBuilder;
import com.atlassian.stash.server.ApplicationPropertiesService;
import com.atlassian.stash.user.StashUser;
import com.atlassian.stash.user.UserService;
import com.lmig.forge.stash.ssh.UserNotFoundException;

public class NotificationService {
   
    private final MailService mailService;
    private final UserService userService;
    private final ApplicationPropertiesService applicationPropertiesService;
    private final I18nService i18nService;
    private final String KEY_GEN_CONTEXT = "/plugins/servlet/account/enterprisekey";
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    

    public NotificationService(MailService mailService, UserService userService,
            ApplicationPropertiesService applicationPropertiesService,I18nService i18nService) {
        this.mailService = mailService;
        this.userService = userService;
        this.applicationPropertiesService = applicationPropertiesService;
        this.i18nService = i18nService;
    }

    public void notifyUserOfExpiredKey(int userId) {
        StashUser user = userService.getUserById(userId);
        String pageUrl = applicationPropertiesService.getBaseUrl() + KEY_GEN_CONTEXT;
        String subject = i18nService.getMessage("stash.ssh.reset.email.subject");
        String body = i18nService.getMessage("stash.ssh.reset.email.body",pageUrl);
        if (null != user) {
            if(log.isDebugEnabled()){
                log.debug("Sending email to: " + user.getEmailAddress());
            }
            MailMessage message = new MailMessage.Builder()
                    .to(user.getEmailAddress())
                    .subject(subject)
                    .text(body).build();
            mailService.submit(message);
        } else {
            throw new UserNotFoundException();
        }

    }
    

}
