package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import com.github.flotskiy.FlotskiyBookShopApp.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/faq")
public class FaqPageController extends HeaderController {

    private final UserInfoService userInfoService;

    @Autowired
    public FaqPageController(
            UserRegistrationService userRegistrationService, BookService bookService, UserInfoService userInfoService
    ) {
        super(userRegistrationService, bookService);
        this.userInfoService = userInfoService;
    }

    @GetMapping
    public String faqPage(Model model) {
        model.addAttribute("faqList", userInfoService.getAllFaq());
        return "/faq";
    }
}
