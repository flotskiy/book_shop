package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.post.ChangeUserDataConfirmPayload;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.UserDto;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfilePageController extends HeaderController {

    @Autowired
    public ProfilePageController(UserRegistrationService userRegistrationService, BookService bookService) {
        super(userRegistrationService, bookService);
    }

    @GetMapping("/profile")
    public String handleProfile(Model model) {
        model.addAttribute("curUser", getUserRegistrationService().gerCurrentUser());
        return "/profile";
    }

    @PostMapping("/profile")
    public String handleProfilePostRequest(ChangeUserDataConfirmPayload payload, Model model) {
        UserDto currentUser = getUserRegistrationService().gerCurrentUser();
        String result = getUserRegistrationService().changeUserData(payload, currentUser);
        model.addAttribute("changeResult", result);
        model.addAttribute("curUser", currentUser);
        return "/profile";
    }
}
