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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final UserContactRepository userContactRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final JWTService jwtService;
    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

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
        if (registrationForm.getPass().isBlank()) {
            newUserEntity.setHash("");
        } else {
            newUserEntity.setHash(passwordEncoder.encode(registrationForm.getPass()));
        }
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
        BookstoreUserDetails userDetails = bookstoreUserDetailsService.loadUserByUsername(payload.getContact());
        String jwtToken = jwtService.generateToken(userDetails);
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult(jwtToken);
        return response;
    }

    public UserDto gerCurrentUser() {
        Object userObject = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BookstoreUserDetails userDetails = null;
        if (userObject instanceof BookstoreUserDetails) {
            userDetails = (BookstoreUserDetails) userObject;
        } else {
            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) userObject;
            Map<String, Object> userAttr = oAuth2User.getAttributes();
            String userEmail = userAttr.get("email").toString();
            try {
                userDetails = bookstoreUserDetailsService.loadUserByUsername(userEmail);
                logger.info("Google user found in database");
            } catch (UsernameNotFoundException usernameNotFoundException) {
                logger.info("New google user creation");
                RegistrationForm googleRegistrationForm = new RegistrationForm();
                String userName = userAttr.get("name").toString();
                googleRegistrationForm.setName(userName);
                googleRegistrationForm.setEmail(userEmail);
                googleRegistrationForm.setPass("");
                UserEntity newUserEntity = registerNewUser(googleRegistrationForm);
                registerNewUserContact(googleRegistrationForm, newUserEntity);
                userDetails = bookstoreUserDetailsService.loadUserByUsername(userEmail);
            }
        }
        return userDetails.getUserDto();
    }

    public Integer getCurrentUserId() {
        return gerCurrentUser().getId();
    }

    public String getExceptionInfo(Exception exception) {
        try {
            throw exception;
        } catch (JwtException jwtException) {
            logger.warning(jwtException.getLocalizedMessage());
            return "Access denied! Try to sign in again!";
        } catch (BadCredentialsException bce) {
            logger.warning(bce.getLocalizedMessage());
            return "Wrong user name and/or password!";
        } catch (AuthenticationException ae) {
            logger.warning(ae.getLocalizedMessage());
            return "Error during authentication occurred!";
        } catch (Exception ex) {
            logger.warning(ex.getLocalizedMessage());
            return "Something went wrong!";
        }
    }
}
