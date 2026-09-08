package com.nativeverificationappbe.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PremblyLivelinessResponseVerification implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("status")
    private String status;

    @JsonProperty("reference")
    private String reference;

    public PremblyLivelinessResponseVerification(){}

    public PremblyLivelinessResponseVerification(String status, String reference){
        this.status = status;
        this.reference = reference;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getReference(){
        return reference;
    }

    public void setReference(String reference){
        this.reference = reference;
    }

    @Override
    public String toString(){
        return "PremblyLivelinessResponseVerification [status=" + status + ", reference=" + reference + "]";
    }

}