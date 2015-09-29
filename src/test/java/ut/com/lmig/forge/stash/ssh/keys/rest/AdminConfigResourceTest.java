package ut.com.lmig.forge.stash.ssh.keys.rest;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.sal.api.user.UserManager;
import com.atlassian.stash.user.StashAuthenticationContext;
import com.atlassian.stash.user.StashUser;
import com.lmig.forge.stash.ssh.config.PluginSettingsService;
import com.lmig.forge.stash.ssh.rest.AdminConfigResource;
import com.lmig.forge.stash.ssh.rest.AdminConfigResourceModel;

public class AdminConfigResourceTest { 
    private static final String TEST_CONFIG_GROUP ="ssh-gods";
    private static final String TEST_CONFIG_USER ="bamboolinker";
    private final AdminConfigResourceModel sampleConfig = new AdminConfigResourceModel(null,TEST_CONFIG_GROUP, AdminConfigResourceModel.DEFAULT_DAYS_USER, AdminConfigResourceModel.DEFAULT_DAYS_BAMBOO, AdminConfigResourceModel.DEFAULT_MILLIS_BETWEEN_RUNS,TEST_CONFIG_USER);
    private UserManager userManager = mock(UserManager.class);
    private StashAuthenticationContext stashAuthenticationContext = mock(StashAuthenticationContext.class);
    private PluginSettingsService pluginSettingsService = mock(PluginSettingsService.class);


    @Before
    public void setup() {
        when(pluginSettingsService.getAdminConfigResourcesModel()).thenReturn(sampleConfig);
        
    }

    @After
    public void tearDown() {

    }

    @Test
    public void configCanBeRerievedByAdmin() {
        when(userManager.isSystemAdmin(anyString())).thenReturn(true);
        when(stashAuthenticationContext.getCurrentUser()).thenReturn(mock(StashUser.class));
        AdminConfigResource resource = new AdminConfigResource(userManager, stashAuthenticationContext, pluginSettingsService);

        Response response = resource.getConfig();
        final AdminConfigResourceModel config = (AdminConfigResourceModel) response.getEntity();

        assertThat(config,is(sampleConfig));
    }
    @Test
    public void configCanNotBeRerievedByNonAdmin() {
        when(userManager.isSystemAdmin(anyString())).thenReturn(false);
        when(stashAuthenticationContext.getCurrentUser()).thenReturn(mock(StashUser.class));
        AdminConfigResource resource = new AdminConfigResource(userManager, stashAuthenticationContext, pluginSettingsService);

        Response response = resource.getConfig();
        final AdminConfigResourceModel config = (AdminConfigResourceModel) response.getEntity();

        assertThat(response.getStatus(),is(403));
        assertThat(config,nullValue());
    }
}
