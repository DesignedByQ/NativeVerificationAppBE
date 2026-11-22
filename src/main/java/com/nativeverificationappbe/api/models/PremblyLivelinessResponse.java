package com.nativeverificationappbe.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.Map;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PremblyLivelinessResponse {

    @JsonProperty("status")
    private Boolean status;

    @JsonProperty("detail")
    private String detail;

    @JsonProperty("response_code")
    private String responseCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("confidence")
    private Double confidence;

    @JsonProperty("confidence_in_percentage")
    private Double confidenceInPercentage;

    @JsonProperty("verification")
    private PremblyLivelinessResponseVerification verification;

    @JsonProperty("data")
    private PremblyLivelinessResponseData data;

    @JsonProperty("widget_info")
    private Map<String, Object> widgetInfo;

    @JsonProperty("session")
    private Map<String, Object> session;

}
