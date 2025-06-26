package com.makers.project3.feature;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class Project3ApplicationAutomationTestsPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;

    public Project3ApplicationAutomationTestsPage(WebDriver driver) {
        this.driver = driver;
    }

    public void login() {
        driver.get("http://localhost:8080/");
        driver.findElement(By.cssSelector("a[href='/homepage']")).click();
        driver.findElement(By.id("username")).sendKeys("examp1e@nationwide.com");
        driver.findElement(By.id("password")).sendKeys("Example!!!");
        driver.findElement(By.name("action")).click();
    }

    public void chooseDeck(Integer deckNumber) {
        String checkboxId = "deck-" + deckNumber;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement deckToggle = wait.until(ExpectedConditions.elementToBeClickable(By.id(checkboxId)));
        deckToggle.click();

        // Custom wait until the checkbox is selected
        wait.until(driver -> driver.findElement(By.id(checkboxId)).isSelected());
    }

    public void pointsToWin(WebDriver driver, String sliderId, int points) {
        String script =
                "var slider = document.getElementById(arguments[0]);" +
                        "slider.value = arguments[1];" +
                        "slider.dispatchEvent(new Event('input'));" +    // update UI/listeners
                        "slider.dispatchEvent(new Event('change'));";

        ((JavascriptExecutor) driver).executeScript(script, sliderId, points);
    }

    public void selectACard(WebDriver driver) {
        By cardSelector = By.cssSelector("div.d-flex.flex-wrap.justify-content-center div.card-wrap.hand.hp");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<WebElement> cards = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(cardSelector));

        if (cards.isEmpty()) {
            throw new RuntimeException("No cards found");
        }

        WebElement randomCard = cards.get(new Random().nextInt(cards.size()));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", randomCard);
        js.executeScript("arguments[0].click();", randomCard);
    }

    public void clickRandomStat(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement card = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".final-card-container .card-wrap.hp")));
        List<WebElement> statButtons = card.findElements(By.cssSelector("button.stat-text-box"));

        if (statButtons.isEmpty()) {
            throw new RuntimeException("No clickable stat buttons found");
        }

        WebElement randomStat = statButtons.get(new Random().nextInt(statButtons.size()));
        wait.until(ExpectedConditions.elementToBeClickable(randomStat));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", randomStat);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", randomStat);
    }
}

