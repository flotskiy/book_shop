package com.github.flotskiy.FlotskiyBookShopApp.security.jwt;

import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Logger;

@Service
public class InactiveJwtService {

    private final JWTService jwtService;
    private final InactiveJwtRepository inactiveJwtRepository;

    @Autowired
    public InactiveJwtService(JWTService jwtService, InactiveJwtRepository inactiveJwtRepository) {
        this.jwtService = jwtService;
        this.inactiveJwtRepository = inactiveJwtRepository;
    }

    public void putJwtToInactiveRepo(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String token = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    token = cookie.getValue();
                }
            }
        }

        if (token == null) {
            Logger.getLogger(this.getClass().getSimpleName()).info("No token found. Nothing to save in InactiveRepo");
            return;
        }

        String hash = getStringHash(token);
        Date expiry = jwtService.extractExpiration(token);
        LocalDateTime localDateTimeExpiry = LocalDateTime.ofInstant(expiry.toInstant(), ZoneId.systemDefault());

        InactiveJwt newInactiveJwt = new InactiveJwt();
        newInactiveJwt.setHash(hash);
        newInactiveJwt.setExpiry(localDateTimeExpiry);
        inactiveJwtRepository.save(newInactiveJwt);
    }

    public String getStringHash(String inputString) {
        return Hashing.sha256().hashString(inputString, StandardCharsets.UTF_8).toString();
    }

    public void removeExpired() {
        inactiveJwtRepository.removeInactiveJwtsByExpiryBefore(LocalDateTime.now());
    }

    public InactiveJwt findInactiveJwtByHash(String hash) {
        return inactiveJwtRepository.findInactiveJwtByHash(hash);
    }
}
