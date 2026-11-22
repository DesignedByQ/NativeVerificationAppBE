package com.nativeverificationappbe.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.DocumentType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentDTO {

    @JsonProperty("documentType")
    private String documentType;
    @JsonProperty("documentNumber")
    private String documentNumber;
    @JsonProperty("documentImage")
    private String documentImage;
    @JsonProperty("expiryDate")
    private String documentExpiration;

//    @JsonProperty("nationalId")
//    private NationalIdentity nationalId;
//    @JsonProperty("votersCard")
//    private VotersCard votersCard;
//    @JsonProperty("driversLicense")
//    private DriversLicense driversLicense;
//    @JsonProperty("passport")
//    private Passport passport;

}
