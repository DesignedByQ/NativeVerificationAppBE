package com.nativeverificationappbe.api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_credentials")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCredentialsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(
            name = "user_seq",
            sequenceName = "user_sequence",
            initialValue = 100000,
            allocationSize = 1
    )

    private Long accountId;
    private String title;
    private String firstname;
    private String middlename;
    private String surname;
    private String birthdate;
    private String gender;
    @Column(unique = true)
    private String nin;
    @Column(unique = true)
    private String phone;
    @Column(unique = true)
    private String email;
    private String password;

//    @OneToOne
//    @JoinColumn(name="address", nullable=true)
//    private AddressEntity address;

}

