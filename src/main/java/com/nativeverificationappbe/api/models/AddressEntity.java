package com.nativeverificationappbe.api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "address")
public class AddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;
    private Long number;
    private String buildingName;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String description;
    private Double latitude;
    private Double longitude;

}
