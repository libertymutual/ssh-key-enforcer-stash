package ut.com.lmig.forge.stash.ssh.keys.rest;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import com.atlassian.sal.api.user.UserKey;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.sal.api.user.UserManager;
import com.atlassian.bitbucket.auth.AuthenticationContext;
import com.lmig.forge.stash.ssh.config.PluginSettingsService;
import com.lmig.forge.stash.ssh.rest.AdminConfigResource;
import com.lmig.forge.stash.ssh.rest.AdminConfigResourceModel;
import ut.com.lmig.forge.stash.ssh.config.PluginSettingsServiceTest;

public class AdminConfigResourceTest { 
    private static final String TEST_CONFIG_GROUP ="ssh-gods";
    private static final String TEST_CONFIG_USER ="bamboolinker";
    private static final UserKey ADMIN_USER_KEY = new UserKey("admin") ;
    private static final UserKey NONADMIN_USER_KEY = new UserKey("user") ;
    private final AdminConfigResourceModel sampleConfig = new PluginSettingsServiceTest.TestBuilder().withAuthorizedGroup(TEST_CONFIG_GROUP).withBambooUser(TEST_CONFIG_USER).build();
    private UserManager userManager = mock(UserManager.class);
    private AuthenticationContext stashAuthenticationContext = mock(AuthenticationContext.class);
    private PluginSettingsService pluginSettingsService = mock(PluginSettingsService.class);
    private AdminConfigResource configResourceEndpoint;

    @Before
    public void setup() {
        configResourceEndpoint = new AdminConfigResource(userManager, stashAuthenticationContext, pluginSettingsService);
        when(pluginSettingsService.getAdminConfigResourcesModel()).thenReturn(sampleConfig);
        when(userManager.isSystemAdmin(ADMIN_USER_KEY)).thenReturn(true);
        when(userManager.isSystemAdmin(NONADMIN_USER_KEY)).thenReturn(false);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void configCanBeRerievedByAdmin() {
        givenAdmin();
        //when
        Response response = configResourceEndpoint.getConfig();
        final AdminConfigResourceModel config = (AdminConfigResourceModel) response.getEntity();

        assertThat(response.getStatus(),is(200));
        assertThat(config,is(sampleConfig));
    }


    @Test
    public void configCanBeUpdatedByAdmin() {
        givenAdmin();
        //when
        Response response = configResourceEndpoint.updateConfig(sampleConfig);
        AdminConfigResourceModel config = (AdminConfigResourceModel) response.getEntity();
        //then
        assertThat(response.getStatus(),is(200));
    }
    @Test
    public void configCanNotBeRerievedByNonAdmin() {
        givenNonAdmin();
        //when
        Response response = configResourceEndpoint.getConfig();
        final AdminConfigResourceModel config = (AdminConfigResourceModel) response.getEntity();

        assertThat(response.getStatus(),is(403));
        assertThat(config,nullValue());
    }
    @Test
    public void configCanNotBeUpdatedByNonAdmin() {
        givenNonAdmin();
        //when
        Response response = configResourceEndpoint.updateConfig(sampleConfig);
        AdminConfigResourceModel config = (AdminConfigResourceModel) response.getEntity();
        //then
        assertThat(response.getStatus(),is(403));
        assertThat(config,nullValue());
    }

    private void givenAdmin(){
        when(userManager.getRemoteUserKey()).thenReturn(ADMIN_USER_KEY);
    }
    private void givenNonAdmin(){
        when(userManager.getRemoteUserKey()).thenReturn(NONADMIN_USER_KEY);
    }
}




