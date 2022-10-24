package com.github.flotskiy.FlotskiyBookShopApp.security;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserContactEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.enums.ContactType;
import com.github.flotskiy.FlotskiyBookShopApp.repository.UserContactRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.UserRepository;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserRegistrationServiceTests {

    @InjectMocks
    private final UserRegistrationService userRegistrationService;
    private final PasswordEncoder passwordEncoder;
    private RegistrationForm registrationForm;

    @MockBean
    private UserRepository userRepositoryMock;

    @MockBean
    private UserContactRepository userContactRepositoryMock;

    @Autowired
    public UserRegistrationServiceTests(UserRegistrationService userRegistrationService, PasswordEncoder passwordEncoder) {
        this.userRegistrationService = userRegistrationService;
        this.passwordEncoder = passwordEncoder;
    }

    @BeforeEach
    void setUp() {
        registrationForm = new RegistrationForm();
        registrationForm.setEmail("test@test.test");
        registrationForm.setName("Name Test");
        registrationForm.setPass("testpass");
    }

    @AfterEach
    void tearDown() {
        registrationForm = null;
    }

    @Test
    void registerNewUserWithContact() {
        UserEntity userEntity = new UserEntity();
        userEntity.setName(registrationForm.getName());
        userEntity.setHash(passwordEncoder.encode(registrationForm.getPass()));

        when(userRepositoryMock.save(Mockito.any(UserEntity.class))).thenReturn(userEntity);
        UserEntity returnedUser = userRegistrationService.registerNewUser(registrationForm);

        assertNotNull(returnedUser);
        assertTrue(passwordEncoder.matches(registrationForm.getPass(), returnedUser.getHash()));
        assertTrue(CoreMatchers.is(returnedUser.getName()).matches(registrationForm.getName()));

        Mockito.verify(userRepositoryMock, Mockito.times(1)).save(Mockito.any(UserEntity.class));

        UserContactEntity userContactEntity = new UserContactEntity();
        userContactEntity.setType(ContactType.EMAIL);
        userContactEntity.setContact(registrationForm.getEmail());
        userContactEntity.setUserEntity(returnedUser);

        when(userContactRepositoryMock.save(Mockito.any(UserContactEntity.class))).thenReturn(userContactEntity);
        UserContactEntity returnedUserContact = userRegistrationService.registerNewUserContact(registrationForm, returnedUser);

        assertNotNull(returnedUserContact);
        assertTrue(CoreMatchers.is(returnedUserContact.getType()).matches(ContactType.EMAIL));
        assertTrue(CoreMatchers.is(returnedUserContact.getUserEntity()).matches(returnedUser));
        assertTrue(CoreMatchers.is(returnedUserContact.getContact()).matches(registrationForm.getEmail()));
    }
}
