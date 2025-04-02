package GUI;

import javax.swing.*;

public class Error extends JFrame{
    private JTextArea textArea1;
    private JPanel panel1;
    private JButton closeButton;

    public Error(String message){
        super("Ошибка");
        setContentPane(panel1);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //setSize(300,200);//;
        textArea1.setText(message);
        pack();
        setLocationRelativeTo(null);
        closeButton.addActionListener(e -> {System.exit(0);});
    }

    public static void main(String[] args) {
        new Error("Ошибка");
    }
}
