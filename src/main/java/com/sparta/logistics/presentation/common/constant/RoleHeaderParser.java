package com.sparta.logistics.presentation.common.constant;

import com.sparta.logistics.domain.model.UserRole;

public class RoleHeaderParser {

    private static final String ROLE_PREFIX = "ROLE_";

    private RoleHeaderParser(){

    }

    public static UserRole parse(String roleHeader){
        String normalizedRole = roleHeader.startsWith(ROLE_PREFIX)
                ? roleHeader.substring(ROLE_PREFIX.length())
                : roleHeader;

        return UserRole.valueOf(normalizedRole);
    }
}
