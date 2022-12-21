package com.github.flotskiy.bookshop.controllers.page;

import com.github.flotskiy.bookshop.model.dto.payments.PaymentDto;
import com.github.flotskiy.bookshop.model.dto.post.ChangeUserDataConfirmPayload;
import com.github.flotskiy.bookshop.model.dto.payments.BalanceTransactionDto;
import com.github.flotskiy.bookshop.model.dto.user.CountedBalanceTransactionsDto;
import com.github.flotskiy.bookshop.model.dto.user.UserDto;
import com.github.flotskiy.bookshop.model.dto.user.VerificationTokenDto;
import com.github.flotskiy.bookshop.model.entity.user.UserEntity;
import com.github.flotskiy.bookshop.security.OnChangeUserDataEvent;
import com.github.flotskiy.bookshop.security.UserRegistrationService;
import com.github.flotskiy.bookshop.service.BookService;
import com.github.flotskiy.bookshop.service.PaymentService;
import com.github.flotskiy.bookshop.service.RegisteredUserChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Controller
@PropertySource("application-variables.properties")
public class ProfilePageController extends HeaderController {

    @Value("${initial.offset}")
    private int offset;

    @Value("${transactions.limit}")
    private int transactionsLimit;

    private final PaymentService paymentService;
    private final RegisteredUserChangeService registeredUserChangeService;
    private final ApplicationEventPublisher eventPublisher;

    private static final String RESULT_KEY = "result";
    private static final String ERROR_KEY = "error";
    private static final String TOKEN_KEY = "token";
    private static final String PROFILE_PAGE = "/profile";

    @Autowired
    public ProfilePageController(
            UserRegistrationService userRegistrationService,
            BookService bookService,
            PaymentService paymentService,
            RegisteredUserChangeService registeredUserChangeService,
            ApplicationEventPublisher eventPublisher
    ) {
        super(userRegistrationService, bookService);
        this.paymentService = paymentService;
        this.registeredUserChangeService = registeredUserChangeService;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/profile")
    public String handleProfile(@RequestParam(value = "sort", required = false) String sort, Model model) {
        handleModelForProfilePage(sort, model);
        return PROFILE_PAGE;
    }

    @PostMapping("/profile")
    public String handleProfilePostRequest(
            @RequestParam(value = "sort", required = false) String sort,
            ChangeUserDataConfirmPayload payload,
            Model model
    ) {
        UserDto currentUser = handleModelForProfilePage(sort, model);
        String result = registeredUserChangeService.changeUserData(payload, currentUser);
        if (result.equals("phone")) {
            registeredUserChangeService.handleDataForPhoneUser(payload, currentUser);
            return "/phone";
        }
        if (result.equals("mail")) {
            eventPublisher.publishEvent(new OnChangeUserDataEvent(currentUser, "http://localhost:8085", payload));
        }
        model.addAttribute("changeResult", result);
        return PROFILE_PAGE;
    }

    @GetMapping("/confirmDataChange")
    public String confirmEmailChangeData(
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam("token") String token,
            Model model
    ) {
        handleModelForProfilePage(sort, model);
        VerificationTokenDto verificationTokenDto = registeredUserChangeService.getVerificationTokenDto(token);
        if (Boolean.FALSE.equals(verificationTokenDto.isValid())) {
            model.addAttribute(TOKEN_KEY, "expired");
        } else {
            try {
                registeredUserChangeService.changeAllUserData(verificationTokenDto);
                handleModelForProfilePage(sort, model);
                model.addAttribute(TOKEN_KEY, "success");
            } catch (Exception exception) {
                Logger.getLogger(this.getClass().getSimpleName()).warning(exception.getMessage());
                model.addAttribute(TOKEN_KEY, ERROR_KEY);
            }
        }
        return PROFILE_PAGE;
    }

    @GetMapping("/transactions")
    @ResponseBody
    public ResponseEntity<Object> getNextBalanceTransactionsPage(
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam("offset") Integer offset,
            @RequestParam("limit") Integer limit
    ) {
        Integer currentUserId = getUserRegistrationService().getCurrentUserIdIncludingGuest();
        try {
            List<BalanceTransactionDto> balanceTransactionDtoList =
                    paymentService.getBalanceTransactions(currentUserId, offset, limit, sort).getContent();
            CountedBalanceTransactionsDto countedBalanceTransactionsDto =
                    new CountedBalanceTransactionsDto(balanceTransactionDtoList);
            int countedTransactions = countedBalanceTransactionsDto.getCount();
            if (countedTransactions < limit) {
                return ResponseEntity.status(206).body(countedBalanceTransactionsDto);
            }
            return ResponseEntity.ok(countedBalanceTransactionsDto);
        } catch (Exception exception) {
            Map<String, Object> result = new HashMap<>();
            result.put(RESULT_KEY, false);
            result.put(ERROR_KEY, exception.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/payment")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleReplenish(@RequestBody PaymentDto payload) {
        Map<String, Object> result = new HashMap<>();
        try {
            paymentService.makeReplenish(payload);
            String replenishUrl = paymentService.getReplenishUrl(payload.getSum());
            result.put(RESULT_KEY, true);
            result.put("data", replenishUrl);
            return ResponseEntity.ok().body(result);
        } catch (Exception exception) {
            result.put(RESULT_KEY, false);
            result.put(ERROR_KEY, exception.getMessage());
            return ResponseEntity.ok().body(result);
        }
    }

    private UserDto handleModelForProfilePage(String sort, Model model) {
        if (sort == null || !sort.equalsIgnoreCase("asc")) {
            sort = "desc";
        }
        UserDto userDto = getUserRegistrationService().getCurrentUserDto();
        int currentUserId = userDto.getId();
        UserEntity currentUserEntity = getUserRegistrationService().getCurrentUserEntity(currentUserId);
        List<BalanceTransactionDto> transactionDtoList =
                paymentService.getBalanceTransactions(currentUserId, offset, transactionsLimit, sort).getContent();
        int count = paymentService.getNumberOfTransactionsOfUser(currentUserEntity);
        model.addAttribute("sort", sort);
        model.addAttribute("curUser", userDto);
        model.addAttribute("transactions", transactionDtoList);
        model.addAttribute("transactionsCount", count);
        return userDto;
    }
}
