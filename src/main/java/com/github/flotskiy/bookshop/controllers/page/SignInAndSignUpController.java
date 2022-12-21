package com.github.flotskiy.bookshop.controllers.page;

import com.github.flotskiy.bookshop.exceptions.CodesNotEqualsException;
import com.github.flotskiy.bookshop.exceptions.ExpiredCodeException;
import com.github.flotskiy.bookshop.exceptions.UserContactEntityNotApproved;
import com.github.flotskiy.bookshop.model.dto.post.ContactConfirmPayloadDto;
import com.github.flotskiy.bookshop.security.RegistrationForm;
import com.github.flotskiy.bookshop.security.ContactConfirmationResponse;
import com.github.flotskiy.bookshop.security.UserRegistrationService;
import com.github.flotskiy.bookshop.service.BookService;
import com.github.flotskiy.bookshop.service.CodeService;
import com.github.flotskiy.bookshop.service.UserBookService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Controller
public class SignInAndSignUpController extends HeaderController {

    private final UserBookService userBookService;
    private final CodeService codeService;

    @Autowired
    public SignInAndSignUpController(
            UserRegistrationService userRegistrationService,
            BookService bookService,
            UserBookService userBookService,
            CodeService codeService
    ) {
        super(userRegistrationService, bookService);
        this.userBookService = userBookService;
        this.codeService = codeService;
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
        codeService.sendEmailConfirmationCode(payload.getContact(), code);
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
            if (Boolean.TRUE.equals(codeService.verifyCode(payload))) {
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
