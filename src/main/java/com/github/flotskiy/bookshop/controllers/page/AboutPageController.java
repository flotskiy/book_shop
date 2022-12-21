package com.github.flotskiy.bookshop.controllers.page;

import com.github.flotskiy.bookshop.security.UserRegistrationService;
import com.github.flotskiy.bookshop.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@PropertySource("application-variables.properties")
@RequestMapping("/about")
public class AboutPageController extends HeaderController {

    @Value("${page.about}")
    private String about;

    @Autowired
    public AboutPageController(UserRegistrationService userRegistrationService, BookService bookService) {
        super(userRegistrationService, bookService);
    }

    @GetMapping
    public String aboutPage(Model model) {
        model.addAttribute("about", about);
        return "/about";
    }
}
