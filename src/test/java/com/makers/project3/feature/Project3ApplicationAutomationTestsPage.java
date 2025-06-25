package com.makers.project3.feature;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Project3ApplicationAutomationTestsPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;

    public Project3ApplicationAutomationTestsPage(WebDriver driver){
        this.driver = driver;
    }
    public void login() {
        driver.get("http://localhost:8080/");
        driver.findElement(By.cssSelector("a[href='/homepage']")).click();
        driver.findElement(By.id("username")).sendKeys("user123@gmail.com");
        driver.findElement(By.id("password")).sendKeys("Password123");
        driver.findElement(By.name("action")).click();
    }
}
