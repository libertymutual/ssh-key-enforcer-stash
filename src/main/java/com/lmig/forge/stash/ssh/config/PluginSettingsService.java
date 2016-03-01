package com.lmig.forge.stash.ssh.config;

import javax.transaction.Transactional;

import org.apache.commons.lang.Validate;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.lmig.forge.stash.ssh.rest.AdminConfigResourceModel;

public class PluginSettingsService {
    public static final String SETTINGS_KEY_AUTHORIZED_GROUP = AdminConfigResourceModel.class.getName() + ".authorizedGroup";
    public static final String SETTINGS_KEY_DAYS_KEEP_USERS = AdminConfigResourceModel.class.getName() + ".daysToKeepUserKeys";
    public static final String SETTINGS_KEY_DAYS_KEEP_BAMBOO = AdminConfigResourceModel.class.getName() + ".daysToKeepBambooKeys";
    public static final String SETTINGS_KEY_MILLIS_INTERVAL = AdminConfigResourceModel.class.getName() + ".millisBetweenRuns";
    public static final String SETTINGS_KEY_BAMBOO_USER = AdminConfigResourceModel.class.getName() + ".bambooUser";
    public static final String SETTINGS_KEY_POLICY_LINK = AdminConfigResourceModel.class.getName() + ".internalKeyPolicyLink";
    
    private final PluginSettingsFactory pluginSettingsFactory;
    private AdminConfigResourceModel cachedModel;
    
    public PluginSettingsService(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

   @Transactional
    public AdminConfigResourceModel getAdminConfigResourcesModel(){
       if(null != cachedModel){
           return cachedModel;
       }
        PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
        AdminConfigResourceModel config = new AdminConfigResourceModel();

        String authorizedGroup = (String) settings.get(SETTINGS_KEY_AUTHORIZED_GROUP);
        if (authorizedGroup != null)
        {
          config.setAuthorizedGroup(authorizedGroup);
        } 
        String bambooUser = (String) settings.get(SETTINGS_KEY_BAMBOO_USER);
        if (bambooUser != null)
        {
          config.setBambooUser(bambooUser);
        } 
        
        String daysToKeepUserKeys = (String) settings.get(SETTINGS_KEY_DAYS_KEEP_USERS);
        if (daysToKeepUserKeys != null)
        {
          config.setDaysToKeepUserKeys(Integer.parseInt(daysToKeepUserKeys));
        }         
        String daysToKeepBambooKeys = (String) settings.get(SETTINGS_KEY_DAYS_KEEP_BAMBOO);
        if (daysToKeepBambooKeys != null)
        {
          config.setDaysToKeepBambooKeys(Integer.parseInt(daysToKeepBambooKeys));
        }         
        String millisBetweenRuns = (String) settings.get(SETTINGS_KEY_MILLIS_INTERVAL);
        if (millisBetweenRuns != null)
        {
          config.setMillisBetweenRuns(Long.parseLong(millisBetweenRuns));
        }
        String internalKeyPolicyLink = (String) settings.get(SETTINGS_KEY_POLICY_LINK);
        if (internalKeyPolicyLink != null)
        {
          config.setInternalKeyPolicyLink(internalKeyPolicyLink);
        } 
        return config;
    }

   @Transactional
    public AdminConfigResourceModel updateAdminConfigResourcesModel(final AdminConfigResourceModel updatedConfig) {

       Validate.isTrue(updatedConfig.getDaysToKeepBambooKeys() >= 0,"must keep keys at least 1 day (or 0 to disable)");
       Validate.isTrue(updatedConfig.getDaysToKeepUserKeys() >= 0,"must keep keys at least 1 day (or 0 to disable)");
       Validate.isTrue(updatedConfig.getMillisBetweenRuns() >= 300000 || updatedConfig.getMillisBetweenRuns() ==0,"Must space at least 5 minutes apart (or 0 to disable all jobs)");
        PluginSettings settings = pluginSettingsFactory.createGlobalSettings();

        settings.put(SETTINGS_KEY_AUTHORIZED_GROUP, updatedConfig.getAuthorizedGroup());
        settings.put(SETTINGS_KEY_BAMBOO_USER, updatedConfig.getBambooUser());
        settings.put(SETTINGS_KEY_POLICY_LINK, updatedConfig.getInternalKeyPolicyLink());
         settings.put(SETTINGS_KEY_DAYS_KEEP_USERS, String.valueOf(updatedConfig.getDaysToKeepUserKeys()));
        settings.put(SETTINGS_KEY_DAYS_KEEP_BAMBOO,  String.valueOf(updatedConfig.getDaysToKeepBambooKeys()));
        settings.put(SETTINGS_KEY_MILLIS_INTERVAL,  String.valueOf(updatedConfig.getMillisBetweenRuns()));
        
        //refresh cache, including logic on default values in case some were not provided.
        cachedModel = updatedConfig;
        return cachedModel;

    }

    public String getAuthorizedGroup() {
        return getAdminConfigResourcesModel().getAuthorizedGroup();
    }

    public int getDaysAllowedForUserKeys() {
        return getAdminConfigResourcesModel().getDaysToKeepUserKeys();
    }
    public int getDaysAllowedForBambooKeys() {
        return getAdminConfigResourcesModel().getDaysToKeepBambooKeys();
    }
    public long  getMillisBetweenRuns() {
        return getAdminConfigResourcesModel().getMillisBetweenRuns();
    }

    public String getAuthorizedUser() {
        return getAdminConfigResourcesModel().getBambooUser();
    }
    
    public String getInternalKeyPolicyLink(String defaultValue){
        return null == getAdminConfigResourcesModel().getInternalKeyPolicyLink()?defaultValue:getAdminConfigResourcesModel().getInternalKeyPolicyLink();
    }

}
