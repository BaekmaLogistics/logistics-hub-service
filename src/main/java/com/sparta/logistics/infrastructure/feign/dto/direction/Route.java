package com.sparta.logistics.infrastructure.feign.dto.direction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class Route {

    @JsonProperty("trafast")
    private List<Trafast> trafasts;
}
