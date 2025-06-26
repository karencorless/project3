package com.makers.project3.feature;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Project3ApplicationAutomationTests {
    private static ChromeDriver driver;
    private static Faker faker;
    private static WebDriverWait wait;
    private Project3ApplicationAutomationTestsPage automationPage;

    @BeforeEach
    public void setup() {
        faker = new Faker();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
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
        WebElement secondActionButton = wait.until(ExpectedConditions.elementToBeClickable(By.name("action")));
        secondActionButton.click();

        driver.findElement(By.cssSelector("a[class='nav-link dropdown-toggle'")).click();
        WebElement userDetail = driver.findElement(By.cssSelector("div[class='d-flex flex-column align-items-center']"));
        assertEquals(username, userDetail.getText());
    }

    @Test
    public void startNewGame() throws InterruptedException {
        automationPage.login();
        WebElement newGame = wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Start Game")));
        newGame.click();
        //choose decks
        automationPage.chooseDeck(2);
        assertTrue(driver.findElement(By.id("deck-2")).isSelected());
        //choose points to win
        automationPage.pointsToWin(driver, "pointsToWin", 1);
        WebElement output = driver.findElement(By.cssSelector("#pointsToWin + output"));
        assertEquals("1", output.getText());
        //start game
        WebElement startGame = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit")));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center'}); arguments[0].click();", startGame);
        //select a card
        automationPage.selectACard(driver);
        WebElement chosenCard = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".final-card-container .card-wrap.hp")));
        assertTrue(chosenCard.isDisplayed());
        //select stat
        automationPage.clickRandomStat(driver);

        List<WebElement> statValues = driver.findElements(By.cssSelector(".score-reveal-container .reveal p:nth-of-type(2) strong"));
        int yourStat = Integer.parseInt(statValues.get(0).getText().trim());
        int player2Stat = Integer.parseInt(statValues.get(1).getText().trim());
        WebElement winnerIs = driver.findElement(By.cssSelector(".game-result-container p"));
//

        if (yourStat > player2Stat) {
            assertTrue(winnerIs.getText().contains("You have won the game."));
        }
        if (player2Stat> yourStat) {
            assertTrue(winnerIs.getText().contains("Player 2 has won the game."));
        }
        if(yourStat == player2Stat) {
            assertTrue(winnerIs.getText().contains("Draw"));
        }

        System.out.println(winnerIs.getText());
    }
    @AfterEach
    public void exitBrowser() {
        driver.quit();
    }
}





