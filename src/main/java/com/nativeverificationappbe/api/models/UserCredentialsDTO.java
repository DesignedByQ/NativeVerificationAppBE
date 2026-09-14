package com.nativeverificationappbe.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCredentialsDTO {

    private String title;
    private String firstname;
    private String middlename;
    private String surname;
    private String birthdate;
    private String gender;
    private String nin;
    private String phone;
    private String email;
    private String password;

}
