package com.github.flotskiy.bookshop.security.jwt;

import com.github.flotskiy.bookshop.security.BookstoreUserDetailsService;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    private static final String LOGOUT_PAGE = "/logout";
    private final Logger customLogger = Logger.getLogger(this.getClass().getSimpleName());

    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final JWTService jwtService;
    private final InactiveJwtService inactiveJwtService;

    @Autowired
    public JWTRequestFilter(
            BookstoreUserDetailsService bookstoreUserDetailsService,
            JWTService jwtService,
            InactiveJwtService inactiveJwtService
    ) {
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.jwtService = jwtService;
        this.inactiveJwtService = inactiveJwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    handleCookie(cookie, request);
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            customLogger.warning("JWT expired. Removing this user from the system");
            response.sendRedirect(LOGOUT_PAGE);
        } catch (JwtException jwtException) {
            customLogger.warning("JWT is wrong. " + jwtException.getLocalizedMessage() +
                    " Removing this user from the system");
            response.sendRedirect(LOGOUT_PAGE);
        } catch (Exception exception) {
            customLogger.warning(exception.getLocalizedMessage() +
                    " in doFilterInternal method. Removing this user from the system");
            response.sendRedirect(LOGOUT_PAGE);
        }
    }

    private void handleCookie(Cookie cookieValue, HttpServletRequest httpServletRequest) {
        boolean isTokenInactive = false;
        String token = null;
        String userName = null;
        if (cookieValue.getName().equals("token")) {
            token = cookieValue.getValue();
            userName = jwtService.extractUserName(token);
        }

        if (userName != null) {
            String hash = inactiveJwtService.getStringHash(token);
            inactiveJwtService.removeExpired();
            InactiveJwt inactiveJwt = inactiveJwtService.findInactiveJwtByHash(hash);
            if (inactiveJwt != null) {
                customLogger.info("current token is inactive, access denied");
                isTokenInactive = true;
            }
        }

        if (userName != null && !isTokenInactive && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = bookstoreUserDetailsService.loadUserByUsername(userName);
            if (Boolean.TRUE.equals(jwtService.validateToken(token, userDetails))) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
    }
}
