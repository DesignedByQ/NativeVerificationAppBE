package com.nativeverificationappbe.api.services;

import com.nativeverificationappbe.api.models.AddressDetails;

import java.util.Map;

public interface AddressServices {

    Map<String, Object> getUserAddress(String userId);

    Boolean saveUserAddress(String user_id, AddressDetails addressDTO);

    Double calculateRangeInMeters(Double deviceLat, Double deviceLng, String user_id);
}
