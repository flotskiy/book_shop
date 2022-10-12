package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/faq")
public class FaqPageController extends HeaderController {

    @Autowired
    public FaqPageController(UserRegistrationService userRegistrationService) {
        super(userRegistrationService);
    }

    @GetMapping
    public String faqPage() {
        return "/faq";
    }
}
