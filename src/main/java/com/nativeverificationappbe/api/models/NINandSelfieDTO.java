package com.nativeverificationappbe.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NINandSelfieDTO {

    private String nin;
    private String image;
    private String birthdate;

}
