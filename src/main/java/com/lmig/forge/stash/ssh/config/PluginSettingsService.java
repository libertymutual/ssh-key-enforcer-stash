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
        return config;
    }

   @Transactional
    public AdminConfigResourceModel updateAdminConfigResourcesModel(final AdminConfigResourceModel updatedConfig) {

       Validate.isTrue(updatedConfig.getDaysToKeepBambooKeys() >= 1,"must keep keys at least 1 day");
       Validate.isTrue(updatedConfig.getDaysToKeepUserKeys() >= 1,"must keep keys at least 1 day");
       Validate.isTrue(updatedConfig.getMillisBetweenRuns() >= 60000,"Must space at least 1 minute apart");
        PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
        
        settings.put(SETTINGS_KEY_AUTHORIZED_GROUP, updatedConfig.getAuthorizedGroup());
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

}
