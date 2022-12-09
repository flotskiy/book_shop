package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.payments.PaymentDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.post.ChangeUserDataConfirmPayload;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.payments.BalanceTransactionDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.CountedBalanceTransactionsDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.UserDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.VerificationTokenDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserEntity;
import com.github.flotskiy.FlotskiyBookShopApp.security.OnChangeUserDataEvent;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import com.github.flotskiy.FlotskiyBookShopApp.service.PaymentService;
import com.github.flotskiy.FlotskiyBookShopApp.service.RegisteredUserChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Controller
public class ProfilePageController extends HeaderController {

    private final PaymentService paymentService;
    private final RegisteredUserChangeService registeredUserChangeService;
    private final ApplicationEventPublisher eventPublisher;

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
    public String handleProfile(Model model) {
        handleModelForProfilePage(model);
        return "/profile";
    }

    @PostMapping("/profile")
    public String handleProfilePostRequest(ChangeUserDataConfirmPayload payload, Model model) {
        UserDto currentUser = handleModelForProfilePage(model);
        String result = registeredUserChangeService.changeUserData(payload, currentUser);
        if (result.equals("phone")) {
            registeredUserChangeService.handleDataForPhoneUser(payload, currentUser);
            return "/phone";
        }
        if (result.equals("mail")) {
            eventPublisher.publishEvent(new OnChangeUserDataEvent(currentUser, "http://localhost:8085", payload));
        }
        model.addAttribute("changeResult", result);
        return "/profile";
    }

    @GetMapping("/confirmDataChange")
    public String confirmEmailChangeData(@RequestParam("token") String token, Model model) {
        handleModelForProfilePage(model);
        VerificationTokenDto verificationTokenDto = registeredUserChangeService.getVerificationTokenDto(token);
        if (!verificationTokenDto.isValid()) {
            model.addAttribute("token", "expired");
        } else {
            try {
                registeredUserChangeService.changeAllUserData(verificationTokenDto);
                handleModelForProfilePage(model);
                model.addAttribute("token", "success");
            } catch (Throwable throwable) {
                Logger.getLogger(this.getClass().getSimpleName()).warning(throwable.getMessage());
                model.addAttribute("token", "error");
            }
        }
        return "/profile";
    }

    @GetMapping("/transactions")
    @ResponseBody
    public ResponseEntity<?> getNextBalanceTransactionsPage(
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam("offset") Integer offset,
            @RequestParam("limit") Integer limit
    ) {
        Integer currentUserId = getUserRegistrationService().getCurrentUserId();
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
            result.put("result", false);
            result.put("error", exception.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/payment")
    @ResponseBody
    public ResponseEntity<?> handleReplenish(@RequestBody PaymentDto payload) {
        Map<String, Object> result = new HashMap<>();
        try {
            paymentService.makeReplenish(payload);
            String replenishUrl = paymentService.getReplenishUrl(payload.getSum());
            result.put("result", true);
            result.put("data", replenishUrl);
            return ResponseEntity.ok().body(result);
        } catch (Throwable throwable) {
            result.put("result", false);
            result.put("error", throwable.getMessage());
            return ResponseEntity.ok().body(result);
        }
    }

    private UserDto handleModelForProfilePage(Model model) {
        UserDto userDto = getUserRegistrationService().getCurrentUserDto();
        int currentUserId = userDto.getId();
        UserEntity currentUserEntity = getUserRegistrationService().getCurrentUserEntity(currentUserId);
        List<BalanceTransactionDto> transactionDtoList =
                paymentService.getBalanceTransactions(currentUserId, 0, 50, "desc").getContent();
        int count = paymentService.getNumberOfTransactionsOfUser(currentUserEntity);
        model.addAttribute("curUser", userDto);
        model.addAttribute("transactions", transactionDtoList);
        model.addAttribute("transactionsCount", count);
        return userDto;
    }
}
