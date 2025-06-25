package com.makers.project3.feature;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Project3ApplicationAutomationTests {
    private static ChromeDriver driver;
    private static Faker faker;
    private static WebDriverWait wait;
    private Project3ApplicationAutomationTestsPage automationPage;

    @BeforeEach
    public void setup() {
        faker = new Faker();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        automationPage = new Project3ApplicationAutomationTestsPage(driver);
    }

    @Test
    public void successfulSignUpAlsoLogsInUser() {
        String username = faker.name().username();
        String email = username + "@email.com";

        driver.get("http://localhost:8080/");
        driver.findElement(By.cssSelector("a[href='/homepage']")).click();
        driver.findElement(By.linkText("Sign up")).click();
        driver.findElement(By.name("email")).sendKeys(email);
        driver.findElement(By.name("password")).sendKeys("P@55qw0rd");
        driver.findElement(By.name("action")).click();
        WebElement secondActionButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("action")));
        secondActionButton.click();

        driver.findElement(By.cssSelector("a[class='nav-link dropdown-toggle'")).click();
        WebElement userDetail = driver.findElement(By.cssSelector("div[class='d-flex flex-column align-items-center']"));
        assertEquals(username,userDetail.getText() );
    }
    @Test
    public void startNewGame() {
        automationPage.login();
        driver.findElement(By.linkText("Start Game")).click();

    }


}
