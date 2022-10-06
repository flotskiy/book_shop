package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.*;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserContactEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.enums.ContactType;
import com.github.flotskiy.FlotskiyBookShopApp.repository.UserContactRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import java.time.LocalDateTime;

@Service
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final UserContactRepository userContactRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserRegistrationService(
            UserRepository userRepository,
            UserContactRepository userContactRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.userContactRepository = userContactRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public void registerNewUserWithContact(RegistrationFormDto registrationFormDto) throws InstanceAlreadyExistsException {
        UserEntity userEntity = userRepository.findUserEntityByUserContactEntity_TypeAndUserContactEntity_Contact(
                ContactType.EMAIL, registrationFormDto.getEmail()
        );
        if (userEntity == null) {
            UserEntity newUserEntity = registerNewUser(registrationFormDto);
            registerNewUserContact(registrationFormDto, newUserEntity);
        } else {
            throw new InstanceAlreadyExistsException("User with that email already exists");
        }
    }

    private UserEntity registerNewUser(RegistrationFormDto registrationFormDto) {
        UserEntity newUserEntity = new UserEntity();
        int newUserId = userRepository.getMaxId() + 1;
        newUserEntity.setId(newUserId);
        newUserEntity.setHash(passwordEncoder.encode(registrationFormDto.getPass()));
        newUserEntity.setBalance(0);
        newUserEntity.setRegTime(LocalDateTime.now());
        newUserEntity.setName(registrationFormDto.getName());
        userRepository.save(newUserEntity);
        return newUserEntity;
    }

    private void registerNewUserContact(RegistrationFormDto registrationFormDto, UserEntity userEntity) {
        UserContactEntity newUserContactEntity = new UserContactEntity();
        Integer currentMaxId = userContactRepository.getMaxId();
        int newUserContactId = 0;
        if (currentMaxId == null) {
            newUserContactId = 1;
        } else {
            newUserContactId =  currentMaxId + 1;
        }
        newUserContactEntity.setId(newUserContactId);
        newUserContactEntity.setUserEntity(userEntity);
        String email = registrationFormDto.getEmail();
        if (email != null && !email.isBlank()) {
            newUserContactEntity.setType(ContactType.EMAIL);
            newUserContactEntity.setContact(email);
        } else {
            newUserContactEntity.setType(ContactType.PHONE);
            newUserContactEntity.setContact(registrationFormDto.getPhone());
        }
        newUserContactEntity.setApproved((short) 0);
        newUserContactEntity.setCode(RandomStringUtils.random(6, true, true));
        newUserContactEntity.setCodeTrails(0);
        newUserContactEntity.setCodeTime(LocalDateTime.now());
        userContactRepository.save(newUserContactEntity);
    }

    public ContactConfirmationResponseDto login(ContactConfirmationPayload payload) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(payload.getContact(), payload.getCode());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ContactConfirmationResponseDto response = new ContactConfirmationResponseDto();
        response.setResult(true);
        return response;
    }

    public UserDto gerCurrentUser() {
        BookstoreUserDetails userDetails =
                (BookstoreUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUserDto();
    }
}
