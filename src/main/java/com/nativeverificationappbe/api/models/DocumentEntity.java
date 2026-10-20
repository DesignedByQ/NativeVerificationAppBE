package com.nativeverificationappbe.api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "documents")
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private NationalIdentity nationalId;
    private VotersCard votersCard;
    private DriversLicense driversLicense;
    private Passport passport;
    private UserCredentialsEntity userCredentialsEntity;

}
