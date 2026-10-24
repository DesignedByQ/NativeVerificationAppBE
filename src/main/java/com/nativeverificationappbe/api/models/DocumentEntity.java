package com.nativeverificationappbe.api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "documents")
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "documents_id")
    private Long documentsId;

    // National ID
    @Column(name = "national_doc_number")
    private String nationalDocNumber;

    @Lob
    @Column(name = "national_doc_image", columnDefinition = "TEXT")
    private String nationalDocImage;

    @Column(name = "national_doc_expiry")
    private String nationalDocExpiry;

    // Voter's Card
    @Column(name = "voters_doc_number")
    private String votersDocNumber;

    @Lob
    @Column(name = "voters_doc_image", columnDefinition = "TEXT")
    private String votersDocImage;

    @Column(name = "voters_doc_expiry")
    private String votersDocExpiry;

    // Driver's License
    @Column(name = "drivers_doc_number")
    private String driversDocNumber;

    @Lob
    @Column(name = "drivers_doc_image", columnDefinition = "TEXT")
    private String driversDocImage;

    @Column(name = "drivers_doc_expiry")
    private String driversDocExpiry;

    // Passport
    @Column(name = "passport_doc_number")
    private String passportDocNumber;

    @Lob
    @Column(name = "passport_doc_image", columnDefinition = "TEXT")
    private String passportDocImage;

    @Column(name = "passport_doc_expiry")
    private String passportDocExpiry;

    // User Relationship
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_owner", nullable = false)
    private UserCredentialsEntity userCredentialsEntity;
}