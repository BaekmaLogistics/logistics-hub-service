package com.sparta.logistics.application.common.service;

import com.sparta.logistics.application.common.dto.Coordinate;

public interface GeocodingService {

    Coordinate getCoordinate(String address);
}
