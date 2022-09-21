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
        Cookie cookie = WebUtils.getCookie(request, "cartContents");
        if (cookie != null && !cookie.getValue().equals("")) {
            String cookieString = cookie.getValue();
            ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(cookieString.split("/")));
            headerInfoDto.setCartBooksCount(cookieBooks.size());
        }
        return headerInfoDto;
    }
}
