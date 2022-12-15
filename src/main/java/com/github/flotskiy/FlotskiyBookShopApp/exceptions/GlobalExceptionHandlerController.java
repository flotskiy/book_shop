package com.github.flotskiy.FlotskiyBookShopApp.exceptions;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@ControllerAdvice
public class GlobalExceptionHandlerController {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @ExceptionHandler(EmptySearchQueryException.class)
    public String handleEmptySearchQueryException(EmptySearchQueryException exception, RedirectAttributes attributes) {
        logger.warning(exception.getLocalizedMessage());
        attributes.addFlashAttribute("searchError", true);
        return "redirect:/search";
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public Map<String, Object> handleAccessDeniedException(AccessDeniedException exception) {
        logger.warning(exception.getLocalizedMessage());
        HashMap<String, Object> result = new HashMap<>();
        result.put("result", false);
        result.put("error", "Access denied for not authenticated user");
        return result;
    }
}
