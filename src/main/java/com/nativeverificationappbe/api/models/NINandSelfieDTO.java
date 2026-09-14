package com.nativeverificationappbe.api.models;

import java.io.Serial;
import java.io.Serializable;

public class NINandSelfieDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String nin;
    private String image;
    private String birthdate;

    public NINandSelfieDTO(){}

    public NINandSelfieDTO(String nin, String image, String birthdate){
        this.nin = nin;
        this.image = image;
        this.birthdate = birthdate;
    }

    public String getNin() {
        return nin;
    }

    public void setNin(String nin) {
        this.nin = nin;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBirthdate() { return birthdate; }

    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }

    @Override
    public String toString() {
        return "NINandSelfieDTO [nin=" + nin + ", image=" + image + ", birthdate=" + birthdate + "]";
    }

}
