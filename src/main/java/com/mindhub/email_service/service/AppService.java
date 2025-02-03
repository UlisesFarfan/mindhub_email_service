package com.mindhub.email_service.service;

import com.mindhub.email_service.config.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppService {
    @Autowired
    private JwtUtils jwtUtils;

    public String getEmail(HttpServletRequest request){
        String token = extraerToken(request);
        String email = jwtUtils.extractUsername(token);
        return email;
    }

    public Long getId(HttpServletRequest request){
        String token = extraerToken(request);
        Claims claims = jwtUtils.parseClaims(token);
        if (claims != null && claims.containsKey("id")) {
            Long userId = Long.parseLong(claims.get("id", String.class));
            return userId;
        } else {
            return null;
        }
    }

    public String extraerToken(HttpServletRequest request){
        return request.getHeader("Authorization").substring(7);
    }

}
