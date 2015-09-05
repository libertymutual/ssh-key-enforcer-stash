package com.edwardawebb.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.stash.user.StashUser;
import com.atlassian.stash.user.UserService;
import com.google.common.collect.ImmutableMap;

public class UserKeyManagementServlet extends HttpServlet{
    private static final Logger log = LoggerFactory.getLogger(UserKeyManagementServlet.class);
    private SoyTemplateRenderer soyTemplateRenderer;
    private UserService userService;

    public UserKeyManagementServlet(SoyTemplateRenderer soyTemplateRenderer, UserService userService) {
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.userService = userService;
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String pathInfo = req.getPathInfo();

        String userSlug = pathInfo.substring(1); // Strip leading slash
        StashUser user = userService.getUserBySlug(userSlug);

        if (user == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        render(resp, "plugin.account.sshTab", ImmutableMap.<String, Object>of("user", user));
    }

    
    private void render(HttpServletResponse resp, String templateName, Map<String, Object> data) throws IOException, ServletException {
        resp.setContentType("text/html;charset=UTF-8");
        try {
            soyTemplateRenderer.render(resp.getWriter(),
                    "com.edwardawebb.stash-ssh-key-enforcer:account-ssh-soy",
                    templateName,
                    data);
        } catch (SoyException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            }
            throw new ServletException(e);
        }
    }
}