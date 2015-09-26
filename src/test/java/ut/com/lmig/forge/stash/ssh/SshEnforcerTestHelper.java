package ut.com.lmig.forge.stash.ssh;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.lmig.forge.stash.ssh.config.PluginSettingsService;
import com.lmig.forge.stash.ssh.rest.AdminConfigResourceModel;

public class SshEnforcerTestHelper {
    
    public static final String PLUGIN_SERVICE_TEST_CONFIG_GROUP ="ssh=gods";
    
    @Before
    public static PluginSettings defaultValuesSettingMock() {
        PluginSettings pluginSettings = mock(PluginSettings.class);
        when(pluginSettings.get(PluginSettingsService.SETTINGS_KEY_AUTHORIZED_GROUP)).thenReturn(PLUGIN_SERVICE_TEST_CONFIG_GROUP);
        when(pluginSettings.get(PluginSettingsService.SETTINGS_KEY_DAYS_KEEP_USERS)).thenReturn(String.valueOf(AdminConfigResourceModel.DEFAULT_DAYS_USER));
        when(pluginSettings.get(PluginSettingsService.SETTINGS_KEY_DAYS_KEEP_BAMBOO)).thenReturn(String.valueOf(AdminConfigResourceModel.DEFAULT_DAYS_BAMBOO));
        when(pluginSettings.get(PluginSettingsService.SETTINGS_KEY_MILLIS_INTERVAL)).thenReturn(String.valueOf(AdminConfigResourceModel.DEFAULT_MILLIS_BETWEEN_RUNS));
        return pluginSettings;
    }
    
}
