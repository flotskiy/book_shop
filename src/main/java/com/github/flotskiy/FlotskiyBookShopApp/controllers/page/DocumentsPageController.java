package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/documents")
public class DocumentsPageController extends HeaderController {

    @Autowired
    public DocumentsPageController(UserRegistrationService userRegistrationService, BookService bookService) {
        super(userRegistrationService, bookService);
    }

    @GetMapping
    public String documentsPage() {
        return "/documents/index";
    }
}
