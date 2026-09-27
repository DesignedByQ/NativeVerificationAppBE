package com.nativeverificationappbe.api.controllers;

import com.nativeverificationappbe.api.services.AddressServices;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/nativeverificationappbe/api/address")
@RequiredArgsConstructor
public class AddressController {


    private final static Logger logger = LoggerFactory.getLogger(AddressController.class);
    private final AddressServices addressServices;

    @GetMapping(value = "/getaddress", produces = "application/json")
    public ResponseEntity<Map<String, Object>> getAddress(@RequestParam String userId, HttpServletRequest request) {

        //var user = userId else se
        logger.info(userId);

        // Get user address

        return ResponseEntity.status(HttpStatus.OK).body(addressServices.getUserAddress(userId));

    }

}
