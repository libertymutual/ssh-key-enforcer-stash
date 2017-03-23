package com.lmig.forge.stash.ssh.rest;

import com.atlassian.bitbucket.user.ApplicationUser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserDetails {
    private String username;
    private int id;

    public UserDetails(String username, int id) {
        this.username = username;
        this.id = id;
    }

    public UserDetails(ApplicationUser user) {
        this.username = user.getSlug();
        this.id = user.getId();
    }


    public String getUsername() {
        return username;
    }

    public UserDetails setUsername(String username) {
        this.username = username;
        return this;
    }

    public int getId() {
        return id;
    }

    public UserDetails setId(int id) {
        this.id = id;
        return this;
    }
}
