package com.github.flotskiy.bookshop.security.jwt;

import com.github.flotskiy.bookshop.repository.InactiveJwtRepository;
import com.google.common.hash.Hashing;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

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
            logger.info("No token found. Nothing to save in InactiveRepo");
            return;
        }

        String hash = getStringHash(token);
        Date expiry;
        try {
            expiry = jwtService.extractExpiration(token);
        } catch (ExpiredJwtException expiredJwtException) {
            logger.warning("JWT Expired. No need to put it in inactiveJwtRepository");
            return;
        }
        LocalDateTime localDateTimeExpiry = LocalDateTime.ofInstant(expiry.toInstant(), ZoneId.systemDefault());

        InactiveJwt newInactiveJwt = new InactiveJwt();
        newInactiveJwt.setHash(hash);
        newInactiveJwt.setExpiry(localDateTimeExpiry);
        inactiveJwtRepository.save(newInactiveJwt);
    }

    public String getStringHash(String inputString) {
        return Hashing.sha256().hashString(inputString, StandardCharsets.UTF_8).toString();
    }

    @Transactional
    public void removeExpired() {
        inactiveJwtRepository.deleteInactiveJwtsByExpiryBefore(LocalDateTime.now());
    }

    public InactiveJwt findInactiveJwtByHash(String hash) {
        return inactiveJwtRepository.findInactiveJwtByHash(hash);
    }
}
