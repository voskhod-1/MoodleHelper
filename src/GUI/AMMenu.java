package GUI;

import Logic.MoodleUser;
import Logic.NowDateTime;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AMMenu extends JFrame {
    private JButton startBtn;
    private JButton cfgSchedBtn;
    private JPanel panel1;
    private JCheckBox invertChB;
    private JLabel nowWeekTypeLabel;

    public AMMenu(MoodleUser user) {
        setContentPane(panel1);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //setSize(400, 300);
        pack();
        setLocationRelativeTo(null);
        nowWeekTypeLabel.setText("Сейчас: " + NowDateTime.getWeekType(false));
        invertChB.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                nowWeekTypeLabel.setText("Сейчас: " + NowDateTime.getWeekType(invertChB.isSelected()));
            }
        });
        Logic.CreateClassesJson.check();
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new AutoMark(user,invertChB.isSelected());
            }
        });
        cfgSchedBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new ConfigEditor(user);
            }
        });
    }
}
