package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/contacts")
public class ContactsPageController extends HeaderController {

    @Autowired
    public ContactsPageController(UserRegistrationService userRegistrationService) {
        super(userRegistrationService);
    }

    @GetMapping
    public String contactsPage() {
        return "/contacts";
    }
}
