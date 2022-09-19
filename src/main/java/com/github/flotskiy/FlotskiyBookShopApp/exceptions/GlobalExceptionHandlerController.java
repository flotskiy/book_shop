package com.github.flotskiy.FlotskiyBookShopApp.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.logging.Logger;

@ControllerAdvice
public class GlobalExceptionHandlerController {

    @ExceptionHandler(EmptySearchQueryException.class)
    public String handleEmptySearchQueryException(
            EmptySearchQueryException emptySearchQueryException, RedirectAttributes redirectAttributes
    ) {
        Logger.getLogger(this.getClass().getSimpleName()).warning(emptySearchQueryException.getLocalizedMessage());
        redirectAttributes.addFlashAttribute("searchError", emptySearchQueryException);
        return "redirect:/";
    }
}
