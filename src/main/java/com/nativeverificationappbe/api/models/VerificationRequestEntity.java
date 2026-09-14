package com.nativeverificationappbe.api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_request")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long verificationReqId;
    private Integer phoneOTP;
    private Boolean phoneVerified = false;
    private Integer emailOTP;
    private Boolean emailVerified = false;
    private LocalDateTime otpCreatedAt = null;
    @Column(unique= true)
    private String expoToken;
    @OneToOne
    @JoinColumn(name="user", nullable=false)
    private UserCredentialsEntity userCredentialsEntity;
    private Boolean addressVerified = false;
    //var password: String,
}
