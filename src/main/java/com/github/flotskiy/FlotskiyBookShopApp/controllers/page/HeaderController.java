package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.HeaderInfoDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;

@Controller
public class HeaderController {

    @ModelAttribute("headerInfoDto")
    public HeaderInfoDto headerInfoDto(HttpServletRequest request) {
        HeaderInfoDto headerInfoDto = new HeaderInfoDto();

        Cookie cartContents = WebUtils.getCookie(request, "cartContents");
        if (cartContents != null && !cartContents.getValue().equals("")) {
            String cartCookieString = cartContents.getValue();
            ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(cartCookieString.split("/")));
            headerInfoDto.setCartBooksCount(cookieBooks.size());
        }

        Cookie keptContents = WebUtils.getCookie(request, "keptContents");
        if (keptContents != null && !keptContents.getValue().equals("")) {
            String keptCookieString = keptContents.getValue();
            ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(keptCookieString.split("/")));
            headerInfoDto.setKeptBooksCount(cookieBooks.size());
        }

        return headerInfoDto;
    }
}
