package com.edwardawebb.stash.ssh.rest;

import java.security.KeyPair;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name = "key")
@XmlAccessorType(XmlAccessType.FIELD)
public class KeyPairResourceModel {

    @XmlElement(name = "publicKey")
    private String publicKey;
    @XmlElement(name = "privateKey")
    private String privateKey;
    @XmlElement(name = "fingerprint")
    private String fingerprint;

    public KeyPairResourceModel() {
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    

    
}