package ut.com.lmig.forge.stash.ssh.config;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
   
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    
    
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
    public void defaultConfigCanBeRetrieved() {
        AdminConfigResourceModel config = pluginSettingsService.getAdminConfigResourcesModel();

        assertThat(config.getAuthorizedGroup(),is(SshEnforcerTestHelper.PLUGIN_SERVICE_TEST_CONFIG_GROUP));
        assertThat(config.getDaysToKeepUserKeys(),is(AdminConfigResourceModel.DEFAULT_DAYS_USER));
        assertThat(config.getDaysToKeepBambooKeys(),is(AdminConfigResourceModel.DEFAULT_DAYS_BAMBOO));
        assertThat(config.getMillisBetweenRuns(),is(AdminConfigResourceModel.DEFAULT_MILLIS_BETWEEN_RUNS));        
    }
    @Test
    public void configCanBeUpdated() {
        String group ="FOO";
        String user="baz";
        int userDays = 7;
        int bambooDays = 30;
        long millis = 300000; //5 minutes
        String policyLink = "http://example.com/info";
        
        pluginSettingsService.updateAdminConfigResourcesModel(
                new TestBuilder()
                        .withAuthorizedGroup(group)
                        .withBambooUser(user)
                        .withDaysToKeepBambooKeys(bambooDays)
                        .withDaysToKeepUserKeys(userDays)
                        .withInternalKeyPolicyLink(policyLink)
                        .withMillisBetweenRuns(millis)
                        .build()
        );

        verify(pluginSettings).put(PluginSettingsService.SETTINGS_KEY_BAMBOO_USER, user);
        verify(pluginSettings).put(PluginSettingsService.SETTINGS_KEY_AUTHORIZED_GROUP, group);
        verify(pluginSettings).put(PluginSettingsService.SETTINGS_KEY_DAYS_KEEP_USERS, String.valueOf(userDays)); 
        verify(pluginSettings).put(PluginSettingsService.SETTINGS_KEY_DAYS_KEEP_BAMBOO, String.valueOf(bambooDays));
        verify(pluginSettings).put(PluginSettingsService.SETTINGS_KEY_MILLIS_INTERVAL, String.valueOf(millis));
        verify(pluginSettings).put(PluginSettingsService.SETTINGS_KEY_POLICY_LINK,policyLink);

    }
    @Test
    public void scheduledIntervalMustBeFiveMinutesOrMore() {
        String group ="FOO";
        String user="baz";
        int userDays = 7;
        int bambooDays = 30;
        long millis = 60000; // 1 minute
        String policyLink = "http://example.com/info";
        
        //bad attempt throws exception
        expectedException.expect(IllegalArgumentException.class);
        pluginSettingsService.updateAdminConfigResourcesModel(
                new TestBuilder()
                        .withAuthorizedGroup(group)
                        .withBambooUser(user)
                        .withDaysToKeepBambooKeys(bambooDays)
                        .withDaysToKeepUserKeys(userDays)
                        .withInternalKeyPolicyLink(policyLink)
                        .withMillisBetweenRuns(millis)
                        .build()
                );

    }
    @Test
    public void daysMustBePositive() {
        String group ="FOO";
        String user="baz";
        int userDays = 1;
        int bambooDays = -1;
        long millis = 60000; // 1 minute
        String policyLink = "http://example.com/info";
        //bad attempt throws exception
        expectedException.expect(IllegalArgumentException.class);
        pluginSettingsService.updateAdminConfigResourcesModel(
                new TestBuilder()
                        .withAuthorizedGroup(group)
                        .withBambooUser(user)
                        .withDaysToKeepBambooKeys(bambooDays)
                        .withDaysToKeepUserKeys(userDays)
                        .withInternalKeyPolicyLink(policyLink)
                        .withMillisBetweenRuns(millis)
                        .build()
        );
        userDays = -1; //swap the negatives
        bambooDays = 1;
       //bad attempt throws exception
        expectedException.expect(IllegalArgumentException.class);
        pluginSettingsService.updateAdminConfigResourcesModel(
                new TestBuilder()
                        .withAuthorizedGroup(group)
                        .withBambooUser(user)
                        .withDaysToKeepBambooKeys(bambooDays)
                        .withDaysToKeepUserKeys(userDays)
                        .withInternalKeyPolicyLink(policyLink)
                        .withMillisBetweenRuns(millis)
                        .build()
        );

    }
    @Test
    public void scheduledIntervalAllowsZeroToDisable() {
        String group ="FOO";
        String user="baz";
        int userDays = 7;
        int bambooDays = 30;
        long millis = 0; // disable
        String policyLink = "http://example.com/info";
        
        pluginSettingsService.updateAdminConfigResourcesModel(
                new TestBuilder()
                        .withAuthorizedGroup(group)
                        .withBambooUser(user)
                        .withDaysToKeepBambooKeys(bambooDays)
                        .withDaysToKeepUserKeys(userDays)
                        .withInternalKeyPolicyLink(policyLink)
                        .withMillisBetweenRuns(millis)
                        .build()
        );

        verify(pluginSettings).put(PluginSettingsService.SETTINGS_KEY_MILLIS_INTERVAL, String.valueOf(millis)); 
    }

    public static class TestBuilder{
        private String authorizedGroup;
        private String bambooUser;
        private int daysToKeepUserKeys = AdminConfigResourceModel.DEFAULT_DAYS_USER;
        private int daysToKeepBambooKeys = AdminConfigResourceModel.DEFAULT_DAYS_BAMBOO;
        private long millisBetweenRuns = AdminConfigResourceModel.DEFAULT_MILLIS_BETWEEN_RUNS;
        private String internalKeyPolicyLink ;

        public TestBuilder() {
        }

        public AdminConfigResourceModel build(){
            AdminConfigResourceModel config = new AdminConfigResourceModel();
            config.setInternalKeyPolicyLink(internalKeyPolicyLink);
            config.setMillisBetweenRuns(millisBetweenRuns);
            config.setDaysToKeepBambooKeys(daysToKeepBambooKeys);
            config.setDaysToKeepUserKeys( daysToKeepUserKeys);
            config.setBambooUser( bambooUser);
            config.setAuthorizedGroup(authorizedGroup);
            return config;
        }

        public TestBuilder withAuthorizedGroup(String authorizedGroup) {
            this.authorizedGroup = authorizedGroup;
            return this;
        }

        public TestBuilder withBambooUser(String bambooUser) {
            this.bambooUser = bambooUser;
            return this;
        }

        public TestBuilder withDaysToKeepUserKeys(int daysToKeepUserKeys) {
            this.daysToKeepUserKeys = daysToKeepUserKeys;
            return this;
        }

        public TestBuilder withDaysToKeepBambooKeys(int daysToKeepBambooKeys) {
            this.daysToKeepBambooKeys = daysToKeepBambooKeys;
            return this;
        }

        public TestBuilder withMillisBetweenRuns(long millisBetweenRuns) {
            this.millisBetweenRuns = millisBetweenRuns;
            return this;
        }

        public TestBuilder withInternalKeyPolicyLink(String internalKeyPolicyLink) {
            this.internalKeyPolicyLink = internalKeyPolicyLink;
            return this;
        }
    }

}
