package com.nativeverificationappbe.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties("true")
public class CoordsDetails {

    //addressCoords: addressCoords, // Pass verified address coordinates to backend
    @JsonProperty("gatePhoto")
    private String gatePhoto;
    @JsonProperty("doorPhoto")
    private String doorPhoto;
    @JsonProperty("facePhoto")
    private String facePhoto;
    @JsonProperty("deviceCoords")
    private CoordsDTO deviceCoords;

}



