package com.github.flotskiy.bookshop.service;

import com.github.flotskiy.bookshop.exceptions.*;
import com.github.flotskiy.bookshop.model.dto.post.ContactConfirmPayloadDto;
import com.github.flotskiy.bookshop.model.entity.user.UserContactEntity;
import com.github.flotskiy.bookshop.model.external.TeleIpPhoneCallResponse;
import com.github.flotskiy.bookshop.repository.UserContactRepository;
import com.google.gson.Gson;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class CodeService {

    @Value("${signup.phone.token}")
    private String token;
    @Value("${appEmail.email}")
    private String bookshopEmail;
    @Value("${code.try.limit}")
    private int codeTryLimit;
    @Value("${code.lifelong}")
    private int codeLifelong;
    @Value("${code.timeout}")
    private int codeTimeoutLong;
    @Value("${code.email.length}")
    private int emailCodeLength;

    private final UserContactRepository userContactRepository;
    private final JavaMailSender javaMailSender;

    @Autowired
    public CodeService(UserContactRepository userContactRepository, JavaMailSender javaMailSender) {
        this.userContactRepository = userContactRepository;
        this.javaMailSender = javaMailSender;
    }

    public String getConfirmationCode(String contact) {
        UserContactEntity userContactEntity = userContactRepository.findUserContactEntityByContact(contact);
        if (userContactEntity != null) {
            LocalDateTime codeTime = userContactEntity.getCodeTime();
            int codeTrails = userContactEntity.getCodeTrails();
            if (isCodeAlive(codeTime) && !isLimitExceeded(codeTrails)) {
                repeatedRequestForConfirmationCode(codeTime, contact, userContactEntity.getCode());
            } else if (isLimitExceeded(codeTrails) && !isRepeatRequestPossible(codeTime)) {
                throw new AttemptsNumberExceededException(getTimeoutRemains(codeTime));
            }
        }
        return generateCodeAndSendToExternalService(contact);
    }

    public String generateSecretCodeForUserContactEntityPhone(String contactPhone) {
        String formattedContactPhone = contactPhone.replaceAll("[( )-]", "");
        return generatePhoneCode(formattedContactPhone);
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

    public boolean isContactAlreadyRegisteredAndApproved(String userContact) {
        UserContactEntity userContactEntity = userContactRepository.findUserContactEntityByContact(userContact);
        return (userContactEntity != null && userContactEntity.getApproved() == 1);
    }

    public void verifyCode(ContactConfirmPayloadDto payload) {
        UserContactEntity userContactEntity = userContactRepository.findUserContactEntityByContact(payload.getContact());
        if (userContactEntity == null) {
            throw new EntityNotFoundException("User Contact not found during verification");
        } else if (userContactEntity.getApproved() == 1) {
            throw new UserChangeException("Re-confirmation prohibited");
        } else if (!isCodeAlive(userContactEntity.getCodeTime())) {
            throw new ExpiredCodeException("The confirmation code is outdated. Request a new one");
        } else if (!userContactEntity.getCode().equals(payload.getCode())) {
            int codeTrails = userContactEntity.getCodeTrails();
            userContactEntity.setCodeTrails(codeTrails + 1);
            userContactRepository.save(userContactEntity);
            throw new CodesNotEqualsException("Invalid code");
        } else if (isLimitExceeded(userContactEntity.getCodeTrails())) {
            throw new CodeTrailsLimitExceededException("Number of attempts to enter code exceeded");
        }
        userContactEntity.setApproved((short) 1);
        int codeTrails = userContactEntity.getCodeTrails();
        userContactEntity.setCodeTrails(codeTrails + 1);
        userContactRepository.save(userContactEntity);
    }

    private String generateCodeAndSendToExternalService(String contactInfo) {
        String codeGenerated;
        if (contactInfo.contains("@")) {
            codeGenerated = generateCodeForEmail();
            sendEmailConfirmationCode(contactInfo, codeGenerated);
        } else {
            codeGenerated = generateSecretCodeForUserContactEntityPhone(contactInfo);
        }
        return codeGenerated;
    }

    private void repeatedRequestForConfirmationCode(LocalDateTime localDateTime, String contactToConfirm, String code) {
        if (isRepeatRequestPossible(localDateTime)) {
            if (contactToConfirm.contains("@")) {
                sendEmailConfirmationCode(contactToConfirm, code);
                throw new ConfirmationException("No new code generated");
            } else {
                throw new ConfirmationException("Repeat call is not possible");
            }
        } else {
            throw new TimeoutMinutesRemainsConfirmationException(getTimeoutRemains(localDateTime));
        }
    }

    private String generateCodeForEmail() {
        String code = RandomStringUtils.random(emailCodeLength, false, true);
        return code.substring(0, emailCodeLength/2) + " " + code.substring(emailCodeLength/2, emailCodeLength);
    }

    private void sendEmailConfirmationCode(String contactEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(bookshopEmail);
        message.setTo(contactEmail);
        message.setSubject("FlotskiyBookShop email verification!");
        message.setText("Verification code is: " + code);
        message.setSentDate(new Date());
        javaMailSender.send(message);
    }

    private boolean isLimitExceeded(int currentCountValue) {
        return currentCountValue >= codeTryLimit;
    }

    private boolean isCodeAlive(LocalDateTime creationTime) {
        return LocalDateTime.now().isBefore(creationTime.plus(codeLifelong, ChronoUnit.MINUTES));
    }

    private boolean isRepeatRequestPossible(LocalDateTime creationTime) {
        return LocalDateTime.now().isAfter(creationTime.plus(codeTimeoutLong, ChronoUnit.MINUTES));
    }

    private String getTimeoutRemains(LocalDateTime creationTime) {
        Duration duration = Duration.between(LocalDateTime.now(), creationTime.plus(codeTimeoutLong, ChronoUnit.MINUTES));
        return String.valueOf(Math.abs(duration.toSeconds()));
    }
}
