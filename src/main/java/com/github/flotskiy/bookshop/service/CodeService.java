package com.github.flotskiy.bookshop.service;

import com.github.flotskiy.bookshop.exceptions.CodesNotEqualsException;
import com.github.flotskiy.bookshop.exceptions.ExpiredCodeException;
import com.github.flotskiy.bookshop.model.dto.post.ContactConfirmPayloadDto;
import com.github.flotskiy.bookshop.model.entity.user.UserContactEntity;
import com.github.flotskiy.bookshop.model.external.TeleIpPhoneCallResponse;
import com.github.flotskiy.bookshop.repository.UserContactRepository;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Date;

@Service
public class CodeService {

    @Value("${signup.phone.token}")
    private String token;

    @Value("${appEmail.email}")
    private String email;

    private final UserContactRepository userContactRepository;
    private final JavaMailSender javaMailSender;

    @Autowired
    public CodeService(UserContactRepository userContactRepository, JavaMailSender javaMailSender) {
        this.userContactRepository = userContactRepository;
        this.javaMailSender = javaMailSender;
    }

    public String generateSecretCodeForUserContactEntityPhone(String contactPhone) {
        String formattedContactPhone = contactPhone.replaceAll("[( )-]", "");
        return generatePhoneCode(formattedContactPhone);
    }

    public Boolean verifyCode(ContactConfirmPayloadDto payload) {
        UserContactEntity userContactEntity = userContactRepository.findUserContactEntityByContact(payload.getContact());
        if (userContactEntity == null) {
            throw new EntityNotFoundException("User Contact not found during verification");
        } else if (Boolean.TRUE.equals(userContactEntity.isCodeExpired())) {
            throw new ExpiredCodeException("Code was created more than 10 minutes ago");
        } else if (!userContactEntity.getCode().equals(payload.getCode())) {
            int codeTrails = userContactEntity.getCodeTrails();
            userContactEntity.setCodeTrails(codeTrails + 1);
            userContactRepository.save(userContactEntity);
            throw new CodesNotEqualsException("Invalid code");
        } else if (Boolean.TRUE.equals(userContactEntity.isCodeAttemptNotExceeded())) {
            userContactEntity.setApproved((short) 1);
            int codeTrails = userContactEntity.getCodeTrails();
            userContactEntity.setCodeTrails(codeTrails + 1);
            userContactRepository.save(userContactEntity);
            return true;
        }
        return false;
    }

    public void sendEmailConfirmationCode(String contact, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(email);
        message.setTo(contact);
        message.setSubject("FlotskiyBookShop email verification!");
        message.setText("Verification code is: " + code);
        message.setSentDate(new Date());
        javaMailSender.send(message);
    }

    public String generatePhoneCode(String phone) {
        String code = "-1";
        phone = phone.replace("+7", "8");
        String uri = "{\"success\": true, \"error\": \"\", \"data\": " +
                "{\"phone\": \"79912341234\", \"code\": \"1234\", \"id\": 2455236765}}";
//                "https://******************/***/**/authcalls/" + token + "/get_code/" + phone;
//                "https://*********/*****/******?phone=" + phone + "&ip=" + ip + "&api_id=" + api_id;

        TeleIpPhoneCallResponse teleIpPhoneCallResponse = new Gson().fromJson(uri, TeleIpPhoneCallResponse.class);
        if (Boolean.TRUE.equals(teleIpPhoneCallResponse.getSuccess()) &&
                teleIpPhoneCallResponse.getData().get("code") != null) {
            code = teleIpPhoneCallResponse.getData().get("code");
        }
        return code;
    }
}
