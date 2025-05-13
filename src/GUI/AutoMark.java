package GUI;

import Logic.JsonIO;
import Logic.MoodleUser;
import Logic.NowDateTime;
import Logic.Schedule;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import javax.swing.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AutoMark extends JFrame {
    private JButton closeBtn;
    private JTextArea textArea1;
    private JPanel panel1;
    private JScrollPane scrollPane1;
    private volatile boolean running = true;

    public AutoMark(MoodleUser user, boolean invert) {
        super("AutoMark");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(panel1);
        setSize(500, 500);
        setLocationRelativeTo(null);

        textArea1.setEditable(false);
        textArea1.setLineWrap(true);
        textArea1.setWrapStyleWord(true);

        closeBtn.addActionListener(e -> {
            running = false;
            dispose();
            new Menu(user);
        });

        setVisible(true);

        new Thread(() -> {
            try {
                String strJson = JsonIO.readStringFromFile("classes.json");
                Schedule schedule = Schedule.stringAsSchedule(strJson);
                WebDriver driver = user.getDriver();

                while (running) {
                    String weekType = NowDateTime.getWeekType(invert);
                    String dayOfWeek = NowDateTime.getDayOfWeek();
                    String currentTime = NowDateTime.getTime();
                    appendToTextArea(String.format(dayOfWeek, weekType, currentTime));

                    String outputStr = String.format("%s %s %s\n", weekType, dayOfWeek, currentTime);

                    Schedule.ClassInfo pairClass = schedule.getNowClassInfo(weekType, dayOfWeek, currentTime);

                    if (pairClass != null) {
                        String link = pairClass.getUrl();
                        if (link != null) {

                            outputStr += link + "\n";
                            System.out.println(outputStr);

                            int attempt = 1;
                            boolean marked = false;

                            while (!marked) {
                                appendToTextArea("Попытка отметки #" + attempt);
                                marked = mark(driver, link);

                                if (!marked) {
                                    appendToTextArea(String.format("Не удалось отметиться (попытка %d). Повтор через 3 минуты...\n", attempt));
                                    try {
                                        TimeUnit.MINUTES.sleep(3);
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                    }
                                    attempt++;
                                }

                            }
                            sleepMinutes(1);
                            appendToTextArea("Успешно отметился #" + attempt);
                        }
                    }
                }
            } catch (Exception e) {
                appendToTextArea("Ошибка: Посещаемости пока нет :(" + "\n");
                e.printStackTrace();
            } finally {
                appendToTextArea("Автоматическая отметка остановлена\n");
            }
        }).start();
    }
    private void appendToTextArea(String text) {
        SwingUtilities.invokeLater(() -> {
            textArea1.append(text+"\n");
            textArea1.setCaretPosition(textArea1.getDocument().getLength());
        });
    }

    private void sleepMinutes(int minutes) {
        try {
            TimeUnit.MINUTES.sleep(minutes);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void sleepSeconds(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }



    public static boolean mark(WebDriver driver, String link) {
        if (!Objects.equals(driver.getCurrentUrl(), link)) {
            driver.get(link);
        }
        try {
            driver.navigate().refresh();
            TimeUnit.SECONDS.sleep(5);
            driver.findElement(By.linkText("Отметить свое присутствие")).click();
            TimeUnit.SECONDS.sleep(3);
            driver.findElement(By.name("status")).click();
            TimeUnit.SECONDS.sleep(3);
            driver.findElement(By.id("id_submitbutton")).click();
            return true;
        } catch (InterruptedException | NoSuchElementException e) {
            return false;
        }
    }
}