package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.exceptions.CodesNotEqualsException;
import com.github.flotskiy.FlotskiyBookShopApp.exceptions.ExpiredCodeException;
import com.github.flotskiy.FlotskiyBookShopApp.exceptions.UserContactEntityNotApproved;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.post.ContactConfirmPayloadDto;
import com.github.flotskiy.FlotskiyBookShopApp.security.RegistrationForm;
import com.github.flotskiy.FlotskiyBookShopApp.security.ContactConfirmationResponse;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import com.github.flotskiy.FlotskiyBookShopApp.service.CodeService;
import com.github.flotskiy.FlotskiyBookShopApp.service.UserBookService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.management.InstanceAlreadyExistsException;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Controller
public class SignInAndSignUpController extends HeaderController {

    @Value("${appEmail.email}")
    private String email;

    private final UserBookService userBookService;
    private final CodeService codeService;
    private final JavaMailSender javaMailSender;

    @Autowired
    public SignInAndSignUpController(
            UserRegistrationService userRegistrationService,
            BookService bookService,
            UserBookService userBookService,
            CodeService codeService,
            JavaMailSender javaMailSender
    ) {
        super(userRegistrationService, bookService);
        this.userBookService = userBookService;
        this.codeService = codeService;
        this.javaMailSender = javaMailSender;
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
        try {
            String code = codeService.generateSecretCodeForUserContactEntityPhone(payload.getContact());
            getUserRegistrationService()
                    .registerNewUserWithContactWhileRequestingContactConfirmation(payload.getContact(), code);
        } catch (InstanceAlreadyExistsException existsException) {
            response.setResult(null);
            response.setError(existsException.getMessage());
        }
        return response;
    }

    @PostMapping("/requestEmailConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestEmailConfirmation(@RequestBody ContactConfirmPayloadDto payload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        String code = RandomStringUtils.random(6, false, true);
        code = code.substring(0, 3) + " " + code.substring(3, 6);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(email);
        message.setTo(payload.getContact());
        message.setSubject("FlotskiyBookShop email verification!");
        message.setText("Verification code is: " + code);
        message.setSentDate(new Date());
        javaMailSender.send(message);
        try {
            getUserRegistrationService()
                    .registerNewUserWithContactWhileRequestingContactConfirmation(payload.getContact(), code);
        } catch (InstanceAlreadyExistsException existsException) {
            response.setResult(null);
            response.setError(existsException.getMessage());
        }
        response.setResult("true");
        return response;
    }

    @PostMapping("/approveContact")
    @ResponseBody
    public ContactConfirmationResponse handleApproveContact(@RequestBody ContactConfirmPayloadDto payload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        try {
            if (codeService.verifyCode(payload)) {
                response.setResult("true");
            } else {
                response.setResult("false");
                response.setError("Exceeded the number of attempts to enter the code");
            }
        } catch (EntityNotFoundException | ExpiredCodeException | CodesNotEqualsException exception) {
            response.setResult(null);
            response.setError(exception.getMessage());
        }
        return response;
    }

    @PostMapping("/reg")
    public String handleUserRegistrationForm(RegistrationForm registrationForm, Model model) {
        try {
            getUserRegistrationService().registerUserPassword(registrationForm);
            model.addAttribute("regOk", true);
        } catch (UserContactEntityNotApproved ex) {
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
}
