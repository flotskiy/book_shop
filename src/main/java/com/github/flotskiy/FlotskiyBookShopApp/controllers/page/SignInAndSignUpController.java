package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.security.ContactConfirmationPayload;
import com.github.flotskiy.FlotskiyBookShopApp.security.RegistrationForm;
import com.github.flotskiy.FlotskiyBookShopApp.security.ContactConfirmationResponse;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
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
import java.util.logging.Logger;

@Controller
public class SignInAndSignUpController extends HeaderController {

    @Autowired
    public SignInAndSignUpController(UserRegistrationService userRegistrationService) {
        super(userRegistrationService);
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
    public ContactConfirmationResponse handleRequestContactConfirmation(@RequestBody ContactConfirmationPayload payload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");
        return response;
    }

    @PostMapping("/approveContact")
    @ResponseBody
    public ContactConfirmationResponse handleApproveContact(@RequestBody ContactConfirmationPayload payload) {
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
    public ContactConfirmationResponse handleLogin(
            @RequestBody ContactConfirmationPayload payload, HttpServletResponse httpServletResponse
    ) {
        ContactConfirmationResponse loginResponse = getUserRegistrationService().jwtLogin(payload);
        Cookie cookie = new Cookie("token", loginResponse.getResult());
        httpServletResponse.addCookie(cookie);
        return loginResponse;
    }

    @GetMapping("/my")
    public String handleMy() {
        return "/my";
    }

    @GetMapping("/myarchive")
    public String handleMyArchive() {
        return "/myarchive";
    }

    @GetMapping("/profile")
    public String handleProfile(Model model) {
        model.addAttribute("curUser", getUserRegistrationService().gerCurrentUser());
        return "/profile";
    }

//    @GetMapping("/logout")
//    public String handleLogout(HttpServletRequest request) {
//        HttpSession session = request.getSession();
//        SecurityContextHolder.clearContext();
//        if (session != null) {
//            session.invalidate();
//        }
//        for (Cookie cookie : request.getCookies()) {
//            cookie.setMaxAge(0);
//        }
//        return "redirect:/";
//    }
}
