package com.nativeverificationappbe.api.services;

import com.nativeverificationappbe.api.models.UserCredentialsEntity;
import com.nativeverificationappbe.api.models.VerificationRequestEntity;
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
            return null;
        }

    }
    

}
