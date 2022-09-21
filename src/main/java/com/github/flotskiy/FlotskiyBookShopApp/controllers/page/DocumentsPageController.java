package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/documents")
public class DocumentsPageController extends HeaderController {

    @GetMapping
    public String documentsPage() {
        return "/documents/index";
    }
}
