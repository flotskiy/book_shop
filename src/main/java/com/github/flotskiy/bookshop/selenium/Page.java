package com.github.flotskiy.bookshop.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Page {

    private final ChromeDriver driver;

    public Page(ChromeDriver driver) {
        this.driver = driver;
    }

    public Page callPage() {
        String url = "http://localhost:8085/";
        driver.get(url);
        return this;
    }

    public Page pause() throws InterruptedException {
        Thread.sleep(2000);
        return this;
    }

    public Page setUpSearchToken(String serchToken) {
        WebElement element = driver.findElement(By.id("query"));
        element.sendKeys(serchToken);
        return this;
    }

    public Page submitSearchQuery() {
        WebElement element = driver.findElement(By.id("search"));
        element.submit();
        return this;
    }

    public Page openSignInLink() {
        WebElement element = driver.findElement(By.cssSelector("a[href='/signin']"));
        element.click();
        return this;
    }

    public Page chooseEmail() {
        WebElement element = driver
                .findElement(By.xpath("/html/body/div/div[2]/main/form/div/div[1]/div[2]/div/div[2]/label/input"));
        element.click();
        return this;
    }

    public Page pasteEmail(String email) {
        WebElement element = driver.findElement(By.id("mail"));
        element.sendKeys(email);
        return this;
    }

    public Page buttonNext() {
        WebElement element = driver.findElement(By.id("sendauth"));
        element.click();
        return this;
    }

    public Page pastePassword(String pass) {
        WebElement element = driver.findElement(By.id("mailcode"));
        element.sendKeys(pass);
        return this;
    }

    public Page buttonEnter() {
        WebElement element = driver.findElement(By.id("toComeInMail"));
        element.click();
        return this;
    }

    public Page scrollDown() {
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,1200)", "");
        return this;
    }

    public Page openMagicTag() {
        WebElement element = driver.findElement(By.cssSelector("a[href='/tags/magic']"));
        element.click();
        return this;
    }

    public Page openBook() {
        WebElement element = driver
                .findElement(By.cssSelector("a[href='/books/book-page-ltz-901-hpaqvpkc']"));
        element.click();
        return this;
    }

    public Page pasteReview(String review) {
        WebElement element = driver.findElement(By.id("review"));
        element.sendKeys(review);
        return this;
    }

    public Page submitReview() {
        WebElement element = driver.findElement(By.className("Comments-sendReview"));
        element.submit();
        return this;
    }

    public Page reloadPage() {
        driver.navigate().refresh();
        return this;
    }

    public Page likeReview() {
        WebElement element = driver.findElement(By.className("btn_like"));
        element.click();
        return this;
    }

    public Page openGenresLink() {
        WebElement element = driver.findElement(By.cssSelector("a[href='/genres']"));
        element.click();
        return this;
    }

    public Page openNovelLink() {
        WebElement element = driver.findElement(By.cssSelector("a[href='/genres/novel']"));
        element.click();
        return this;
    }

    public Page openRecentLink() {
        WebElement element = driver.findElement(By.cssSelector("a[href='/recent']"));
        element.click();
        return this;
    }

    public Page openPopularLink() {
        WebElement element = driver.findElement(By.cssSelector("a[href='/popular']"));
        element.click();
        return this;
    }

    public Page openAuthorsLink() {
        WebElement element = driver.findElement(By.cssSelector("a[href='/authors']"));
        element.click();
        return this;
    }

    public Page openAuthorPageLink() {
        WebElement element = driver.findElement(By.linkText("Air Dust"));
        element.click();
        return this;
    }
}
