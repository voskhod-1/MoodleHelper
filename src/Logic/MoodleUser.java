package Logic;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.NoSuchElementException;

import java.io.IOException;


public class MoodleUser {

    String login,password;
    WebDriver driver = new ChromeDriver();
    public MoodleUser(String login, String password) throws IOException {
        this.login = login;
        this.password = password;
        if (!logIn()) {
            driver.quit();
            throw new IOException("Ошибка авторизации");
        }
    }
    private boolean logIn()  {
        driver.get("https://edu.vsu.ru/login/index.php");
        driver.findElement(By.id("username")).sendKeys(login);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("loginbtn")).click();
        try {
            driver.findElement(By.className("alert-danger"));
        }catch (NoSuchElementException e){
            return true;
        }
        return false;
    }
    public String getUserName(){
        driver.get("https://edu.vsu.ru/my/");
        String userName = driver.findElement(By.className("usertext")).getText();
        return userName;
    }

    public WebDriver getDriver(){
        return driver;
    }
}
