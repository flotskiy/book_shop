package com.github.flotskiy.bookshop.selenium;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SeleniumTests {

    private static ChromeDriver driver;

    @BeforeAll
    static void setup() {
        System.setProperty("webdriver.chrome.driver", "C:/Users/flotskiy/Desktop/-/chromedriver/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(5));
    }

    @AfterAll
    static void tearDown() {
        driver.quit();
    }

    @Test
    public void testMainPageAccess() throws InterruptedException {
        Page page = new Page(driver);
        page
                .callPage()
                .pause();
        assertTrue(driver.getPageSource().contains("BOOKSHOP"));
    }

    @Test
    public void testMainPageSearchByQuery() throws InterruptedException {
        Page page = new Page(driver);
        page
                .callPage()
                .pause()
                .setUpSearchToken("Young A")
                .pause()
                .submitSearchQuery()
                .pause();

        assertTrue(driver.getPageSource().contains("Young Again"));
    }

    @Test
    public void signInAndTravel() throws InterruptedException {
        String testEmail = "flotskiy@mail.test";
        String pass = "mypass";
        String review = "This test is designed especially to review testing purpose";

        Page page = new Page(driver);
        page
                .callPage()
                .pause()
                .openSignInLink()
                .pause()
                .chooseEmail()
                .pause()
                .pasteEmail(testEmail)
                .pause()
                .buttonNext()
                .pause()
                .pastePassword(pass)
                .pause()
                .buttonEnter()
                .pause()
                .callPage()
                .pause()
                .scrollDown()
                .pause()
                .openMagicTag()
                .pause()
                .openBook()
                .pause()
                .pasteReview(review)
                .pause()
                .submitReview()
                .pause()
                .reloadPage()
                .pause()
                .likeReview()
                .pause()
                .reloadPage()
                .pause()
                .openGenresLink()
                .pause()
                .openNovelLink()
                .pause()
                .openRecentLink()
                .pause()
                .openPopularLink()
                .pause()
                .openAuthorsLink()
                .pause()
                .openAuthorPageLink()
                .pause();

        assertTrue(driver.getPageSource().contains("Biography"));
    }
}
