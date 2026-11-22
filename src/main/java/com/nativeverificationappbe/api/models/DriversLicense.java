package com.nativeverificationappbe.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DriversLicense {

    @JsonProperty("docNumber")
    private String docNumber;
    @JsonProperty("docType")
    private String docType;
    @JsonProperty("docImage")
    private String docImage;
    @JsonProperty("docExpiry")
    private String docExpiry;

}
