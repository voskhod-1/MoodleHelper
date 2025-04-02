package Logic;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class UserCloner {

    static Set<String> windowHandles = new HashSet<>();

    private static boolean cloneUser(WebDriver driver) {
        String nowUrl = driver.getCurrentUrl();
        if (nowUrl.contains("bbb") && nowUrl.contains("edu.vsu.ru/")) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.open('" + nowUrl + "', '_blank');");
            windowHandles = driver.getWindowHandles();

        }
        return true;
    }

    public static boolean multiClone(WebDriver driver, int quantity) {
        for (int i = 0; i < quantity; i++) {
            Random rand = new Random();
            cloneUser(driver);
            try {
                TimeUnit.SECONDS.sleep(rand.nextInt(0, 30));
            } catch (InterruptedException e) {
                return false;
            }
        }
        return true;
    }

    public static boolean deleteClones(WebDriver driver) {
        for (String handle : windowHandles) {
            driver.switchTo().window(handle);
            driver.close();
        }
        return true;
    }

    public static void getMenu(WebDriver driver) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Теперь введите количество клонов");
        int quantity = sc.nextInt();
        multiClone(driver, quantity);
    }
}
