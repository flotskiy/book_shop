package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.exceptions.UserBalanceNotEnoughException;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.UserDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.links.Book2UserEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.payments.BalanceTransactionEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserEntity;
import com.github.flotskiy.FlotskiyBookShopApp.repository.BalanceTransactionRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.Book2UserRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
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

    @Autowired
    public PaymentService(
            UserRepository userRepository,
            Book2UserRepository book2UserRepository,
            BalanceTransactionRepository balanceTransactionRepository
    ) {
        this.userRepository = userRepository;
        this.book2UserRepository = book2UserRepository;
        this.balanceTransactionRepository = balanceTransactionRepository;
    }

    public String getPayment(List<BookDto> cartBooks) throws NoSuchAlgorithmException {
        int paymentTotal = cartBooks.stream().mapToInt(BookDto::getDiscountPrice).sum();
        MessageDigest md = MessageDigest.getInstance("MD5");
        String invId = "1";
        md.update((merchantLogin + ":" + paymentTotal + ":" + invId + ":" + firstPass).getBytes());
        return "https://auth.robokassa.ru/Merchant/Index.aspx" +
                "?MerchantLogin=" + merchantLogin +
                "&InvId=" + invId +
                "&Culture=ru" +
                "&Encoding=utf-8" +
                "&OutSum=" + paymentTotal +
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

            int bookPrice = bookDto.getDiscountPrice();
            String description =
                    "Buying a book&#32;<a href=\"/books/" + bookDto.getSlug() + "\">" + bookDto.getTitle() + "</a>";
            BalanceTransactionEntity balanceTransactionEntity = new BalanceTransactionEntity();
            balanceTransactionEntity.setUserId(currentUserId);
            balanceTransactionEntity.setTime(LocalDateTime.now());
            balanceTransactionEntity.setValue(bookPrice);
            balanceTransactionEntity.setBookId(bookId);
            balanceTransactionEntity.setDescription(description);
            balanceTransactionRepository.save(balanceTransactionEntity);
        }
    }
}
