package com.lmig.forge.stash.ssh.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name = "config")
@XmlAccessorType(XmlAccessType.FIELD)
public class AdminConfigResourceModel {

    public static final int DEFAULT_DAYS_USER = 90;
    public static final int DEFAULT_DAYS_BAMBOO = 365;
    public static final long DEFAULT_MILLIS_BETWEEN_RUNS = 1000 * 60 * 60 * 24; //one day
    @XmlElement(name = "message")
    private String message="";
    @XmlElement(name = "authorizedGroup")
    private String authorizedGroup;
    @XmlElement(name = "bambooUser")
    private String bambooUser;
    @XmlElement(name = "daysToKeepUserKeys")
    private int daysToKeepUserKeys = DEFAULT_DAYS_USER;
    @XmlElement(name = "daysToKeepBambooKeys")
    private int daysToKeepBambooKeys = DEFAULT_DAYS_BAMBOO;
    @XmlElement(name = "millisBetweenRuns")
    private long millisBetweenRuns = DEFAULT_MILLIS_BETWEEN_RUNS;
    @XmlElement(name = "internalKeyPolicyLink")
    private String internalKeyPolicyLink ;


    public AdminConfigResourceModel() {
    }




    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthorizedGroup() {
        return authorizedGroup;
    }

    public void setAuthorizedGroup(String authorizedGroup) {
        this.authorizedGroup = authorizedGroup;
    }

    public int getDaysToKeepUserKeys() {
        return daysToKeepUserKeys;
    }

    public void setDaysToKeepUserKeys(int daysToKeepUserKeys) {
        this.daysToKeepUserKeys = daysToKeepUserKeys;
    }

    public int getDaysToKeepBambooKeys() {
        return daysToKeepBambooKeys;
    }

    public void setDaysToKeepBambooKeys(int daysToKeepBambooKeys) {
        this.daysToKeepBambooKeys = daysToKeepBambooKeys;
    }

    public long getMillisBetweenRuns() {
        return millisBetweenRuns;
    }

    public void setMillisBetweenRuns(long millisBetweenRuns) {
        this.millisBetweenRuns = millisBetweenRuns;
    }

    public String getBambooUser() {
       return bambooUser;
    }
    public void setBambooUser(String username){
        this.bambooUser = username;
    }



    public String getInternalKeyPolicyLink() {
        return internalKeyPolicyLink;
    }



    public void setInternalKeyPolicyLink(String internalKeyPolicyLink) {
        this.internalKeyPolicyLink = internalKeyPolicyLink;
    }


}