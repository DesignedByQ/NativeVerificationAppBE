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
public class PremblyLivelinessResponseNinData {

    @JsonProperty("title")
    private String title;

    @JsonProperty("firstname")
    private String firstname;

    @JsonProperty("middlename")
    private String middlename;

    @JsonProperty("surname")
    private String surname;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("birthdate")
    private String birthdate;

    @JsonProperty("nin")
    private String nin;

    @JsonProperty("telephoneno")
    private String telephoneno;

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

}
