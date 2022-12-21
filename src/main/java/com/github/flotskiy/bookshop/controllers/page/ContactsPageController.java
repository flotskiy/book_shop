package com.github.flotskiy.bookshop.controllers.page;

import com.github.flotskiy.bookshop.model.dto.post.MessageForm;
import com.github.flotskiy.bookshop.security.UserRegistrationService;
import com.github.flotskiy.bookshop.service.BookService;
import com.github.flotskiy.bookshop.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.logging.Logger;

@Controller
@RequestMapping("/contacts")
public class ContactsPageController extends HeaderController {

    private final UserInfoService userInfoService;

    @Autowired
    public ContactsPageController(
            UserRegistrationService userRegistrationService, BookService bookService, UserInfoService userInfoService
    ) {
        super(userRegistrationService, bookService);
        this.userInfoService = userInfoService;
    }

    @GetMapping
    public String contactsPage() {
        return "/contacts";
    }

    @PostMapping
    public String sendMessage(MessageForm messageForm, Model model) {
        try {
            int userId = getUserRegistrationService().getCurrentUserDto().getId();
            userInfoService.createNewMessage(messageForm, userId);
            model.addAttribute("fail", "false");
        } catch (Exception exception) {
            model.addAttribute("fail", "true");
            model.addAttribute("form", messageForm);
            Logger.getLogger(this.getClass().getSimpleName()).warning(exception.getMessage());
        }
        return "/contacts";
    }
}
