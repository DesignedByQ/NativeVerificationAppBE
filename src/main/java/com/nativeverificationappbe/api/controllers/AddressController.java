package com.nativeverificationappbe.api.controllers;

import com.nativeverificationappbe.api.models.AddressDetails;
import com.nativeverificationappbe.api.models.CoordsDetails;
import com.nativeverificationappbe.api.services.AddressServicesImp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/nativeverificationappbe/api/address")
@RequiredArgsConstructor
public class AddressController {


    private final static Logger logger = LoggerFactory.getLogger(AddressController.class);
    private final AddressServicesImp addressServices;

    @GetMapping(value = "/getaddress", produces = "application/json")
    public ResponseEntity<Map<String, Object>> getAddress(@RequestParam String userId, HttpServletRequest request) {

        //var user = userId else se
        //logger.info(userId);

        // Get user address

        return ResponseEntity.status(HttpStatus.OK).body(addressServices.getUserAddress(userId));

    }

    @PostMapping(value = "/saveaddress", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Boolean> saveAddress(@RequestBody AddressDetails addressDTO, HttpServletRequest request) {

        if (addressDTO.getUserAddress().getLatitude() == null || addressDTO.getUserAddress().getLongitude() == null) {
            throw new IllegalStateException("Address coordinates are invalid or null");
        }

        HttpSession session = request.getSession(false);

        String user = session.getAttribute("USER_ID").toString();

        if (user != null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(addressServices.saveUserAddress(user, addressDTO));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }

    }

    @PostMapping(value = "/checkrange", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> checkrange(@RequestBody CoordsDetails coords, HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        String userId = session.getAttribute("USER_ID").toString();

        if (userId == null) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of(
                            "inRange", false,
                    "message", "User ID is invalid or blank"
                )
            );
        }

        if (coords.getDeviceCoords().getLatitude() == null || coords.getDeviceCoords().getLongitude() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of(
                            "inRange", false,
                    "message", "Device coordinates are invalid or blank."
                )
            );
        }

        //Calculate the range difference
        Double calculatedDistance = addressServices.calculateRangeInMeters(
                coords.getDeviceCoords().getLatitude(),
                coords.getDeviceCoords().getLongitude(),
                userId
        );

        if (calculatedDistance == null) {
            throw new IllegalStateException("Calculated distance is null");
        }

        //logger.info("calculatedDistance: {}", calculatedDistance);

        double maxAllowedMeters = 50.0;

        boolean inRange = calculatedDistance <= maxAllowedMeters;

        return ResponseEntity.ok(
                Map.of(
                        "inRange", inRange,
                        "range", Math.round(calculatedDistance * 100.0) / 100.0, // Round to 2 decimal places
                        "message", inRange ? "Within valid radius" : "Outside valid range"
            )
        );


        //Get the address coords from the db
    }

}
