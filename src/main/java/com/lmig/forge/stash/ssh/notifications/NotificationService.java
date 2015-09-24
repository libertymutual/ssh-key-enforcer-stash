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

import com.atlassian.stash.mail.MailMessage;
import com.atlassian.stash.mail.MailService;
import com.atlassian.stash.server.ApplicationPropertiesService;
import com.atlassian.stash.user.StashUser;
import com.atlassian.stash.user.UserService;
import com.lmig.forge.stash.ssh.UserNotFoundException;

public class NotificationService {
    private final MailService mailService;
    private final UserService userService;
    private final ApplicationPropertiesService applicationPropertiesService;
    private final String KEY_GEN_CONTEXT = "/plugins/servlet/account/enterprisekey";

    public NotificationService(MailService mailService, UserService userService,
            ApplicationPropertiesService applicationPropertiesService) {
        this.mailService = mailService;
        this.userService = userService;
        this.applicationPropertiesService = applicationPropertiesService;
    }

    public void notifyUserOfExpiredKey(int userId) {
        StashUser user = userService.getUserById(userId);
        String pageUrl = applicationPropertiesService.getBaseUrl() + KEY_GEN_CONTEXT;
        if (null != user) {
            MailMessage message = new MailMessage.Builder()
                    .to(user.getEmailAddress())
                    .subject("Action Required: Reset Enterprise SSH Keys")
                    .text("Your Key has been destroyed by the firey wrath of SSH Key Enforcer. Please generate a new key by visiting <a href=\""
                            + pageUrl + "\">MyAccount SSH Keys</a>. If the link above does not work, please paste " + pageUrl + " directly into your browser.").build();
            mailService.submit(message);
        } else {
            throw new UserNotFoundException();
        }

    }

}
