package com.github.flotskiy.bookshop.service;

import com.github.flotskiy.bookshop.model.dto.book.BookDto;
import com.github.flotskiy.bookshop.model.external.google.api.books.Item;
import com.github.flotskiy.bookshop.model.external.google.api.books.Root;
import com.github.flotskiy.bookshop.model.external.google.api.books.SaleInfo;
import com.github.flotskiy.bookshop.model.external.google.api.books.VolumeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class GoogleSearchBookService {

    private final RestTemplate restTemplate;

    @Autowired
    public GoogleSearchBookService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${google.books.api.key}")
    private String googleApiKey;

    public List<BookDto> getGoogleBooksApiSearchResult(String searchWord, Integer offset, Integer limit) {
        String requestUrl = "https://www.googleapis.com/books/v1/volumes?" +
                "q=" + searchWord +
                "&key=" + googleApiKey +
                "&filter=paid-ebooks" +
                "&startIndex=" + offset +
                "&maxResults=" + limit;

        List<BookDto> googleBooks = new ArrayList<>();
        try {
            Root root = restTemplate.getForEntity(requestUrl, Root.class).getBody();

            if (root != null) {
                for (Item item : root.getItems()) {
                    BookDto bookDto = convertItemToBookDto(item);
                    googleBooks.add(bookDto);
                }
            }
        } catch (NullPointerException npe) {
            Logger.getLogger(this.getClass().getSimpleName()).warning(npe.getMessage());
        }
        return googleBooks;
    }

    private BookDto convertItemToBookDto(Item item) {
        BookDto bookDto = new BookDto();

        VolumeInfo volumeInfo = item.getVolumeInfo();
        if (volumeInfo != null) {
            bookDto.setAuthors(String.join(",", volumeInfo.getAuthors()));
            bookDto.setTitle(volumeInfo.getTitle());
            bookDto.setImage(volumeInfo.getImageLinks().getThumbnail());
            bookDto.setSlug(volumeInfo.getPreviewLink());
        }

        SaleInfo saleInfo = item.getSaleInfo();
        if (saleInfo != null) {
            int price = 0;
            if (saleInfo.getListPrice() != null) {
                price = saleInfo.getListPrice().getAmount();
            }
            double discountPrice = 0.;
            if (saleInfo.getRetailPrice() != null) {
                discountPrice = saleInfo.getRetailPrice().getAmount();
            }
            int discountSize = 0;
            if (price != 0 && discountPrice != 0) {
                discountSize = 100 - ((int) (100. * discountPrice / price));
            }
            bookDto.setPrice(price);
            bookDto.setDiscountPrice((int) discountPrice);
            bookDto.setDiscount((short) discountSize);
        }
        return bookDto;
    }
}
