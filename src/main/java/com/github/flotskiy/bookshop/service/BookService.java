package com.github.flotskiy.bookshop.service;

import com.github.flotskiy.bookshop.aspect.annotations.EntityAccessControllable;
import com.github.flotskiy.bookshop.exceptions.BookstoreApiWrongParameterException;
import com.github.flotskiy.bookshop.model.dto.book.AuthorDto;
import com.github.flotskiy.bookshop.model.dto.book.BookDto;
import com.github.flotskiy.bookshop.model.dto.book.PopularityDto;
import com.github.flotskiy.bookshop.model.dto.book.page.BookFileDto;
import com.github.flotskiy.bookshop.model.dto.book.page.BookSlugDto;
import com.github.flotskiy.bookshop.model.dto.user.UserDto;
import com.github.flotskiy.bookshop.model.entity.author.AuthorEntity;
import com.github.flotskiy.bookshop.model.entity.book.BookEntity;
import com.github.flotskiy.bookshop.model.entity.book.BookTagEntity;
import com.github.flotskiy.bookshop.model.entity.book.file.BookFileEntity;
import com.github.flotskiy.bookshop.model.entity.book.file.BookFileTypeEntity;
import com.github.flotskiy.bookshop.repository.Book2AuthorRepository;
import com.github.flotskiy.bookshop.repository.Book2GenreRepository;
import com.github.flotskiy.bookshop.repository.BookRepository;
import com.github.flotskiy.bookshop.repository.BookTag2BookRepository;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final TagService tagService;
    private final AuthorService authorService;
    private final ReviewAndLikeService reviewAndLikeService;
    private final Book2UserService book2UserService;
    private final BookTag2BookRepository bookTag2BookRepository;
    private final Book2GenreRepository book2GenreRepository;
    private final Book2AuthorRepository book2AuthorRepository;
    private final ResourceStorageService resourceStorageService;

    @Autowired
    public BookService(
            BookRepository bookRepository,
            BooksRatingAndPopularityService booksRatingAndPopularityService,
            TagService tagService,
            AuthorService authorService,
            ReviewAndLikeService reviewAndLikeService,
            Book2UserService book2UserService,
            BookTag2BookRepository bookTag2BookRepository,
            Book2GenreRepository book2GenreRepository,
            Book2AuthorRepository book2AuthorRepository,
            ResourceStorageService resourceStorageService
    ) {
        this.bookRepository = bookRepository;
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
        this.tagService = tagService;
        this.authorService = authorService;
        this.reviewAndLikeService = reviewAndLikeService;
        this.book2UserService = book2UserService;
        this.bookTag2BookRepository = bookTag2BookRepository;
        this.book2GenreRepository = book2GenreRepository;
        this.book2AuthorRepository = book2AuthorRepository;
        this.resourceStorageService = resourceStorageService;
    }

    public List<BookDto> getListOfRecommendedBooks(Integer offset, Integer limit, UserDto userDto) {
        int userId = userDto.getId();
        List<Integer> bookIdsList = Collections.emptyList();
        if (userId != -1) {
            bookIdsList = getRecommendedBookIdsForUser(userDto);
        }
        if (userId == -1 || bookIdsList.isEmpty()) {
            int subListLeftLimit = offset * limit;
            List<BookDto> resultWhenNoBooksWasAddedBefore = getRecommendedBooksIfNoBooksAddedBefore(userDto);
            if (subListLeftLimit >= resultWhenNoBooksWasAddedBefore.size() - 1) {
                return Collections.emptyList();
            }
            if (subListLeftLimit + limit > resultWhenNoBooksWasAddedBefore.size()) {
                limit = resultWhenNoBooksWasAddedBefore.size();
                return resultWhenNoBooksWasAddedBefore.subList(subListLeftLimit, limit);
            }
            return resultWhenNoBooksWasAddedBefore.subList(subListLeftLimit, subListLeftLimit + limit);
        } else {
            Pageable nextPage = PageRequest.of(offset, limit);
            List<BookEntity> recommendedBookEntities =
                    bookRepository.findBookEntitiesByIdIsInOrderByPubDageDesc(bookIdsList, nextPage).getContent();
            return convertBookEntitiesToBookDtoWithRatingList(recommendedBookEntities, userId);
        }
    }

    public List<BookDto> getBooksByAuthor(String authorName, Integer userId) {
        return convertBookEntitiesToBookDtoList(bookRepository.findBookEntitiesByAuthorEntitiesNameContaining(authorName), userId);
    }

    @EntityAccessControllable
    public List<BookDto> getBooksByTitle(String title, Integer userId) {
        if (title.length() <= 1) {
            throw new BookstoreApiWrongParameterException("Wrong value passed to parameter");
        }
        List<BookDto> data = convertBookEntitiesToBookDtoList(bookRepository.findBookEntitiesByTitleContaining(title), userId);
        if (data.isEmpty()) {
            throw new BookstoreApiWrongParameterException("No data found with specified parameter");
        }
        return data;
    }

    public List<BookDto> getBooksWithPriceBetween(int min, int max, Integer userId) {
        return convertBookEntitiesToBookDtoList(bookRepository.findBookEntitiesByPriceBetween(min, max), userId);
    }

    public List<BookDto> getBooksWithMaxDiscount(Integer userId) {
        return convertBookEntitiesToBookDtoList(bookRepository.getBookEntitiesWithMaxDiscount(), userId);
    }

    public List<BookDto> getBestsellers(Integer userId) {
        return convertBookEntitiesToBookDtoList(bookRepository.getBestsellers(), userId);
    }

    public Page<BookDto> getPageOfSearchResultBooks(String searchWord, int offset, int limit, int userId) {
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<BookEntity> bookEntities = bookRepository.findBookEntitiesByTitleContaining(searchWord, nextPage);
        return bookEntities.map(bookEntity -> convertBookEntityToBookDto(bookEntity, userId));
    }

    public Integer getSearchResultsSize(String searchWord) {
        return bookRepository.countBookEntitiesByTitleContaining(searchWord);
    }

    public Page<BookDto> getPageOfRecentBooks(LocalDate from, LocalDate to, int offset, int limit, UserDto userDto) {
        to = to.plusDays(1);
        List<Integer> allBooksIdOfUser = getaAllUserBooksId(userDto);
        if (allBooksIdOfUser.isEmpty()) {
            allBooksIdOfUser.add(0);
        }
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<BookEntity> bookEntitiesClean =
                bookRepository.findBookEntitiesByPubDateBetweenAndIdNotContainingOrderByPubDateDescIdDesc(
                        from, to, allBooksIdOfUser, nextPage);
        return bookEntitiesClean.map(bookEntity -> convertBookEntityToBookDtoWithRatingValue(bookEntity, userDto.getId()));
    }

    public List<BookDto> getRecentBooks(int offset, int limit, UserDto userDto) {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusMonths(1);
        return getPageOfRecentBooks(from, to, offset, limit, userDto).getContent();
    }

    public List<BookDto> getPopularBooks(int offset, int limit, UserDto userDto) {
        List<PopularityDto> popularityDtoList = booksRatingAndPopularityService.getAllPopularBooks(userDto);
        List<Integer> bookIds = booksRatingAndPopularityService.getPopularBookIds(popularityDtoList, offset, limit);
        List<BookEntity> bookEntities = bookRepository.findBookEntitiesByIdIsIn(bookIds);
        List<BookDto> result = new ArrayList<>();
        for (BookEntity book : bookEntities) {
            result.add(convertBookEntityToBookDtoWithPopularityValue(book, popularityDtoList, userDto.getId()));
        }
        result.sort(Comparator.comparing(BookDto::getRating).reversed());
        return result;
    }

    public Page<BookDto> getPageOfBooksByTag(Integer tagId, Integer offset, Integer limit, Integer userId) {
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<BookEntity> bookEntities = bookRepository.findBookEntitiesByBookTagsIdOrderByPubDateDesc(tagId, nextPage);
        return bookEntities.map(bookEntity -> convertBookEntityToBookDto(bookEntity, userId));
    }

    public Page<BookDto> getPageOfBooksByGenreId(Integer genreId, Integer offset, Integer limit, Integer userId) {
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<BookEntity> bookEntities =
                bookRepository.findBookEntitiesByGenreEntitiesIdOrderByPubDateDesc(genreId, nextPage);
        return bookEntities.map(bookEntity -> convertBookEntityToBookDto(bookEntity, userId));
    }

    public Page<BookDto> getPageOfBooksByAuthorId(Integer authorId, Integer offset, Integer limit, Integer userId) {
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<BookEntity> bookEntities =
                bookRepository.findBookEntitiesByAuthorEntitiesIdOrderByPubDateDesc(authorId, nextPage);
        return bookEntities.map(bookEntity -> convertBookEntityToBookDto(bookEntity, userId));
    }

    public List<BookDto> getBooksBySlugIn(List<String> slugs, Integer userId) {
        return convertBookEntitiesToBookDtoWithRatingList(bookRepository.findBookEntitiesBySlugIn(slugs), userId);
    }

    public BookEntity getBookEntityBySlug(String slug) {
        return bookRepository.findBookEntityBySlug(slug);
    }

    public BookEntity getBookEntityById(Integer bookId) {
        Optional<BookEntity> bookEntityOptional = bookRepository.findById(bookId);
        if (bookEntityOptional.isEmpty()) {
            throw new EntityNotFoundException("BookEntity not found");
        }
        return bookEntityOptional.get();
    }

    public void saveBookEntity(BookEntity bookEntity) {
        bookRepository.save(bookEntity);
    }

    public BookSlugDto getBookSlugBySlug(String slug, Integer userId) {
        BookEntity bookEntity = bookRepository.findBookEntityBySlug(slug);
        BookSlugDto bookSlugDto = new BookSlugDto(convertBookEntityToBookDtoWithRatingValue(bookEntity, userId));
        bookSlugDto.setDescription(bookEntity.getDescription());
        Set<BookTagEntity> tagEntities = bookEntity.getBookTags();

        Set<AuthorDto> authorDtos = bookEntity.getAuthorEntities().stream()
                .map(authorService::convertAuthorEntityToAuthorDtoShort).collect(Collectors.toSet());
        bookSlugDto.setAuthors(authorDtos);
        bookSlugDto.setTags(tagService.convertTagEntitySetToTagDtoSet(tagEntities));
        List<BookFileDto> bookFileDtos = null;
        if (resourceStorageService.isDownloadAllowed(bookEntity.getId(), userId)) {
            bookFileDtos = bookEntity.getBookFileEntities().stream()
                    .map(this::convertBookFileEntityToBookFileDto)
                    .sorted(Comparator.comparing(BookFileDto::getName))
                    .collect(Collectors.toList());
        }
        bookSlugDto.setBookFileDtos(bookFileDtos);
        bookSlugDto.setBookReviewDtos(reviewAndLikeService.getBookReviewDtos(bookEntity.getId()));
        return bookSlugDto;
    }

    public String checkFrom(String from) {
        if (from == null || from.isEmpty()) {
            return "01.01.1900";
        }
        return from;
    }

    public String checkTo(String to) {
        if (to == null || to.isEmpty()) {
            to = LocalDate.now().plus(1, ChronoUnit.DAYS).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        }
        return to;
    }

    public List<BookEntity> findBookEntitiesByIdIsIn(List<Integer> bookIdList) {
        return bookRepository.findBookEntitiesByIdIsIn(bookIdList);
    }

    public List<BookDto> convertBookEntitiesToBookDtoWithRatingList(List<BookEntity> booksListToConvert, Integer userId) {
        List<BookDto> booksDtoList = new ArrayList<>();
        for (BookEntity book : booksListToConvert) {
            booksDtoList.add(convertBookEntityToBookDtoWithRatingValue(book, userId));
        }
        return booksDtoList;
    }

    private List<BookDto> convertBookEntitiesToBookDtoList(List<BookEntity> booksListToConvert, Integer userId) {
        List<BookDto> booksDtoList = new ArrayList<>();
        for (BookEntity book : booksListToConvert) {
            booksDtoList.add(convertBookEntityToBookDto(book, userId));
        }
        return booksDtoList;
    }

    private BookDto convertBookEntityToBookDto(BookEntity bookEntity, Integer userId) {
        BookDto bookDto = new BookDto();
        int bookId = bookEntity.getId();
        bookDto.setId(bookId);
        bookDto.setSlug(bookEntity.getSlug());
        bookDto.setImage(bookEntity.getImage());
        bookDto.setTitle(bookEntity.getTitle());
        short discount = bookEntity.getDiscount();
        bookDto.setDiscount(discount);
        bookDto.setIsBestseller(bookEntity.getIsBestseller() == 1);
        int price = bookEntity.getPrice();
        bookDto.setPrice(price);
        int discountPrice = (int) (price * ((100 - (double) discount) / 100));
        bookDto.setDiscountPrice(discountPrice);
        bookDto.setPubDate(bookEntity.getPubDate());

        Set<AuthorEntity> authorEntities = bookEntity.getAuthorEntities();
        List<AuthorEntity> sortedBySortIndexAuthorEntities =
                authorService.sortAuthorEntitiesBySortIndex(authorEntities, bookId);

        StringBuilder authorName = new StringBuilder();
        for (AuthorEntity author : sortedBySortIndexAuthorEntities) {
            authorName.append(author.getName()).append(", ");
        }
        if (authorName.length() > 0) {
            authorName.delete(authorName.length() - ", ".length(), authorName.length());
        }
        if (authorName.length() == 0) {
            authorName = new StringBuilder("BOOK HAS NO AUTHOR");
        }
        bookDto.setAuthors(authorName.toString());

        bookDto.setStatus(book2UserService.getBookStatus(bookDto.getId(), userId));
        bookDto.setRating((short) 0);

        return bookDto;
    }

    private BookDto convertBookEntityToBookDtoWithRatingValue(BookEntity bookEntity, Integer userId) {
        BookDto bookDto = convertBookEntityToBookDto(bookEntity, userId);
        short rating = booksRatingAndPopularityService.calculateBookRating(bookEntity.getId()).shortValue();
        bookDto.setRating(rating);
        return bookDto;
    }

    private BookDto convertBookEntityToBookDtoWithPopularityValue(
            BookEntity bookEntity, List<PopularityDto> popularityDtoList, Integer userId) {
        BookDto bookDto = convertBookEntityToBookDto(bookEntity, userId);

        short rating = 0;
        for (PopularityDto popularityDto : popularityDtoList) {
            if (bookEntity.getId() == popularityDto.getBookId()) {
                rating = popularityDto.getPopularity();
            }
        }
        bookDto.setRating(rating);

        return bookDto;
    }

    private BookFileDto convertBookFileEntityToBookFileDto(BookFileEntity bookFileEntity) {
        BookFileDto bookFileDto = new BookFileDto();
        String hash = bookFileEntity.getHash();
        bookFileDto.setHash(hash);
        int typeId = bookFileEntity.getTypeId();
        BookFileTypeEntity bookFileTypeEntity = resourceStorageService.getBookFileTypeEntityMap().get(typeId);
        bookFileDto.setName(bookFileTypeEntity.getName());
        if (LocaleContextHolder.getLocale().getLanguage().equals("ru")) {
            bookFileDto.setDescription(bookFileTypeEntity.getDescription().split("/")[1]);
        } else {
            bookFileDto.setDescription(bookFileTypeEntity.getDescription().split("/")[0]);
        }
        bookFileDto.setPath(bookFileEntity.getPath());
        bookFileDto.setSize(resourceStorageService.getFileSize(hash));
        return bookFileDto;
    }

    private List<BookDto> getRecommendedBooksIfNoBooksAddedBefore(UserDto userDto) {
        List<Integer> first30bookIdsListWithMaxRating =
                booksRatingAndPopularityService.getFirst30bookIdsWithMaxUsersRatingMoreOrEquals4();
        List<BookDto> first30booksListWithMaxRating = convertBookEntitiesToBookDtoWithRatingList(
                bookRepository.findBookEntitiesByIdIsIn(first30bookIdsListWithMaxRating), userDto.getId()
        );
        List<BookDto> first30BooksForTheLast30days = getRecentBooks(0, 30, userDto);
        return Stream.concat(first30booksListWithMaxRating.stream(), first30BooksForTheLast30days.stream())
                .distinct()
                .sorted(Comparator.comparing(BookDto::getPubDate))
                .collect(Collectors.toList());
    }

    private List<Integer> getRecommendedBookIdsForUser(UserDto userDto) {
        List<Integer> allUserBooksId = getaAllUserBooksId(userDto);

        List<Integer> allTagsId = bookTag2BookRepository.getTagIdsForBooksIdsInList(allUserBooksId);
        List<Integer> allGenresId = book2GenreRepository.getGenreIdsForBooksIdsInList(allUserBooksId);
        List<Integer> allAuthorsId = book2AuthorRepository.getAuthorIdsForBookIdsInList(allUserBooksId);

        List<Integer> allBooksIdsByTags = bookTag2BookRepository.getBookIdsForTagIdsInList(allTagsId);
        List<Integer> allBooksIdsByGenres = book2GenreRepository.getBookIdsForGenreIdsInList(allGenresId);
        List<Integer> allBooksIdsByAuthors = book2AuthorRepository.getBookIdsForAuthorIdsInList(allAuthorsId);

        Iterable<Integer> allBookIdsIterable = Iterables.unmodifiableIterable(
                Iterables.concat(allBooksIdsByTags, allBooksIdsByGenres, allBooksIdsByAuthors));
        List<Integer> allBookIdsList = Lists.newArrayList(Sets.newHashSet(allBookIdsIterable));
        allBookIdsList.removeAll(allUserBooksId);
        return allBookIdsList;
    }

    private List<Integer> getaAllUserBooksId(UserDto userDto) {
        return userDto.getUserBooksData().getAllBooks()
                .stream().map(BookDto::getId).collect(Collectors.toList());
    }
}
