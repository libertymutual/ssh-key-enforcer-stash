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

package com.lmig.forge.stash.ssh.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lmig.forge.stash.ssh.config.PluginSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.google.common.collect.ImmutableMap;

public class UserKeyManagementServlet extends HttpServlet{
    private static final Logger log = LoggerFactory.getLogger(UserKeyManagementServlet.class);
    private final SoyTemplateRenderer soyTemplateRenderer;
    private final PluginSettingsService pluginSettingsService;

    public UserKeyManagementServlet(SoyTemplateRenderer soyTemplateRenderer,PluginSettingsService pluginSettingsService) {
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.pluginSettingsService = pluginSettingsService;
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String pathInfo = req.getPathInfo();

        
        render(resp, "plugin.account.sshTab", ImmutableMap.<String, Object>of("policyLink", pluginSettingsService.getInternalKeyPolicyLink("")));
    }

    
    private void render(HttpServletResponse resp, String templateName, Map<String, Object> data) throws IOException, ServletException {
        resp.setContentType("text/html;charset=UTF-8");
        try {
            soyTemplateRenderer.render(resp.getWriter(),
                    "com.lmig.forge.stash.ssh.stash-ssh-key-enforcer:account-ssh-soy",
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