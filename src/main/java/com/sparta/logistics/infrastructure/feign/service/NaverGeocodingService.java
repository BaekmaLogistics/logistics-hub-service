package com.sparta.logistics.infrastructure.feign.service;

import com.sparta.logistics.application.common.dto.Coordinate;
import com.sparta.logistics.application.common.service.GeocodingService;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.infrastructure.feign.client.NaverGeocodingClient;
import com.sparta.logistics.infrastructure.feign.dto.gecoding.GeocodingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NaverGeocodingService implements GeocodingService {

    private final NaverGeocodingClient naverGeocodingClient;

    @Override
    public Coordinate getCoordinate(String address){
        GeocodingResponse response = naverGeocodingClient.geocode(address);

        if(response.getAddresses() == null || response.getAddresses().isEmpty()){
            throw new ApiException(ErrorResponseCode.INVALID_ADDRESS);
        }

        return response.getAddresses().get(0).toCoordinate();
    }
}
