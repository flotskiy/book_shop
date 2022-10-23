package com.github.flotskiy.FlotskiyBookShopApp.security;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.UserDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserContactEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.enums.ContactType;
import com.github.flotskiy.FlotskiyBookShopApp.repository.UserContactRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BookstoreUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserContactRepository userContactRepository;

    @Autowired
    public BookstoreUserDetailsService(UserRepository userRepository, UserContactRepository userContactRepository) {
        this.userRepository = userRepository;
        this.userContactRepository = userContactRepository;
    }

    @Override
    public BookstoreUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository
                .findUserEntityByUserContactEntity_TypeAndUserContactEntity_Contact(ContactType.EMAIL, email);
        if (userEntity != null) {
            UserDto userDto = convertUserEntityToUserDto(userEntity);
            return new BookstoreUserDetails(userDto);
        } else {
            throw new UsernameNotFoundException("UserEntity not found");
        }
    }

    private UserDto convertUserEntityToUserDto(UserEntity userEntity) {
        int userId = userEntity.getId();
        UserContactEntity userContactEntity = userContactRepository.findUserContactEntityByUserEntityId(userId);

        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setName(userEntity.getName());
        userDto.setPassword(userEntity.getHash());
        userDto.setBalance(userEntity.getBalance());
        ContactType contactType = userContactEntity.getType();
        if (contactType.name().equals("PHONE")) {
            userDto.setPhone(userContactEntity.getContact());
        } else if (contactType.name().equals("EMAIL")) {
            userDto.setEmail(userContactEntity.getContact());
        }
        return userDto;
    }
}
