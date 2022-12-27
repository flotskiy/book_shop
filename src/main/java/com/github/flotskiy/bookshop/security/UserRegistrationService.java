package com.github.flotskiy.bookshop.security;

import com.github.flotskiy.bookshop.exceptions.UserChangeException;
import com.github.flotskiy.bookshop.exceptions.UserContactEntityNotApproved;
import com.github.flotskiy.bookshop.model.dto.post.ContactConfirmPayloadDto;
import com.github.flotskiy.bookshop.model.dto.user.*;
import com.github.flotskiy.bookshop.model.entity.user.UserContactEntity;
import com.github.flotskiy.bookshop.model.entity.user.UserEntity;
import com.github.flotskiy.bookshop.model.enums.ContactType;
import com.github.flotskiy.bookshop.repository.UserContactRepository;
import com.github.flotskiy.bookshop.repository.UserRepository;
import com.github.flotskiy.bookshop.security.jwt.JWTService;
import com.github.flotskiy.bookshop.service.CodeService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
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
import org.springframework.web.context.request.RequestContextHolder;

import javax.management.InstanceAlreadyExistsException;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final UserContactRepository userContactRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final JWTService jwtService;
    private final CodeService codeService;
    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Autowired
    public UserRegistrationService(
            UserRepository userRepository,
            UserContactRepository userContactRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            BookstoreUserDetailsService bookstoreUserDetailsService,
            JWTService jwtService,
            CodeService codeService
    ) {
        this.userRepository = userRepository;
        this.userContactRepository = userContactRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.jwtService = jwtService;
        this.codeService = codeService;
    }

    public UserEntity getCurrentUserEntity(Integer currentUserId) {
        Optional<UserEntity> userEntityOptional = userRepository.findById(currentUserId);
        if (userEntityOptional.isEmpty()) {
            throw new EntityNotFoundException("UserEntity not found");
        }
        return userEntityOptional.get();
    }

    public void registerOrUpdateNewUserWithContactWhileRequestingContactConfirmation(String contact, String codeString) {
        UserEntity userEntity = userRepository.findUserEntityByUserContactEntity_Contact(contact);
        if (userEntity == null) {
            userEntity = registerNewUserWhileRequestingContactConfirmation();
            registerNewUserContactWhileRequestingContactConfirmation(contact, codeString, userEntity);
        } else {
            UserContactEntity userContactEntity = userContactRepository.findUserContactEntityByContact(contact);
            if (userContactEntity.getApproved() == 0) {
                userContactEntity.setCode(codeString);
                userContactEntity.setCodeTrails(0);
                userContactEntity.setCodeTime(LocalDateTime.now());
                userContactRepository.save(userContactEntity);
            } else {
                throw new UserChangeException("Re-confirmation prohibited");
            }
        }
    }

    public int handleGuestSession(String guestSession) {
        UserEntity guest = userRepository.findUserEntityByUserContactEntity_Contact(guestSession);
        if (guest == null) {
            guest = registerGuestUser();
            registerGuestUserContact(guest, guestSession);
        }
        return guest.getId();
    }

    // TODO: METHOD IS USING IN TEST ONLY - DEL OR REFACTOR
    public UserEntity registerNewUserWithContact(RegistrationForm registrationForm) throws InstanceAlreadyExistsException {
        UserEntity userEntity = null;
        if (registrationForm.getEmail() != null) {
            userEntity = userRepository.findUserEntityByUserContactEntity_Contact(registrationForm.getEmail());
        } else if (registrationForm.getPhone() != null) {
            userEntity = userRepository.findUserEntityByUserContactEntity_Contact(registrationForm.getPhone());
        }
        if (userEntity == null) {
            userEntity = registerNewUser(registrationForm);
            registerNewUserContact(registrationForm, userEntity);
        } else {
            throw new InstanceAlreadyExistsException("User with that email already exists");
        }
        return userEntity;
    }

    private UserEntity registerNewUserWhileRequestingContactConfirmation() {
        UserEntity userEntity = registerNewUserEntity();
        userEntity.setName("default");
        return userRepository.save(userEntity);
    }

    private UserEntity registerGuestUser() {
        UserEntity userEntity = registerNewUserEntity();
        userEntity.setName("guest-default");
        return userRepository.save(userEntity);
    }

    private UserEntity registerNewUserEntity() {
        Integer currentMaxId = userRepository.getMaxId();
        int newUserId = 1;
        if (currentMaxId != null) {
            newUserId = currentMaxId + 1;
        }
        UserEntity newUserEntity = new UserEntity();
        newUserEntity.setId(newUserId);
        newUserEntity.setHash("");
        newUserEntity.setBalance(0);
        newUserEntity.setRegTime(LocalDateTime.now());
        return newUserEntity;
    }

    // TODO: used for google registration, check workflow
    public UserEntity registerNewUser(RegistrationForm registrationForm) {
        Integer currentMaxId = userRepository.getMaxId();
        int newUserId = 1;
        if (currentMaxId != null) {
            newUserId = currentMaxId + 1;
        }

        UserEntity newUserEntity = new UserEntity();
        newUserEntity.setId(newUserId);
        if (registrationForm.getPass().isBlank()) {
            newUserEntity.setHash("");
        } else {
            newUserEntity.setHash(passwordEncoder.encode(registrationForm.getPass()));
        }
        newUserEntity.setBalance(0);
        newUserEntity.setRegTime(LocalDateTime.now());
        newUserEntity.setName(registrationForm.getName());
        return userRepository.save(newUserEntity);
    }

    private void registerNewUserContactWhileRequestingContactConfirmation(
            String contact, String codeString, UserEntity userEntity
    ) {
        UserContactEntity newUserContactEntity = getNewUserContactEntity(userEntity);
        if (contact.contains("@")) {
            newUserContactEntity.setType(ContactType.EMAIL);
            newUserContactEntity.setContact(contact);
        } else {
            newUserContactEntity.setType(ContactType.PHONE);
            newUserContactEntity.setContact(contact);
        }
        newUserContactEntity.setCode(codeString);
        newUserContactEntity.setCodeTrails(0);
        newUserContactEntity.setCodeTime(LocalDateTime.now());
        userContactRepository.save(newUserContactEntity);
    }

    private void registerGuestUserContact(UserEntity guestEntity, String guestSession) {
        UserContactEntity guestUserContactEntity = getNewUserContactEntity(guestEntity);
        guestUserContactEntity.setType(ContactType.GUEST);
        guestUserContactEntity.setContact(guestSession);
        userContactRepository.save(guestUserContactEntity);
    }

    private UserContactEntity getNewUserContactEntity(UserEntity userEntity) {
        Integer currentMaxId = userContactRepository.getMaxId();
        int newUserContactId = 1;
        if (currentMaxId != null) {
            newUserContactId = currentMaxId + 1;
        }
        UserContactEntity userContactEntity = new UserContactEntity();
        userContactEntity.setId(newUserContactId);
        userContactEntity.setUserEntity(userEntity);
        userContactEntity.setApproved((short) 0);
        return userContactEntity;
    }

    public String registerUserPassword(RegistrationForm registrationForm) {
        String contact = null;
        if (registrationForm.getPhone() != null) {
            contact = registrationForm.getPhone();
        } else if (registrationForm.getEmail() != null) {
            contact = registrationForm.getEmail();
        }
        UserEntity userEntity = userRepository.findUserEntityByUserContactEntity_Contact(contact);
        if (userEntity == null) {
            throw new EntityNotFoundException("UserEntity not found during registering user password");
        }
        UserContactEntity userContactEntity = userContactRepository.findUserContactEntityByUserEntityId(userEntity.getId());
        if (userContactEntity.getApproved() == 1) {
            userEntity.setName(registrationForm.getName());
            userEntity.setHash(passwordEncoder.encode(registrationForm.getPass()));
            userRepository.save(userEntity);
        } else {
            throw new UserContactEntityNotApproved("You have to approve contact to save the password");
        }
        return contact;
    }

    // TODO: check this method usage
    public UserContactEntity registerNewUserContact(RegistrationForm registrationForm, UserEntity userEntity) {
        UserContactEntity newUserContactEntity = getNewUserContactEntity(userEntity);
        String email = registrationForm.getEmail();
        String phone = registrationForm.getPhone();
        if (email != null && !email.isBlank()) {
            newUserContactEntity.setType(ContactType.EMAIL);
            newUserContactEntity.setContact(email);
            newUserContactEntity.setCode(RandomStringUtils.random(6, true, true));
        } else if (phone != null && !phone.isBlank()) {
            newUserContactEntity.setType(ContactType.PHONE);
            newUserContactEntity.setContact(phone);
            newUserContactEntity.setCode(codeService.generateSecretCodeForUserContactEntityPhone(phone));
        }
        newUserContactEntity.setCodeTrails(0);
        newUserContactEntity.setCodeTime(LocalDateTime.now());
        return userContactRepository.save(newUserContactEntity);
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
        BookstoreUserDetails userDetails;
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

    public Integer getCurrentUserIdIncludingGuest() {
        String guestSession = RequestContextHolder.currentRequestAttributes().getSessionId();
        if (isGuestKnown(guestSession)) {
            return userRepository.findUserEntityByUserContactEntity_Contact(guestSession).getId();
        }
        return bookstoreUserDetailsService.gerCurrentAuthenticatedUserId();
    }

    public UserDto getCurrentUserDto() {
        int userId = bookstoreUserDetailsService.gerCurrentAuthenticatedUserId();
        if (userId < 1) {
            return new UserDto();
        }
        return bookstoreUserDetailsService.getUserDtoById(userId);
    }

    public boolean isGuestKnown(String session) {
        UserEntity guestUserEntity = userRepository.findUserEntityByUserContactEntity_Contact(session);
        return guestUserEntity != null;
    }

    public UserDto getCurrentGuestUserDto(String session) {
        UserEntity userEntity = userRepository.findUserEntityByUserContactEntity_Contact(session);
        int userId = userEntity.getId();

        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setUserBooksData(bookstoreUserDetailsService.createUserBooksData(userId));
        return userDto;
    }

    public Integer getGuestUserId(String session) {
        return userRepository.findUserEntityByUserContactEntity_Contact(session).getId();
    }

    public String getExceptionInfo(Exception exception) {
        if (exception instanceof JwtException) {
            return "Access denied! Try to sign in again!";
        } else if (exception instanceof BadCredentialsException) {
            return "Wrong password!";
        } else if (exception instanceof AuthenticationException) {
            return "Error during authentication occurred!";
        } else {
            return "Something went wrong!";
        }
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken);
    }
}
