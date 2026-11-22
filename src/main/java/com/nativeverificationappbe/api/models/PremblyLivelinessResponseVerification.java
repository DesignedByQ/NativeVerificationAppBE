package com.nativeverificationappbe.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PremblyLivelinessResponseVerification {

    @JsonProperty("status")
    private String status;

    @JsonProperty("reference")
    private String reference;

}