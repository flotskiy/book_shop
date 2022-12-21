package com.github.flotskiy.bookshop.config;

import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;

@Configuration
public class SpringfoxConfig {

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    public ApiInfo apiInfo() {
        return new ApiInfo(
               "Bookshop API",
               "API for bookstore",
               "1.0",
                "http://www.termsofservice.org",
                new Contact("API owner", "http://www.bookshopapiownersite.com", "owner@bookshopapi.org"),
                "api_licence",
                "http://www.licenceapibookshopownerlalalala.com",
                new ArrayList<>()
        );
    }
}
