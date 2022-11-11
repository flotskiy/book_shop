package com.github.flotskiy.FlotskiyBookShopApp.security;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.post.ContactConfirmPayloadDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserContactEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.enums.ContactType;
import com.github.flotskiy.FlotskiyBookShopApp.repository.UserContactRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.UserRepository;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import javax.management.InstanceAlreadyExistsException;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRegistrationServiceTests {

    private final MockMvc mockMvc;

    @InjectMocks
    private final UserRegistrationService userRegistrationService;
    private final PasswordEncoder passwordEncoder;
    private RegistrationForm registrationForm;
    private UserEntity testUserEntity;
    private UserContactEntity testUserContactEntity;

    @MockBean
    private UserRepository userRepositoryMock;

    @MockBean
    private UserContactRepository userContactRepositoryMock;

    @MockBean
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserRegistrationServiceTests(
            MockMvc mockMvc,
            UserRegistrationService userRegistrationService,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager
    ) {
        this.mockMvc = mockMvc;
        this.userRegistrationService = userRegistrationService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @BeforeAll
    void setUp() {
        registrationForm = new RegistrationForm();
        registrationForm.setEmail("test@test.test");
        registrationForm.setName("Name Test");
        registrationForm.setPass("testpass");

        testUserEntity = new UserEntity();
        testUserEntity.setId(1);
        testUserEntity.setName(registrationForm.getName());
        testUserEntity.setHash(passwordEncoder.encode(registrationForm.getPass()));

        testUserContactEntity = new UserContactEntity();
        testUserContactEntity.setId(1);
        testUserContactEntity.setType(ContactType.EMAIL);
        testUserContactEntity.setContact(registrationForm.getEmail());
        testUserContactEntity.setUserEntity(testUserEntity);
    }

    @AfterAll
    void tearDown() {
        registrationForm = null;
        testUserEntity = null;
        testUserContactEntity = null;
    }

    @Test
    void registerNewUserWithContact() {
        when(userRepositoryMock.save(Mockito.any(UserEntity.class))).thenReturn(testUserEntity);
        UserEntity returnedUser = userRegistrationService.registerNewUser(registrationForm);

        assertNotNull(returnedUser);
        assertTrue(passwordEncoder.matches(registrationForm.getPass(), returnedUser.getHash()));
        assertTrue(CoreMatchers.is(returnedUser.getName()).matches(registrationForm.getName()));

        Mockito.verify(userRepositoryMock, Mockito.times(1)).save(Mockito.any(UserEntity.class));

        when(userContactRepositoryMock.save(Mockito.any(UserContactEntity.class))).thenReturn(testUserContactEntity);
        UserContactEntity returnedUserContact = userRegistrationService.registerNewUserContact(registrationForm, returnedUser);

        assertNotNull(returnedUserContact);
        assertTrue(CoreMatchers.is(returnedUserContact.getType()).matches(ContactType.EMAIL));
        assertTrue(CoreMatchers.is(returnedUserContact.getUserEntity()).matches(returnedUser));
        assertTrue(CoreMatchers.is(returnedUserContact.getContact()).matches(registrationForm.getEmail()));
    }

    @Test
    void registerNewUserFail() {
        Mockito.doReturn(new UserEntity())
                .when(userRepositoryMock)
                .findUserEntityByUserContactEntity_TypeAndUserContactEntity_Contact(ContactType.EMAIL, registrationForm.getEmail());
        UserEntity userEntity = null;
        try {
            userEntity = userRegistrationService.registerNewUserWithContact(registrationForm);
        } catch (InstanceAlreadyExistsException e) {
            Logger.getLogger(this.getClass().getSimpleName()).info(e.getLocalizedMessage());
        }
        assertNull(userEntity);
    }

    @Test
    void jwtLogin() {
        Mockito.doReturn(testUserEntity)
                .when(userRepositoryMock)
                .findUserEntityByUserContactEntity_TypeAndUserContactEntity_Contact(ContactType.EMAIL, registrationForm.getEmail());

        Mockito.doReturn(testUserContactEntity)
                .when(userContactRepositoryMock)
                .findUserContactEntityByUserEntityId(testUserEntity.getId());

        ContactConfirmPayloadDto confirmPayload = new ContactConfirmPayloadDto();
        confirmPayload.setContact(registrationForm.getEmail());
        confirmPayload.setCode(registrationForm.getPass());
        ContactConfirmationResponse contactConfirmationResponse = userRegistrationService.jwtLogin(confirmPayload);

        assertNotNull(contactConfirmationResponse);
        assertTrue(contactConfirmationResponse.getResult().matches(".+\\..+\\..+"));
    }

    @Test
    void newUserRegistration() throws Exception {
        mockMvc.perform(
                        post("/reg")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("name", "Hello Test User")
                                .param("email", "test@test.test")
                                .param("pass", "testpass")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("/signin"));

        Mockito.verify(userRepositoryMock, Mockito.times(1))
                .save(Mockito.any(UserEntity.class));
        Mockito.verify(userContactRepositoryMock, Mockito.times(1))
                .save(Mockito.any(UserContactEntity.class));
    }
}
