package com.nativeverificationappbe.api.models;

import java.io.Serial;
import java.io.Serializable;

public class NINandSelfieDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String nin;
    private String image;

    public NINandSelfieDTO() {}

    public NINandSelfieDTO(String nin, String image){
        this.nin = nin;
        this.image = image;
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

    @Override
    public String toString() {
        return "NINandSelfieDTO [nin=" + nin + ", image=" + image + "]";
    }

}
