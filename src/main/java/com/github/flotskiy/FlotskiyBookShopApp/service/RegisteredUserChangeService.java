package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.exceptions.PasswordChangeException;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.post.ChangeUserDataConfirmPayload;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.UserDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.VerificationTokenDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserContactEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.VerificationTokenEntity;
import com.github.flotskiy.FlotskiyBookShopApp.repository.UserContactRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.UserRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.VerificationTokenRepository;
import com.github.flotskiy.FlotskiyBookShopApp.security.BookstoreUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@PropertySource("application-variables.properties")
public class RegisteredUserChangeService {

    @Value("${expiration.flashcall}")
    private int flashCallExpireMinutes;

    @Value("${expiration.email}")
    private int emailTokenExpireMinutes;

    private final UserRepository userRepository;
    private final UserContactRepository userContactRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final CodeService codeService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegisteredUserChangeService(
            UserRepository userRepository,
            UserContactRepository userContactRepository,
            VerificationTokenRepository verificationTokenRepository,
            BookstoreUserDetailsService bookstoreUserDetailsService,
            CodeService codeService,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userContactRepository = userContactRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.codeService = codeService;
        this.passwordEncoder = passwordEncoder;
    }

    public String changeUserData(ChangeUserDataConfirmPayload updatedUser, UserDto currentUser) {
        UserEntity currentUserEntity = userRepository.findUserEntityByUserContactEntity_Contact(currentUser.getContact());
        if (currentUserEntity == null) {
            throw new EntityNotFoundException("UserEntity not found during changing user's data");
        }
        String updatedContact = "";
        if (currentUser.getContact().contains("@")) {
            updatedContact = updatedUser.getMail();
        } else {
            updatedContact = updatedUser.getPhone();
        }
        if (updatedUser.getName().equals(currentUser.getName()) && updatedContact.equals(currentUser.getContact())) {
            try {
                changeUserPassword(updatedUser, currentUserEntity);
                return "pass";
            } catch (PasswordChangeException passwordChangeException) {
                return "passfail";
            }
        } else {
            if (isUserDataChangePossible(updatedUser, updatedContact, currentUserEntity)) {
                if (updatedContact.contains("@")) {
                    return "mail";
                } else {
                    return "phone";
                }
            } else {
                return "userfail";
            }
        }
    }

    private void changeUserPassword(ChangeUserDataConfirmPayload updatedUser, UserEntity currentUser) {
        if (isUserPasswordChangePossible(updatedUser.getPassword(), updatedUser.getPasswordReply())) {
            currentUser.setHash(passwordEncoder.encode(updatedUser.getPassword()));
            userRepository.save(currentUser);
        } else {
            throw new PasswordChangeException("Password reply differs from password or password is empty");
        }
    }

    @Transactional
    public void changeAllUserData(VerificationTokenDto verificationTokenDto) {
        int currentUserId = bookstoreUserDetailsService.gerCurrentUserId();
        UserEntity currentUser = userRepository.findById(currentUserId).get();
        currentUser.setHash(verificationTokenDto.getHash());
        currentUser.setName(verificationTokenDto.getName());
        userRepository.save(currentUser);

        UserContactEntity userContactEntity = userContactRepository.findUserContactEntityByUserEntityId(currentUser.getId());
        userContactEntity.setContact(verificationTokenDto.getContact());
        userContactRepository.save(userContactEntity);
    }

    private boolean isUserDataChangePossible(ChangeUserDataConfirmPayload updatedUser, String newContact, UserEntity userEntity) {
        return (isUserPasswordChangePossible(updatedUser.getPassword(), updatedUser.getPasswordReply())
                && isUserNameOrContactChangePossible(updatedUser, newContact, userEntity));
    }

    private boolean isUserPasswordChangePossible(String password, String passwordReply) {
        return !password.isBlank() && password.equals(passwordReply);
    }

    private boolean isUserNameOrContactChangePossible(
            ChangeUserDataConfirmPayload updatedUser, String newContact, UserEntity userEntity
    ) {
        String oldContact = userEntity.getUserContactEntity().getContact();
        return  ((!updatedUser.getName().isBlank() && !newContact.isBlank())
                &&
                (!updatedUser.getName().equals(userEntity.getName()) || !newContact.equals(oldContact)));
    }

    public void createVerificationToken(ChangeUserDataConfirmPayload payload, UserDto userDto, String token) {
        int tokenLifeLongInMinutes = emailTokenExpireMinutes;
        UserEntity userEntity = userRepository.findById(userDto.getId()).get();
        VerificationTokenEntity verificationTokenEntity =
                createVerificationTokenEntity(payload, userEntity, tokenLifeLongInMinutes);
        verificationTokenEntity.setToken(token);
        verificationTokenEntity.setContact(payload.getMail());
        verificationTokenRepository.save(verificationTokenEntity);
    }

    public void handleDataForPhoneUser(ChangeUserDataConfirmPayload payload, UserDto userDto) {
        int codeLifeLongInMinutes = flashCallExpireMinutes;
        String phoneCode = codeService.generatePhoneCode(userDto.getContact());
        UserEntity userEntity = userRepository.findById(userDto.getId()).get();
        VerificationTokenEntity verificationTokenEntity =
                createVerificationTokenEntity(payload, userEntity, codeLifeLongInMinutes);
        verificationTokenEntity.setToken(phoneCode);
        verificationTokenEntity.setContact(payload.getPhone());
        verificationTokenRepository.save(verificationTokenEntity);
    }

    private VerificationTokenEntity createVerificationTokenEntity(
            ChangeUserDataConfirmPayload payload, UserEntity userEntity, int lifeLong
    ) {
        VerificationTokenEntity verificationTokenEntity = new VerificationTokenEntity();
        verificationTokenEntity.setUser(userEntity);
        verificationTokenEntity.setExpiryTime(LocalDateTime.now().plus(lifeLong, ChronoUnit.MINUTES));
        verificationTokenEntity.setName(payload.getName());
        verificationTokenEntity.setHash(passwordEncoder.encode(payload.getPassword()));
        return verificationTokenEntity;
    }

    public VerificationTokenDto getVerificationTokenDto(String token) {
        VerificationTokenEntity verificationTokenEntity =
                verificationTokenRepository.findVerificationTokenEntityByToken(token);
        LocalDateTime now = LocalDateTime.now();
        VerificationTokenDto verificationTokenDto = new VerificationTokenDto();
        if (verificationTokenEntity != null && now.isBefore(verificationTokenEntity.getExpiryTime())) {
            verificationTokenDto.setName(verificationTokenEntity.getName());
            verificationTokenDto.setContact(verificationTokenEntity.getContact());
            verificationTokenDto.setHash(verificationTokenEntity.getHash());
        } else {
            verificationTokenDto.setValid(false);
        }
        if (verificationTokenEntity != null) {
            verificationTokenRepository.delete(verificationTokenEntity);
        }
        return verificationTokenDto;
    }
}
