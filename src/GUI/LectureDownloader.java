package GUI;

import Logic.MoodleUser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class LectureDownloader extends JFrame {
    private JPanel panel;
    private JButton downloadButton;
    private JTextField urlField;
    private JProgressBar progressBar;
    private JTextArea messageArea;
    private JButton closeBtn;

    public LectureDownloader(MoodleUser user) {
        setTitle("Lecture Downloader");
        setContentPane(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(600,400);
        setVisible(true);

        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> downloadLecture()).start();
            }
        });
        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Menu(user);
            }
        });
    }

    private void downloadLecture() {
        String lectiaUrl = urlField.getText();
        String[] urlFragments = lectiaUrl.split("/");
        if (urlFragments.length < 7) {
            appendMessage("Неправильный формат ссылки\n");
            return;
        }

        String deskshareUrl = "https://bbb.edu.vsu.ru/" + urlFragments[4] + "/" + urlFragments[6] + "/deskshare/deskshare.mp4";
        String webcamsUrl = "https://bbb.edu.vsu.ru/" + urlFragments[4] + "/" + urlFragments[6] + "/video/webcams.mp4";

        try {
            downloadFile(deskshareUrl);
            downloadFile(webcamsUrl);
        } catch (Exception e) {
            appendMessage("Ошибка: " + e.getMessage() + "\n");
        }
    }

    private void downloadFile(String fileURL) throws IOException {
        URL url = new URL(fileURL);
        HttpsURLConnection httpConn = (HttpsURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");
        int contentLength = httpConn.getContentLength();
        httpConn.connect();

        try {
            if (httpConn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                String fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
                File file = new File(fileName);
                if (file.exists()) {
                    appendMessage("Файл " + fileName + " уже существует. Перезапись не будет выполнена.\n");
                    return;
                }

                try (BufferedInputStream in = new BufferedInputStream(httpConn.getInputStream());
                     FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    byte dataBuffer[] = new byte[1024];
                    int bytesRead;
                    int totalBytesRead = 0;
                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                        totalBytesRead += bytesRead;
                        updateProgressBar(totalBytesRead, contentLength);
                    }
                    appendMessage("Файл " + fileName + " успешно загружен.\n");
                }
            } else {
                appendMessage("No file to download. Server replied HTTP code: " + httpConn.getResponseCode() + "\n");
            }
        } finally {
            httpConn.disconnect();
        }
    }

    private void updateProgressBar(int totalBytesRead, int contentLength) {
        int progress = (int) ((totalBytesRead / (double) contentLength) * 100);
        SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
    }

    private void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> messageArea.append(message));
    }

}