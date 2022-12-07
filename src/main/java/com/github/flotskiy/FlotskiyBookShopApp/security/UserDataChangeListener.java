package com.github.flotskiy.FlotskiyBookShopApp.security;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.UserDto;
import com.github.flotskiy.FlotskiyBookShopApp.service.RegisteredUserChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class UserDataChangeListener implements ApplicationListener<OnChangeUserDataEvent> {

    @Value("${appEmail.email}")
    private String email;

    private final RegisteredUserChangeService registeredUserChangeService;
    private final JavaMailSender mailSender;

    @Autowired
    public UserDataChangeListener(
            RegisteredUserChangeService registeredUserChangeService,
            JavaMailSender mailSender
    ) {
        this.registeredUserChangeService = registeredUserChangeService;
        this.mailSender = mailSender;
    }

    @Override
    public void onApplicationEvent(OnChangeUserDataEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnChangeUserDataEvent event) {
        UserDto userDto = event.getUserDto();
        String contact = userDto.getContact();
        String token = UUID.randomUUID().toString();
        registeredUserChangeService.createVerificationToken(event.getPayload(), userDto, token);

        String messageText = "Follow this link to complete changing data: ";
        String link = event.getUrl() + "/confirmDataChange?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(email);
        message.setTo(contact);
        message.setSubject("User data change request");
        message.setText(messageText + link);
        message.setSentDate(new Date());
        mailSender.send(message);
    }
}
