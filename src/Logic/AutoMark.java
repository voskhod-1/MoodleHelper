package Logic;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

public class AutoMark {
    public AutoMark() {
    }

    public static String autoMark(WebDriver driver, Schedule schedule, boolean invert) {
        boolean isMarked = false;
        String var10000 = NowDateTime.getWeekType(invert);
        String outputStr = var10000 + " " + NowDateTime.getDayOfWeek() + " " + NowDateTime.getTime() + "\n";
        Schedule.ClassInfo pairClass = schedule.getNowClassInfo(NowDateTime.getWeekType(invert), NowDateTime.getDayOfWeek(), NowDateTime.getTime());
        String link;
        if (pairClass == null) {
            link = null;
        } else {
            link = pairClass.getUrl();
            if (link != null) {
                outputStr = outputStr + link + "\n";
                System.out.println(outputStr);
                isMarked = mark(driver, link);
                if (!isMarked) {
                    return link;
                }

                return outputStr;
            }

        }

        return null;
    }

    public static void consoleMark(WebDriver driver, boolean invert) {
        try {
            String strJson = JsonIO.readStringFromFile("classes.json");
            Schedule schedule = Schedule.stringAsSchedule(strJson);
            int cnt = 0;

            while (true) {
                while (!autoMark(driver, schedule, invert).contains("mod")) {
                    cnt = 0;
                    System.out.println("Успешно отметил вас");

                    try {
                        TimeUnit.MINUTES.sleep(1L);
                    } catch (InterruptedException var6) {
                        InterruptedException e = var6;
                        e.printStackTrace();
                    }
                }

                ++cnt;
                System.out.println("Не удалось отметиться. Попытка " + cnt);
            }
        } catch (IOException | JsonSyntaxException var7) {
            Exception e = var7;
            ((Exception) e).printStackTrace();
        }
    }

    public static boolean mark(WebDriver driver, String link) {
        if (!Objects.equals(driver.getCurrentUrl(), link)) {
            driver.get(link);
        }

        try {
            driver.findElement(By.linkText("Отметить свое присутствие")).click();
            TimeUnit.SECONDS.sleep(3L);
            driver.findElement(By.name("status")).click();
            TimeUnit.SECONDS.sleep(3L);
            driver.findElement(By.id("id_submitbutton")).click();
            return true;
        } catch (InterruptedException | NoSuchElementException var3) {
            return false;
        }
    }

    public static boolean invertWeeks() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(NowDateTime.getWeekType(false));
        System.out.println("Если необходимо сменить тип недель, введите 'y");
        return scanner.nextLine().equals("y");
    }
}
