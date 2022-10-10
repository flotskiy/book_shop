package com.github.flotskiy.FlotskiyBookShopApp.security;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.post.ContactConfirmPayloadDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.*;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserContactEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.enums.ContactType;
import com.github.flotskiy.FlotskiyBookShopApp.repository.UserContactRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.UserRepository;
import com.github.flotskiy.FlotskiyBookShopApp.security.jwt.JWTService;
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
    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final JWTService jwtService;

    @Autowired
    public UserRegistrationService(
            UserRepository userRepository,
            UserContactRepository userContactRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            BookstoreUserDetailsService bookstoreUserDetailsService,
            JWTService jwtService
    ) {
        this.userRepository = userRepository;
        this.userContactRepository = userContactRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.jwtService = jwtService;
    }

    public void registerNewUserWithContact(RegistrationForm registrationForm) throws InstanceAlreadyExistsException {
        UserEntity userEntity = userRepository.findUserEntityByUserContactEntity_TypeAndUserContactEntity_Contact(
                ContactType.EMAIL, registrationForm.getEmail()
        );
        if (userEntity == null) {
            UserEntity newUserEntity = registerNewUser(registrationForm);
            registerNewUserContact(registrationForm, newUserEntity);
        } else {
            throw new InstanceAlreadyExistsException("User with that email already exists");
        }
    }

    private UserEntity registerNewUser(RegistrationForm registrationForm) {
        UserEntity newUserEntity = new UserEntity();
        int newUserId = userRepository.getMaxId() + 1;
        newUserEntity.setId(newUserId);
        newUserEntity.setHash(passwordEncoder.encode(registrationForm.getPass()));
        newUserEntity.setBalance(0);
        newUserEntity.setRegTime(LocalDateTime.now());
        newUserEntity.setName(registrationForm.getName());
        userRepository.save(newUserEntity);
        return newUserEntity;
    }

    private void registerNewUserContact(RegistrationForm registrationForm, UserEntity userEntity) {
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
        String email = registrationForm.getEmail();
        if (email != null && !email.isBlank()) {
            newUserContactEntity.setType(ContactType.EMAIL);
            newUserContactEntity.setContact(email);
        } else {
            newUserContactEntity.setType(ContactType.PHONE);
            newUserContactEntity.setContact(registrationForm.getPhone());
        }
        newUserContactEntity.setApproved((short) 0);
        newUserContactEntity.setCode(RandomStringUtils.random(6, true, true));
        newUserContactEntity.setCodeTrails(0);
        newUserContactEntity.setCodeTime(LocalDateTime.now());
        userContactRepository.save(newUserContactEntity);
    }

    public ContactConfirmationResponse login(ContactConfirmPayloadDto payload) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(payload.getContact(), payload.getCode());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");
        return response;
    }

    public ContactConfirmationResponse jwtLogin(ContactConfirmPayloadDto payload) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(payload.getContact(), payload.getCode())
        );
        BookstoreUserDetails userDetails =
                (BookstoreUserDetails) bookstoreUserDetailsService.loadUserByUsername(payload.getContact());
        String jwtToken = jwtService.generateToken(userDetails);
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult(jwtToken);
        return response;
    }

    public UserDto gerCurrentUser() {
        BookstoreUserDetails userDetails =
                (BookstoreUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUserDto();
    }
}
