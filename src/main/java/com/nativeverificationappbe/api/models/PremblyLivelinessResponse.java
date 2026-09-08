package com.nativeverificationappbe.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PremblyLivelinessResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("status")
    private Boolean status;

    @JsonProperty("detail")
    private String detail;

    @JsonProperty("response_code")
    private String responseCode;

    @JsonProperty("confidence")
    private Double confidence;

    @JsonProperty("confidence_in_percentage")
    private Double confidenceInPercentage;

    @JsonProperty("verification")
    private PremblyLivelinessResponseVerification verification;

    @JsonProperty("widget_info")
    private Map<String, Object> widgetInfo;

    @JsonProperty("session")
    private Map<String, Object> session;

    public PremblyLivelinessResponse(){}

    public PremblyLivelinessResponse(Boolean status, String detail, String responseCode, Double confidence, Double confidenceInPercentage, PremblyLivelinessResponseVerification verification, Map<String, Object> widgetInfo, Map<String, Object> session){
        this.status = status;
        this.detail = detail;
        this.responseCode = responseCode;
        this.confidence = confidence;
        this.confidenceInPercentage = confidenceInPercentage;
        this.verification = verification;
        this.widgetInfo = widgetInfo;
        this.session = session;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status){
        this.status = status;
    }

    public String getDetail(){
        return detail;
    }

    public void setDetail(String detail){
        this.detail = detail;
    }

    public String getResponseCode(){
        return responseCode;
    }

    public void setResponseCode(String responseCode){
        this.responseCode = responseCode;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence){
        this.confidence = confidence;
    }

    public Double getConfidenceInPercentage() {
        return confidenceInPercentage;
    }

    public void setConfidenceInPercentage(Double confidenceInPercentage){
        this.confidenceInPercentage = confidenceInPercentage;
    }

    public PremblyLivelinessResponseVerification getVerification(){
        return verification;
    }

    public void setVerification(PremblyLivelinessResponseVerification verification) {
        this.verification = verification;
    }

    public Map<String, Object> getWidgetInfo() {
        return widgetInfo;
    }

    public void setWidgetInfo(Map<String, Object> widgetInfo){
        this.widgetInfo = widgetInfo;
    }

    public Map<String, Object> getSession() {
        return session;
    }

    public void setSession(Map<String, Object> session){
        this.session = session;
    }

    @Override
    public String toString() {
        return "PremblyLivelinessResponse [status=" + status + ", detail=" + detail + ", responseCode=" + responseCode + ", confidence=" + confidence + ", confidenceInPercentage=" + confidenceInPercentage + ", verification=" + verification + ", widgetInfo=" + widgetInfo + ", session=" + session + "]";
    }

}
