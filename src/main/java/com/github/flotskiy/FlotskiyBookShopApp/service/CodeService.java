package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserContactEntity;
import com.github.flotskiy.FlotskiyBookShopApp.repository.UserContactRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import liquibase.util.StringUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CodeService {

    @Value("${twilio.ACCOUNT_SID}")
    private String account_sid;

    @Value("${twilio.AUTH_TOKEN}")
    private String auth_token;

    @Value("${twilio.TWILIO_NUMBER}")
    private String twilio_number;

    private final UserContactRepository userContactRepository;

    @Autowired
    public CodeService(UserContactRepository userContactRepository) {
        this.userContactRepository = userContactRepository;
    }

    public String generateSecretCodeForUserContactEntity(String contactPhone) {
        Twilio.init(account_sid, auth_token);
        String formattedContactPhone = contactPhone.replaceAll("[( )-]", "");
        String generatedCode = generateCode();
        Message.creator(
                new PhoneNumber(formattedContactPhone),
                new PhoneNumber(twilio_number),
                "Your secret code is: " + generatedCode
        ).create();
        return generatedCode;
    }

    public Boolean verifyCode(String code) {
        UserContactEntity userContactEntity = userContactRepository.findUserContactEntityByCode(code);
        return (userContactEntity != null && userContactEntity.isCodeValid());
    }

    private String generateCode() {
        String result = RandomStringUtils.random(6, false, true);
        result = result.substring(0, 3) + " " + result.substring(3);
        return result;
    }
}
