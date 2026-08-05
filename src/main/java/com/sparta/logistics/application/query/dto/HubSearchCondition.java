package com.sparta.logistics.application.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HubSearchCondition {
    private String name;
    private String address;
}
