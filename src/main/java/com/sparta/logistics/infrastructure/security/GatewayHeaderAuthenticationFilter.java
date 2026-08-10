package com.sparta.logistics.infrastructure.security;

import com.sparta.logistics.presentation.common.constant.HeaderConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class GatewayHeaderAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String userIdHeader = request.getHeader(HeaderConstants.USER_ID);
        String userRoleHeader = request.getHeader(HeaderConstants.USER_ROLE);

        if(userIdHeader != null && userRoleHeader != null){
            setAuthentication(userIdHeader, userRoleHeader);
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(
            String userIdHeader,
            String userRoleHeader
    ){
        try{
            UUID userId = UUID.fromString(userIdHeader);
            UserRole userRole = UserRole.valueOf(userRoleHeader);

            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_"+userRole.name());

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    List.of(authority)
            );

            SecurityContextHolder.getContext()
                    .setAuthentication(authenticationToken);
        } catch (IllegalArgumentException e){
            SecurityContextHolder.clearContext();
        }
    }
}
