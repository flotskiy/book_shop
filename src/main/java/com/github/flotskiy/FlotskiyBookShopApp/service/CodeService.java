package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.exceptions.CodesNotEqualsException;
import com.github.flotskiy.FlotskiyBookShopApp.exceptions.ExpiredCodeException;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.post.ContactConfirmPayloadDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserContactEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.external.TeleIpPhoneCallResponse;
import com.github.flotskiy.FlotskiyBookShopApp.repository.UserContactRepository;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityNotFoundException;

@Service
public class CodeService {

    @Value("${signup.phone.token}")
    private String token;

    private final UserContactRepository userContactRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public CodeService(UserContactRepository userContactRepository, RestTemplate restTemplate) {
        this.userContactRepository = userContactRepository;
        this.restTemplate = restTemplate;
    }

    public String generateSecretCodeForUserContactEntityPhone(String contactPhone) {
        String formattedContactPhone = contactPhone.replaceAll("[( )-]", "");
        return generatePhoneCode(formattedContactPhone);
    }

    public Boolean verifyCode(ContactConfirmPayloadDto payload) {
        UserContactEntity userContactEntity = userContactRepository.findUserContactEntityByContact(payload.getContact());
        if (userContactEntity == null) {
            throw new EntityNotFoundException("User Contact not found during verification");
        } else if (userContactEntity.isCodeExpired()) {
            throw new ExpiredCodeException("Code was created more than 10 minutes ago");
        } else if (!userContactEntity.getCode().equals(payload.getCode())) {
            int codeTrails = userContactEntity.getCodeTrails();
            userContactEntity.setCodeTrails(codeTrails + 1);
            userContactRepository.save(userContactEntity);
            throw new CodesNotEqualsException("Invalid code");
        } else if (userContactEntity.isCodeAttemptNotExceeded()) {
            userContactEntity.setApproved((short) 1);
            int codeTrails = userContactEntity.getCodeTrails();
            userContactEntity.setCodeTrails(codeTrails + 1);
            userContactRepository.save(userContactEntity);
            return true;
        }
        return false;
    }

    private String generatePhoneCode(String phone) {
        String code = "-1";
        phone = phone.replace("+7", "8");
        String uri = "{\"success\": true, \"error\": \"\", \"data\": " +
                "{\"phone\": \"79912341234\", \"code\": \"1234\", \"id\": 2455236765}}";
//                "https://******************/***/**/authcalls/" + token + "/get_code/" + phone;
//                "https://*********/*****/******?phone=" + phone + "&ip=" + ip + "&api_id=" + api_id;

        TeleIpPhoneCallResponse teleIpPhoneCallResponse =
                new Gson().fromJson(uri, TeleIpPhoneCallResponse.class);
        // TODO: REMOVE SOUT -VVV-
        System.out.println(teleIpPhoneCallResponse.getSuccess());
        System.out.println(teleIpPhoneCallResponse.getError());
        teleIpPhoneCallResponse.getData().entrySet().forEach(stringStringEntry ->
                System.out.println(stringStringEntry.getKey() + " - " + stringStringEntry.getValue()));

        if (teleIpPhoneCallResponse.getSuccess() && teleIpPhoneCallResponse.getData().get("code") != null) {
            code = teleIpPhoneCallResponse.getData().get("code");
        }
        return code;
    }
}
