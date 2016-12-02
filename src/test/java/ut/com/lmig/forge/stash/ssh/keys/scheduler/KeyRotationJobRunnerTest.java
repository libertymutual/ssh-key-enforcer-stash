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

package ut.com.lmig.forge.stash.ssh.keys.scheduler;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.atlassian.bitbucket.permission.Permission;
import org.junit.Before;
import org.junit.Test;

import ut.com.lmig.forge.stash.ssh.SshEnforcerTestHelper;

import com.atlassian.bitbucket.user.EscalatedSecurityContext;
import com.atlassian.bitbucket.user.SecurityService;
import com.lmig.forge.stash.ssh.config.PluginSettingsService;
import com.lmig.forge.stash.ssh.scheduler.KeyRotationJobRunner;
import com.lmig.forge.stash.ssh.scheduler.KeyRotationOperation;




public class KeyRotationJobRunnerTest {
   
    
    @Before
    public void setup(){
    }
    
    @Test
    public void securityElevatedOperationsIsCalledByJob() throws Throwable{
        SecurityService service = mock(SecurityService.class);
        KeyRotationOperation kro = mock(KeyRotationOperation.class);
        EscalatedSecurityContext esc = mock(EscalatedSecurityContext.class);        
        PluginSettingsService pluginsService =  SshEnforcerTestHelper.getPluginSettingsServiceMock();
        when(service.withPermission(any(Permission.class), anyString())).thenReturn(esc);
        
        KeyRotationJobRunner jobRunner = new KeyRotationJobRunner(kro, service,pluginsService);
        jobRunner.runJob(null);
        
       
        verify(esc).call(kro);
        
        
    }
    
    @Test
    public void userKeysNotTouchedWhenUserDaysIsZero() throws Throwable{
        SecurityService service = mock(SecurityService.class);
        KeyRotationOperation kro = mock(KeyRotationOperation.class);
        EscalatedSecurityContext esc = mock(EscalatedSecurityContext.class);
        PluginSettingsService pluginsService =  SshEnforcerTestHelper.getPluginSettingsServiceMock();
        when(pluginsService.getDaysAllowedForUserKeys()).thenReturn(0);
        when(service.withPermission(any(Permission.class), anyString())).thenReturn(esc);
        
        KeyRotationJobRunner jobRunner = new KeyRotationJobRunner(kro, service,pluginsService);
        jobRunner.runJob(null);
        
        
        verify(esc, times(0)).call(kro);
        
        
    }
    
    
    
}
