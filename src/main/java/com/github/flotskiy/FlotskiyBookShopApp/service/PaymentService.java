package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.exceptions.UserBalanceNotEnoughException;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.payments.BalanceTransactionDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.payments.PaymentDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.UserDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.links.Book2UserEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.payments.BalanceTransactionEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserEntity;
import com.github.flotskiy.FlotskiyBookShopApp.repository.BalanceTransactionRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.Book2UserRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.UserRepository;
import com.github.flotskiy.FlotskiyBookShopApp.util.CustomStringHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class PaymentService {

    @Value("${robokassa.merchant.login}")
    private String merchantLogin;

    @Value("${robokassa.pass.first.test}")
    private String firstPass;

    private final UserRepository userRepository;
    private final Book2UserRepository book2UserRepository;
    private final BalanceTransactionRepository balanceTransactionRepository;
    private final CustomStringHandler customStringHandler;

    @Autowired
    public PaymentService(
            UserRepository userRepository,
            Book2UserRepository book2UserRepository,
            BalanceTransactionRepository balanceTransactionRepository,
            CustomStringHandler customStringHandler
    ) {
        this.userRepository = userRepository;
        this.book2UserRepository = book2UserRepository;
        this.balanceTransactionRepository = balanceTransactionRepository;
        this.customStringHandler = customStringHandler;
    }

    public String getReplenishUrl(String sum) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        String invId = "1";
        md.update((merchantLogin + ":" + sum + ":" + invId + ":" + firstPass).getBytes());
        return "https://auth.robokassa.ru/Merchant/Index.aspx" +
                "?MerchantLogin=" + merchantLogin +
                "&InvId=" + invId +
                "&Culture=ru" +
                "&Encoding=utf-8" +
                "&OutSum=" + sum +
                "&SignatureValue=" + DatatypeConverter.printHexBinary(md.digest()).toUpperCase() +
                "&IsTest=1";
    }

    @Transactional
    public void pay(UserDto currentUser) {
        int currentUserId = currentUser.getId();

        List<BookDto> cartBooks = currentUser.getUserBooksData().getCart();
        int paymentTotal = cartBooks.stream().mapToInt(BookDto::getDiscountPrice).sum();
        if (currentUser.getBalance() < paymentTotal) {
            throw new UserBalanceNotEnoughException("Total Payment exceeds balance available");
        }

        UserEntity currentUserEntity = userRepository.findById(currentUser.getId()).get();
        int newBalance = currentUserEntity.getBalance() - paymentTotal;
        currentUserEntity.setBalance(newBalance);
        userRepository.save(currentUserEntity);

        for (BookDto bookDto : cartBooks) {
            int bookId = bookDto.getId();
            Book2UserEntity book2UserEntity = book2UserRepository.findBook2UserEntityByBookIdAndUserId(bookId, currentUserId);
            book2UserEntity.setTypeId(3); // typeId == 3 is equals to status 'PAID'
            book2UserEntity.setTime(LocalDateTime.now());
            book2UserRepository.save(book2UserEntity);

            String description = bookDto.getSlug() + ":" + bookDto.getTitle();
            BalanceTransactionEntity balanceTransactionEntity = new BalanceTransactionEntity();
            balanceTransactionEntity.setUserId(currentUserEntity);
            balanceTransactionEntity.setTime(LocalDateTime.now());
            balanceTransactionEntity.setValue(bookDto.getDiscountPrice());
            balanceTransactionEntity.setBookId(bookId);
            balanceTransactionEntity.setDescription(description);
            balanceTransactionRepository.save(balanceTransactionEntity);
        }
    }

    public Page<BalanceTransactionDto> getBalanceTransactions(Integer userId, Integer offset, Integer limit, String sort) {
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<BalanceTransactionEntity> balanceTransactionEntityPage;
        if (sort.equals("desc")) {
            balanceTransactionEntityPage =
                    balanceTransactionRepository.getSortedBalanceTransactionsForUserOrderByTimeDesc(userId, nextPage);
        } else {
            balanceTransactionEntityPage =
                    balanceTransactionRepository.getSortedBalanceTransactionsForUserOrderByTimeAsc(userId, nextPage);
        }
        return balanceTransactionEntityPage.map(this::convertBalanceTransactionEntityToDto);
    }

    public Integer getNumberOfTransactionsOfUser(UserEntity userEntity) {
        return balanceTransactionRepository.countBalanceTransactionEntitiesByUserId(userEntity);
    }

    @Transactional
    public void makeReplenish(PaymentDto payload) {
        int currentUserId = Integer.parseInt(payload.getHash());
        int sumToReplenish = Integer.parseInt(payload.getSum());
        UserEntity currentUserEntity = userRepository.findById(currentUserId).get();
        int newBalance = currentUserEntity.getBalance() + sumToReplenish;
        currentUserEntity.setBalance(newBalance);
        userRepository.save(currentUserEntity);

        String description = "Account replenishment";
        LocalDateTime creationTime =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(payload.getTime()), ZoneId.systemDefault());
        BalanceTransactionEntity balanceTransactionEntity = new BalanceTransactionEntity();
        balanceTransactionEntity.setUserId(currentUserEntity);
        balanceTransactionEntity.setTime(creationTime);
        balanceTransactionEntity.setValue(sumToReplenish);
        balanceTransactionEntity.setBookId(-1);
        balanceTransactionEntity.setDescription(description);
        balanceTransactionRepository.save(balanceTransactionEntity);
    }

    private BalanceTransactionDto convertBalanceTransactionEntityToDto(BalanceTransactionEntity balanceTransactionEntity) {
        String date = balanceTransactionEntity.getTime().format(customStringHandler.getFormatter());
        int bookId = balanceTransactionEntity.getBookId();
        BalanceTransactionDto balanceTransactionDto = new BalanceTransactionDto();
        balanceTransactionDto.setDate(date);
        if (bookId == -1) {
            balanceTransactionDto.setValue("+" + balanceTransactionEntity.getValue() + " р.");
        } else {
            balanceTransactionDto.setValue("-" + balanceTransactionEntity.getValue() + " р.");
        }
        balanceTransactionDto.setDescription(balanceTransactionEntity.getDescription());
        balanceTransactionDto.setBookId(bookId);
        return balanceTransactionDto;
    }
}
