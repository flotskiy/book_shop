package com.github.flotskiy.FlotskiyBookShopApp.security;

import com.github.flotskiy.FlotskiyBookShopApp.aspect.annotations.EntityAccessControllable;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.UserBooksData;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.UserDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.BookEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.links.Book2UserEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserContactEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserEntity;
import com.github.flotskiy.FlotskiyBookShopApp.repository.Book2UserRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.UserContactRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.UserRepository;
import com.github.flotskiy.FlotskiyBookShopApp.service.Book2UserService;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookstoreUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserContactRepository userContactRepository;
    private final Book2UserRepository book2UserRepository;
    private final BookService bookService;
    private final Book2UserService book2UserService;

    @Autowired
    public BookstoreUserDetailsService(
            UserRepository userRepository,
            UserContactRepository userContactRepository,
            Book2UserRepository book2UserRepository,
            BookService bookService,
            Book2UserService book2UserService
    ) {
        this.userRepository = userRepository;
        this.userContactRepository = userContactRepository;
        this.book2UserRepository = book2UserRepository;
        this.bookService = bookService;
        this.book2UserService = book2UserService;
    }

    @EntityAccessControllable
    @Override
    public BookstoreUserDetails loadUserByUsername(String contact) {
        UserEntity userEntity = userRepository.findUserEntityByUserContactEntity_Contact(contact);
        if (userEntity != null) {
            UserDto userDto = convertUserEntityToUserDto(userEntity);
            return new BookstoreUserDetails(userDto);
        } else {
            throw new UsernameNotFoundException("UserEntity not found");
        }
    }

    @EntityAccessControllable
    public UserDto getUserDtoById(Integer id) {
        Optional<UserEntity> userEntityOptional = userRepository.findById(id);
        if (userEntityOptional.isPresent()) {
            return convertUserEntityToUserDto(userEntityOptional.get());
        } else {
            throw new UsernameNotFoundException("UserEntity not found");
        }
    }

    public int gerCurrentAuthenticatedUserId() {
        Object userObject = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BookstoreUserDetails userDetails = null;
        if (userObject instanceof BookstoreUserDetails) {
            userDetails = (BookstoreUserDetails) userObject;
        } else if (userObject instanceof DefaultOAuth2User) {
            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) userObject;
            Map<String, Object> userAttr = oAuth2User.getAttributes();
            String userEmail = userAttr.get("email").toString();
            try {
                userDetails = loadUserByUsername(userEmail);
            } catch (UsernameNotFoundException usernameNotFoundException) {
                return -1;
            }
        } else {
            return -1;
        }
        return userDetails.getUserDto().getId();
    }

    private UserDto convertUserEntityToUserDto(UserEntity userEntity) {
        int userId = userEntity.getId();
        UserContactEntity userContactEntity = userContactRepository.findUserContactEntityByUserEntityId(userId);

        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setName(userEntity.getName());
        userDto.setPassword(userEntity.getHash());
        userDto.setBalance(userEntity.getBalance());
        userDto.setContact(userContactEntity.getContact());
        userDto.setUserBooksData(createUserBooksData(userId));
        return userDto;
    }

    public UserBooksData createUserBooksData(Integer userId) {
        UserBooksData userBooksData = new UserBooksData();
        Map<String, Integer> bookToUserTypeMap = book2UserService.getBookToUserTypeMap();
        for (String key : bookToUserTypeMap.keySet()) {
            switch (key) {
                case "KEPT":
                    userBooksData.setKept(getBookDtoListFromBook2UserList(userId, bookToUserTypeMap.get(key)));
                    break;
                case "CART":
                    userBooksData.setCart(getBookDtoListFromBook2UserList(userId, bookToUserTypeMap.get(key)));
                    break;
                case "PAID":
                    userBooksData.setPaid(getBookDtoListFromBook2UserList(userId, bookToUserTypeMap.get(key)));
                    break;
                case "ARCHIVED":
                    userBooksData.setArchived(getBookDtoListFromBook2UserList(userId, bookToUserTypeMap.get(key)));
                    break;
            }
        }
        return userBooksData;
    }

    private List<BookDto> getBookDtoListFromBook2UserList(Integer userId, Integer typeId) {
        List<Book2UserEntity> book2UserEntities =
                book2UserRepository.findBook2UserEntitiesByUserIdAndTypeId(userId, typeId);
        List<Integer> bookIdList = book2UserEntities.stream().map(Book2UserEntity::getBookId).collect(Collectors.toList());
        List<BookEntity> bookEntityList = bookService.findBookEntitiesByIdIsIn(bookIdList);
        return bookService.convertBookEntitiesToBookDtoWithRatingList(bookEntityList, userId);
    }
}
