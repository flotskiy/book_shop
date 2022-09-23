package com.github.flotskiy.FlotskiyBookShopApp.service;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.StringJoiner;

@Service
public class CookieService {

    public void handleCartCookie(String cookieString, String slug, HttpServletResponse response, Model model) {
        if (cookieString == null || cookieString.equals("")) {
            Cookie cookie = new Cookie("cartContents", slug);
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute("isCartEmpty", false);
        } else if (!cookieString.contains(slug)) {
            StringJoiner joiner = new StringJoiner("/");
            joiner.add(cookieString).add(slug);
            Cookie cookie = new Cookie("cartContents", joiner.toString());
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute("isCartEmpty", false);
        }
    }

    public void handleKeptCookie(String cookieString, String slug, HttpServletResponse response, Model model) {
        if (cookieString == null || cookieString.equals("")) {
            Cookie cookie = new Cookie("keptContents", slug);
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute("isKeptEmpty", false);
        } else if (!cookieString.contains(slug)) {
            StringJoiner joiner = new StringJoiner("/");
            joiner.add(cookieString).add(slug);
            Cookie cookie = new Cookie("keptContents", joiner.toString());
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute("isKeptEmpty", false);
        }
    }
}
