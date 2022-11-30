package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.payments.PaymentDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.post.ChangeUserDataConfirmPayload;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.payments.BalanceTransactionDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.CountedBalanceTransactionsDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.UserDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserEntity;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import com.github.flotskiy.FlotskiyBookShopApp.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProfilePageController extends HeaderController {

    private final PaymentService paymentService;

    @Autowired
    public ProfilePageController(
            UserRegistrationService userRegistrationService,
            BookService bookService,
            PaymentService paymentService
    ) {
        super(userRegistrationService, bookService);
        this.paymentService = paymentService;
    }

    @GetMapping("/profile")
    public String handleProfile(Model model) {
        Integer currentUserId = getUserRegistrationService().getCurrentUserId();
        UserEntity currentUserEntity = getUserRegistrationService().getCurrentUserEntity(currentUserId);
        List<BalanceTransactionDto> transactionDtoList =
                paymentService.getBalanceTransactions(currentUserId, 0, 50, "desc").getContent();
        int count = paymentService.getNumberOfTransactionsOfUser(currentUserEntity);
        model.addAttribute("curUser", getUserRegistrationService().gerCurrentUser());
        model.addAttribute("transactions", transactionDtoList);
        model.addAttribute("transactionsCount", count);
        return "/profile";
    }

    @PostMapping("/profile")
    public String handleProfilePostRequest(ChangeUserDataConfirmPayload payload, Model model) {
        UserDto currentUser = getUserRegistrationService().gerCurrentUser();
        UserEntity currentUserEntity = getUserRegistrationService().getCurrentUserEntity(currentUser.getId());
        String result = getUserRegistrationService().changeUserData(payload, currentUser);
        List<BalanceTransactionDto> transactionDtoList =
                paymentService.getBalanceTransactions(currentUser.getId(), 0, 50, "desc").getContent();
        int count = paymentService.getNumberOfTransactionsOfUser(currentUserEntity);
        model.addAttribute("changeResult", result);
        model.addAttribute("curUser", currentUser);
        model.addAttribute("transactions", transactionDtoList);
        model.addAttribute("transactionsCount", count);
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
            return ResponseEntity.status(400).body(result);
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
}
