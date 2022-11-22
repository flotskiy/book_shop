package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class PaymentService {

    @Value("${robokassa.merchant.login}")
    private String merchantLogin;

    @Value("${robokassa.pass.first.test}")
    private String firstPass;

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
}
