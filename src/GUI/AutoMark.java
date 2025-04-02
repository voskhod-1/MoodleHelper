package GUI;

import Logic.JsonIO;
import Logic.MoodleUser;
import Logic.NowDateTime;
import Logic.Schedule;
import org.openqa.selenium.WebDriver;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

public class AutoMark extends JFrame {
    private JButton closeBtn;
    private JTextArea textArea1;
    private JPanel panel1;
    private JScrollPane scrollPane1;

    public AutoMark(MoodleUser user, boolean invert) {
        super("AutoMark");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panel1);
        setSize(400, 400);
        //pack();
        setLocationRelativeTo(null);
        setVisible(true);
        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Menu(user);
            }
        });

        // Ваш поток для обновления текста в textArea1
        new Thread(() -> {
            try {
                String strJson = JsonIO.readStringFromFile("classes.json");
                Schedule schedule = Schedule.stringAsSchedule(strJson);
                while (true) {
                    textArea1.append(NowDateTime.getWeekType(invert) + " " + NowDateTime.getDayOfWeek() + " " + NowDateTime.getTime());
                    String outputInfo = Logic.AutoMark.autoMark(user.getDriver(), schedule, invert);
                    SwingUtilities.invokeLater(() -> {
                        if (outputInfo != null) {
                            int cnt = 0;
                            if (!outputInfo.contains("mod")) {
                                cnt++;
                                textArea1.append("Не удалось отметиться. Попытка " + cnt + "\n");
                                boolean isSuccess = false;
                                while (!isSuccess) {
                                    isSuccess = Logic.AutoMark.mark(user.getDriver(), outputInfo);
                                    try {
                                        TimeUnit.MINUTES.sleep(3);
                                    } catch (InterruptedException e1) {
                                        new Error(e1.toString());
                                    }
                                }
                                textArea1.append("Успешно");
                            } else textArea1.append(outputInfo);
                        } else {
                            textArea1.append("\n");
                        }
                        textArea1.setCaretPosition(textArea1.getDocument().getLength());
                    });
                    TimeUnit.MINUTES.sleep(1);
                }
            } catch (Exception e) {
                new Error(e.toString());
                dispose();
            }
        }).start();
    }
}