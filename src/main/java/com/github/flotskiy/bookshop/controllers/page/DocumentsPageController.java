package com.github.flotskiy.bookshop.controllers.page;

import com.github.flotskiy.bookshop.security.UserRegistrationService;
import com.github.flotskiy.bookshop.service.BookService;
import com.github.flotskiy.bookshop.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/documents")
public class DocumentsPageController extends HeaderController {

    private final UserInfoService userInfoService;

    @Autowired
    public DocumentsPageController(
            UserRegistrationService userRegistrationService, BookService bookService, UserInfoService userInfoService
    ) {
        super(userRegistrationService, bookService);
        this.userInfoService = userInfoService;
    }

    @GetMapping
    public String documentsPage(Model model) {
        model.addAttribute("docList", userInfoService.getAllDocuments());
        return "/documents/index";
    }

    @GetMapping("/{slug}")
    public String bookPage(@PathVariable("slug") String slug, Model model) {
        model.addAttribute("doc", userInfoService.getDocumentDto(slug));
        return "/documents/slug";
    }
}
