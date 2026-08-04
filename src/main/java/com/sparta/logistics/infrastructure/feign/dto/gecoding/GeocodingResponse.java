package com.sparta.logistics.infrastructure.feign.dto.gecoding;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GeocodingResponse {

    private String status;

    private List<GeocodingAddress> addresses;
}
