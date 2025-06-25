package com.makers.project3.feature;

import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeDriver;


public class Project3ApplicationAutomationTests {
    private static ChromeDriver driver;

    @Test
    public void launchBrowser() {
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/homepage");
    }
}
