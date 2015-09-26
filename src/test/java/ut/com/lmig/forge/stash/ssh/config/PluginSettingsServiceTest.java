package ut.com.lmig.forge.stash.ssh.config;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ut.com.lmig.forge.stash.ssh.SshEnforcerTestHelper;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.lmig.forge.stash.ssh.config.PluginSettingsService;
import com.lmig.forge.stash.ssh.rest.AdminConfigResourceModel;

public class PluginSettingsServiceTest {

    
    private PluginSettingsService pluginSettingsService;
    private TransactionTemplate transactionTemplate; 
    private PluginSettingsFactory pluginSettingsFactory ;
    private PluginSettings pluginSettings;
   
    

    
    
    @Before
    public void setup() {
         pluginSettingsFactory = mock(PluginSettingsFactory.class);
        pluginSettings = SshEnforcerTestHelper.defaultValuesSettingMock();
        when(pluginSettingsFactory.createGlobalSettings()).thenReturn(pluginSettings);
        pluginSettingsService = new PluginSettingsService(pluginSettingsFactory);
     }

     
    @After
    public void tearDown() {

    }

    @Test
    public void configCanBeRetrieved() {
        AdminConfigResourceModel config = pluginSettingsService.getAdminConfigResourcesModel();

        assertThat(config.getAuthorizedGroup(),is(SshEnforcerTestHelper.PLUGIN_SERVICE_TEST_CONFIG_GROUP));
        assertThat(config.getDaysToKeepUserKeys(),is(AdminConfigResourceModel.DEFAULT_DAYS_USER));
        assertThat(config.getDaysToKeepBambooKeys(),is(AdminConfigResourceModel.DEFAULT_DAYS_BAMBOO));
        assertThat(config.getMillisBetweenRuns(),is(AdminConfigResourceModel.DEFAULT_MILLIS_BETWEEN_RUNS));        
    }
    @Test
    public void configCanBeUpdated() {
        String group ="FOO";
        int userDays = 7;
        int bambooDays = 30;
        long millis = 60000;
        
        pluginSettingsService.updateAdminConfigResourcesModel(new AdminConfigResourceModel(null, group, userDays, bambooDays, millis));

        verify(pluginSettings).put(PluginSettingsService.SETTINGS_KEY_AUTHORIZED_GROUP, group); 
        verify(pluginSettings).put(PluginSettingsService.SETTINGS_KEY_DAYS_KEEP_USERS, String.valueOf(userDays)); 
        verify(pluginSettings).put(PluginSettingsService.SETTINGS_KEY_DAYS_KEEP_BAMBOO, String.valueOf(bambooDays)); 
        verify(pluginSettings).put(PluginSettingsService.SETTINGS_KEY_MILLIS_INTERVAL, String.valueOf(millis)); 
    }
}
