package com.sparta.logistics.infrastructure.feign.dto.gecoding;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.logistics.application.common.dto.Coordinate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GeocodingAddress {

    @JsonProperty("roadAddress")
    private String roadAddress;

    @JsonProperty("jibunAddress")
    private String jibunAddress;

    @JsonProperty("x")
    private String longitude;

    @JsonProperty("y")
    private String latitude;

    public Coordinate toCoordinate() {
        return new Coordinate(
                Double.parseDouble(latitude),
                Double.parseDouble(longitude)
        );
    }
}
