package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.post.ContactConfirmPayloadDto;
import com.github.flotskiy.FlotskiyBookShopApp.security.RegistrationForm;
import com.github.flotskiy.FlotskiyBookShopApp.security.ContactConfirmationResponse;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import com.github.flotskiy.FlotskiyBookShopApp.service.UserBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.management.InstanceAlreadyExistsException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Controller
public class SignInAndSignUpController extends HeaderController {

    private final UserBookService userBookService;

    @Autowired
    public SignInAndSignUpController(
            UserRegistrationService userRegistrationService, BookService bookService, UserBookService userBookService) {
        super(userRegistrationService, bookService);
        this.userBookService = userBookService;
    }

    @GetMapping("/signin")
    public String handleSignIn() {
        return "/signin";
    }

    @GetMapping("/signup")
    public String handleSignUp(Model model) {
        model.addAttribute("regForm", new RegistrationForm());
        return "/signup";
    }

    @PostMapping("/requestContactConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestContactConfirmation(@RequestBody ContactConfirmPayloadDto payload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");
        return response;
    }

    @PostMapping("/approveContact")
    @ResponseBody
    public ContactConfirmationResponse handleApproveContact(@RequestBody ContactConfirmPayloadDto payload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");
        return response;
    }

    @PostMapping("/reg")
    public String handleUserRegistrationForm(RegistrationForm registrationForm, Model model) {
        try {
            getUserRegistrationService().registerNewUserWithContact(registrationForm);
            model.addAttribute("regOk", true);
        } catch (InstanceAlreadyExistsException ex) {
            Logger.getLogger(this.getClass().getSimpleName()).warning(ex.getMessage());
            model.addAttribute("regOk", false);
        }
        return "/signin";
    }

    @PostMapping("/login")
    @ResponseBody
    public Map<String, String> handleLogin(
            @RequestBody ContactConfirmPayloadDto payload, HttpServletResponse httpServletResponse
    ) {
        HashMap<String, String> result = new HashMap<>();
        try {
            ContactConfirmationResponse loginResponse = getUserRegistrationService().jwtLogin(payload);
            Cookie cookie = new Cookie("token", loginResponse.getResult());
            httpServletResponse.addCookie(cookie);
            result.put("result", loginResponse.getResult());
        } catch (Exception exception) {
            String message = getUserRegistrationService().getExceptionInfo(exception);
            result.put("error", message);
        }
        return result;
    }

    @GetMapping("/my")
    public String handleMy(Model model) {
        userBookService.handleUnreadRequest(model);
        return "/my";
    }

    @GetMapping("/myarchive")
    public String handleMyArchive(Model model) {
        userBookService.handleArchivedRequest(model);
        return "/myarchive";
    }

    @GetMapping("/profile")
    public String handleProfile(Model model) {
        model.addAttribute("curUser", getUserRegistrationService().gerCurrentUser());
        return "/profile";
    }
}
