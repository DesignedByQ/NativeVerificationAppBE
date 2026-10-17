package com.nativeverificationappbe.api.services;

import com.nativeverificationappbe.api.models.*;
import com.nativeverificationappbe.api.repos.AddressRepo;
import com.nativeverificationappbe.api.repos.UserCredRepo;
import com.nativeverificationappbe.api.repos.VerificationRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AddressServicesImp implements  AddressServices {

    private static final Logger logger = LoggerFactory.getLogger(AddressServicesImp.class);

    private final AddressRepo addressRepo;

    private final Mapper mapper;

    private final UserCredRepo userCredRepo;

    private final VerificationRepo verificationRepo;

    @Override
    public Map<String, Object> getUserAddress(String user_id){

        Long userId = Long.parseLong(user_id);

        try {

            UserCredentialsEntity user = userCredRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

            VerificationRequestEntity verification = verificationRepo.findUserByAccountId(user.getAccountId());

            if (verification == null) {
                throw new EntityNotFoundException("Verification record not found for accountId: ${user.accountId}");
            } else {

                return Map.of(
                        "userAddress", user.getAddress(),
                        "verification", verification.getAddressVerified()
                );
            }
        } catch (Exception e) {
            logger.error("Error retrieving user's address.");
            return Map.of(
                    "error", "Failed to fetch address",
                    "userAddress", "null",
                    "verification", "null"
            );
        }

    }

    // Function to save address
    @Override
    public Boolean saveUserAddress(String user_id, AddressDetails addressDTO) {

        Long userId = Long.parseLong(user_id);

        // 1. Find user
        UserCredentialsEntity user = userCredRepo.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // 3. Save the new Address entity FIRST (turns it from transient to persistent)
        AddressEntity newAddress = addressRepo.save(mapper.addressDTOtoEntity(addressDTO));

        //Add address and save user
        user.setAddress(newAddress);
        userCredRepo.save(user);
        return true;
    }

    @Override
    public Double calculateRangeInMeters(Double deviceLat, Double deviceLng, String user_id) {

        Long userId = Long.parseLong(user_id);

        UserCredentialsEntity user = userCredRepo.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found: " + user_id));

        Double addressLat = user.getAddress().getLatitude();
        Double addressLng = user.getAddress().getLongitude();

        logger.info("addressLat: {}", addressLat);
        logger.info("addressLng: {}", addressLng);
        logger.info("deviceLat: {}", deviceLat);
        logger.info("deviceLng: {}", deviceLng);

        if (addressLat == null || addressLng == null || deviceLat == null || deviceLng == null) {
            return null;
        }

        final double earthRadiusMeters = 6_371_000.0;

        double dLat = Math.toRadians(deviceLat - addressLat);
        double dLng = Math.toRadians(deviceLng - addressLng);

        double a =
                Math.pow(Math.sin(dLat / 2), 2)
                        + Math.cos(Math.toRadians(addressLat))
                        * Math.cos(Math.toRadians(deviceLat))
                        * Math.pow(Math.sin(dLng / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanceMeters = earthRadiusMeters * c;

        logger.info("{} is {} meters within their saved address.", userId, distanceMeters);

        return distanceMeters;
    }

}





