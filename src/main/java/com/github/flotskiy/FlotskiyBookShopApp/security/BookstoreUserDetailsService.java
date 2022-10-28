package com.github.flotskiy.FlotskiyBookShopApp.security;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.UserBooksData;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.UserDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.BookEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.links.Book2UserEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.links.Book2UserTypeEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserContactEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.enums.ContactType;
import com.github.flotskiy.FlotskiyBookShopApp.repository.Book2UserRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.Book2UserTypeRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.UserContactRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.UserRepository;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class BookstoreUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserContactRepository userContactRepository;
    private final Book2UserRepository book2UserRepository;
    private final Book2UserTypeRepository book2UserTypeRepository;
    private final BookService bookService;

    private Map<String, Integer> bookToUserTypeMap;
    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Autowired
    public BookstoreUserDetailsService(
            UserRepository userRepository,
            UserContactRepository userContactRepository,
            Book2UserRepository book2UserRepository,
            Book2UserTypeRepository book2UserTypeRepository,
            BookService bookService
    ) {
        this.userRepository = userRepository;
        this.userContactRepository = userContactRepository;
        this.book2UserRepository = book2UserRepository;
        this.book2UserTypeRepository = book2UserTypeRepository;
        this.bookService = bookService;
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
        userDto.setUserBooksData(createUserBooksData(userId));
        return userDto;
    }

    private UserBooksData createUserBooksData(Integer userId) {
        UserBooksData userBooksData = new UserBooksData();
        if (bookToUserTypeMap == null) {
            bookToUserTypeMap = fillInBookToUserTypeMap();
        }
        for (String key : bookToUserTypeMap.keySet()) {
            if (key.equals("KEPT")) {
                userBooksData.setKept(getBookDtoListFromBook2UserList(userId, bookToUserTypeMap.get(key)));
            } else if (key.equals("CART")) {
                userBooksData.setCart(getBookDtoListFromBook2UserList(userId, bookToUserTypeMap.get(key)));
            } else if (key.equals("PAID")) {
                userBooksData.setPaid(getBookDtoListFromBook2UserList(userId, bookToUserTypeMap.get(key)));
            } else if (key.equals("ARCHIVED")) {
                userBooksData.setArchived(getBookDtoListFromBook2UserList(userId, bookToUserTypeMap.get(key)));
            }
        }
        return userBooksData;
    }

    private List<BookDto> getBookDtoListFromBook2UserList(Integer userId, Integer typeId) {
        List<Book2UserEntity> book2UserEntities =
                book2UserRepository.findBook2UserEntitiesByUserIdAndTypeId(userId, typeId);
        List<Integer> bookIdList = book2UserEntities.stream().map(Book2UserEntity::getBookId).collect(Collectors.toList());
        List<BookEntity> bookEntityList = bookService.findBookEntitiesByIdIsIn(bookIdList);
        return bookService.convertBookEntitiesToBookDtoWithRatingList(bookEntityList);
    }


    private Map<String, Integer> fillInBookToUserTypeMap() {
        Map<String, Integer> result = new HashMap<>();
        List<Book2UserTypeEntity> book2UserTypeEntities = book2UserTypeRepository.findAll();
        result = book2UserTypeEntities.stream().collect(Collectors.toMap(Book2UserTypeEntity::getName, Book2UserTypeEntity::getId));
        logger.info("get info from DB about Book2User types: " + result);
        return result;
    }
}
