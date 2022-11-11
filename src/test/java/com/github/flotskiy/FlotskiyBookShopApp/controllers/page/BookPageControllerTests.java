package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class BookPageControllerTests {

    private final MockMvc mockMvc;

    @Autowired
    public BookPageControllerTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    @WithUserDetails("flotskiy@mail.test")
    void putBookToCartByRegisteredUser() throws Exception {
        mockMvc.perform(post("/books/changeBookStatus/{slug}", "book-page-rhz-176-tfj7uhj5")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new JSONObject()
                                                .put("booksIds", "book-page-rhz-176-tfj7uhj5")
                                                .put("status", "CART").toString()
                                )
                )
                .andExpect(status().isOk())
                .andExpect(content().json("{\"result\":true}"));
    }

    @Test
    @WithUserDetails("flotskiy@mail.test")
    void removeBookFromCartByRegisteredUser() throws Exception {
        mockMvc.perform(
                    post("/books/changeBookStatus/cart/remove/{slug}", "book-page-rhz-176-tfj7uhj5")
                )
                .andExpect(status().isOk())
                .andExpect(content().json("{\"result\":true}"));
    }
}
