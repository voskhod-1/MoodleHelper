package GUI;

import Logic.MoodleUser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TrollMenu extends JFrame {
    private JPanel pane;
    private JButton asciiBtn;
    private JButton soonBtn;
    private JButton soonBtn1;


    public TrollMenu(MoodleUser user) {
        super("TrollMenu");
        setContentPane(pane);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setSize(500, 500);
        pack();
        setLocationRelativeTo(null);
        asciiBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AsciiConverter();
                dispose();
            }
        });
    }
}


