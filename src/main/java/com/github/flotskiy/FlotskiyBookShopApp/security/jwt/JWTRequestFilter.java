package com.github.flotskiy.FlotskiyBookShopApp.security.jwt;

import com.github.flotskiy.FlotskiyBookShopApp.security.BookstoreUserDetails;
import com.github.flotskiy.FlotskiyBookShopApp.security.BookstoreUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

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
            throws ServletException, IOException {
        boolean isTokenInactive = false;
        String token = null;
        String userName = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    token = cookie.getValue();
                    userName = jwtService.extractUserName(token);
                }

                if (userName != null) {
                    String hash = inactiveJwtService.getStringHash(token);
                    inactiveJwtService.removeExpired();
                    InactiveJwt inactiveJwt = inactiveJwtService.findInactiveJwtByHash(hash);
                    if (inactiveJwt != null) {
                        Logger.getLogger(this.getClass().getSimpleName()).info("current token is inactive, access denied");
                        isTokenInactive = true;
                    }
                }

                if (userName != null && !isTokenInactive && SecurityContextHolder.getContext().getAuthentication() == null) {
                    BookstoreUserDetails userDetails =
                            (BookstoreUserDetails) bookstoreUserDetailsService.loadUserByUsername(userName);
                    if (jwtService.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
