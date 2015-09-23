package com.edwardawebb.stash.ssh.notifications;

import com.atlassian.stash.mail.MailMessage;
import com.atlassian.stash.mail.MailService;
import com.atlassian.stash.server.ApplicationPropertiesService;
import com.atlassian.stash.user.StashUser;
import com.atlassian.stash.user.UserService;
import com.edwardawebb.stash.ssh.UserNotFoundException;

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
