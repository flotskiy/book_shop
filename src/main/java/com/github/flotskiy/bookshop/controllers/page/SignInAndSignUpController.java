package com.github.flotskiy.bookshop.controllers.page;

import com.github.flotskiy.bookshop.exceptions.*;
import com.github.flotskiy.bookshop.model.dto.post.ContactConfirmPayloadDto;
import com.github.flotskiy.bookshop.security.RegistrationForm;
import com.github.flotskiy.bookshop.security.ContactConfirmationResponse;
import com.github.flotskiy.bookshop.security.UserRegistrationService;
import com.github.flotskiy.bookshop.service.BookService;
import com.github.flotskiy.bookshop.service.CodeService;
import com.github.flotskiy.bookshop.service.UserBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Controller
public class SignInAndSignUpController extends HeaderController {

    private static final String RESULT_KEY = "result";
    private static final String ERROR_KEY = "error";

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

    @PostMapping("/requestContact")
    @ResponseBody
    public Map<String, Boolean> handleRequestContact(@RequestBody ContactConfirmPayloadDto payload) {
        Map<String, Boolean> result = new HashMap<>();
        boolean isApproved = codeService.isContactAlreadyRegisteredAndApproved(payload.getContact());
        result.put(RESULT_KEY, isApproved);
        return result;
    }

    @PostMapping(value = {"/requestContactConfirmation", "/requestEmailConfirmation"})
    @ResponseBody
    public Map<String, Object> handleRequestContactConfirmation(@RequestBody ContactConfirmPayloadDto payload) {
        Map<String, Object> result = new HashMap<>();
        try {
            String contact = payload.getContact();
            result.put(RESULT_KEY, "true");
            String code = codeService.getConfirmationCode(contact);
            getUserRegistrationService()
                    .registerOrUpdateNewUserWithContactWhileRequestingContactConfirmation(contact, code);
        } catch (AttemptsNumberExceededException | TimeoutMinutesRemainsConfirmationException timedException) {
            result.put("secondsToWait", Integer.parseInt(timedException.getMessage()));
            result.put(ERROR_KEY, timedException.getClass().getSimpleName());
        } catch (ConfirmationException confirmationException) {
            result.put(ERROR_KEY, confirmationException.getMessage());
        } catch (Exception exception) {
            result.put(RESULT_KEY, "false");
            result.put(ERROR_KEY, exception.getMessage());
            return result;
        }
        return result;
    }

    @PostMapping("/approveContact")
    @ResponseBody
    public Map<String, Object> handleApproveContact(@RequestBody ContactConfirmPayloadDto payload) {
        Map<String, Object> result = new HashMap<>();
        try {
            codeService.verifyCode(payload);
            result.put(RESULT_KEY, true);
        } catch (ExpiredCodeException | CodeTrailsLimitExceededException approveSpecificException) {
            result.put(RESULT_KEY, false);
            result.put("return", true);
            result.put(ERROR_KEY, approveSpecificException.getMessage());
        } catch (UserChangeException userChangeException) {
            result.put(RESULT_KEY, false);
            result.put(ERROR_KEY, userChangeException.getMessage());
        } catch (Exception exception) {
            result.put(RESULT_KEY, false);
            result.put(ERROR_KEY, "Code is not approved");
        }
        return result;
    }

    @PostMapping("/reg")
    public String handleUserRegistrationForm(RegistrationForm registrationForm, Model model, HttpServletRequest request) {
        model.addAttribute("regForm", new RegistrationForm());
        try {
            String contact = getUserRegistrationService().registerUserPassword(registrationForm);
            request.login(contact, registrationForm.getPass());
            model.addAttribute("regOk", true);
        } catch (Exception exception) {
            Logger.getLogger(this.getClass().getSimpleName()).warning(exception.getMessage());
            model.addAttribute("regOk", false);
        }
        return "/signup";
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
            result.put(RESULT_KEY, loginResponse.getResult());
        } catch (Exception exception) {
            String message = getUserRegistrationService().getExceptionInfo(exception);
            result.put(ERROR_KEY, message);
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
