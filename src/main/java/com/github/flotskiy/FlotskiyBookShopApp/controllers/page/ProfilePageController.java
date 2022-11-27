package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.post.ChangeUserDataConfirmPayload;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.BalanceTransactionDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.CountedBalanceTransactionsDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.UserDto;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import com.github.flotskiy.FlotskiyBookShopApp.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
        List<BalanceTransactionDto> transactionDtoList =
                paymentService.getBalanceTransactions(currentUserId, 0, 50, "desc").getContent();
        int count = paymentService.getNumberOfTransactionsOfUser(currentUserId);
        model.addAttribute("curUser", getUserRegistrationService().gerCurrentUser());
        model.addAttribute("transactions", transactionDtoList);
        model.addAttribute("transactionsCount", count);
        return "/profile";
    }

    @PostMapping("/profile")
    public String handleProfilePostRequest(ChangeUserDataConfirmPayload payload, Model model) {
        UserDto currentUser = getUserRegistrationService().gerCurrentUser();
        String result = getUserRegistrationService().changeUserData(payload, currentUser);
        List<BalanceTransactionDto> transactionDtoList =
                paymentService.getBalanceTransactions(currentUser.getId(), 0, 50, "desc").getContent();
        int count = paymentService.getNumberOfTransactionsOfUser(currentUser.getId());
        model.addAttribute("changeResult", result);
        model.addAttribute("curUser", currentUser);
        model.addAttribute("transactions", transactionDtoList);
        model.addAttribute("transactionsCount", count);
        return "/profile";
    }

    @GetMapping("/transactions")
    @ResponseBody
    public ResponseEntity<?> getNextPopularPage(
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
}
