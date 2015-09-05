package com.edwardawebb.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
public class KeyDetailsResourceModel {

    @XmlElement(name = "value")
    private String message;

    public KeyDetailsResourceModel() {
    }

    public KeyDetailsResourceModel(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}