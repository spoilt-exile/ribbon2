/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2020 Freax Software
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package tk.freaxsoftware.ribbon2.gateway.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.ZonedDateTime;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import tk.freaxsoftware.ribbon2.gateway.GatewayMain;
import tk.freaxsoftware.ribbon2.gateway.entity.UserEntity;

/**
 * JWT encoding/decoding service.
 * @author Stanislav Nepochatov
 */
public class JWTTokenService {
    
    private static JWTTokenService instance;
    
    /**
     * Security key for JWT signing.
     */
    private final SecretKey jwtKey;
    
    /**
     * Valid days of JWT token.
     */
    private final Integer validDays;
    
    /**
     * Private controller.
     * @param secret JWT secret;
     * @param validHours valid hours value;
     */
    private JWTTokenService(String secret, Integer validDays) {
        byte[] jwtSecret = secret.getBytes();
        jwtKey = new SecretKeySpec(jwtSecret, SignatureAlgorithm.HS512.getJcaName());
        this.validDays = validDays;
    }
    
    /**
     * Encrypt token for certain user.
     * @param user given user;
     * @return encrypted token;
     */
    public String encryptToken(UserEntity user) {
        return Jwts.builder().setId(user.getLogin()).setExpiration(Date.from(ZonedDateTime.now().plusDays(validDays).toInstant())).signWith(SignatureAlgorithm.HS256, jwtKey).compact();
    }
    
    /**
     * Decrypt token from string to claims instance. May throws unchecked exceptions.
     * @param tokenValue raw token value;
     * @return claims instance;
     */
    public Claims decryptToken(String tokenValue) {
        return Jwts.parser().verifyWith(jwtKey).build().parseSignedClaims(tokenValue).getPayload();
    }
    
    /**
     * Get instance of JWT token service.
     * @return initiated service instance;
     */
    public static JWTTokenService getInstance() {
        if (instance == null) {
            instance = new JWTTokenService(GatewayMain.appConfig.getHttp().getAuthTokenSecret(), GatewayMain.appConfig.getHttp().getAuthTokenValidDays());
        }
        return instance;
    }
    
}
