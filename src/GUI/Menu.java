package GUI;

import Logic.MoodleUser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JFrame {
    private JPanel MenuPanel;
    private JLabel Hello;
    private JButton autoMarkBtn;
    private JButton dwnldBtn;
    private JButton TrollMenuBtn;

    public Menu(MoodleUser user) {
        super("Menu");
        setContentPane(MenuPanel);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setSize(500, 500);
        Hello.setText("Здравствуйте, " + user.getUserName());
        pack();
        setLocationRelativeTo(null);
        autoMarkBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AMMenu(user);
                dispose();
            }
        });
        dwnldBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LectureDownloader(user);
                dispose();
            }
        });
        TrollMenuBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TrollMenuBtn.setName("Не работает");
            }
        });
    }
}


