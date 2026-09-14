package com.nativeverificationappbe.api.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PremblyLivelinessResponseData {

    @JsonProperty("detail")
    private String detail;

    @JsonProperty("status")
    private Boolean status;

    @JsonProperty("response_code")
    private String responseCode;

    @JsonProperty("nin_data")
    private PremblyLivelinessResponseNinData ninData;

    @JsonProperty("face_data")
    private PremblyLivelinessResponseFaceData faceData;

}

