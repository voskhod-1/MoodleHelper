package GUI;

import Logic.ImageToAsciiConverter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;    
import java.net.URI;
import java.net.URL;
import java.net.URLStreamHandler;

class ImageSaver{
    public static BufferedImage image = null;
    public static BufferedImage binaryImage = null;
}

public class AsciiConverter extends JFrame {
    private JSlider brightSlider;
    private JSlider sharpnessSlider;
    private JButton copyBtn;
    private JTextArea resultArea;
    private JButton openBtn;
    private JPanel pane;
    private JSpinner heightSpinner;
    private JSpinner widthSpinner;
    private JComboBox comboBox1;
    private JTextField textField1;
    private JButton loadBtn;

    public AsciiConverter() {
        super("Blind Artist");
        setContentPane(pane);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setSize(500, 500);
        pack();
        JFileChooser fileChooserOpen = new JFileChooser();
        setLocationRelativeTo(null);

        loadBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println(textField1.getText());
                    ImageSaver.image = ImageIO.read(new URL(textField1.getText()));
                    updatePhoto();
                    loadBtn.setText("Загружено");
                } catch (Exception eb) {
                    System.out.println(eb.toString());
                }
            }
        });

        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePhoto();
            }
        });

        heightSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updatePhoto();
            }
        });

        widthSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updatePhoto();
            }
        });

        brightSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updatePhoto();
            }
        });

        sharpnessSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updatePhoto();
            }
        });

        openBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (fileChooserOpen.showOpenDialog(pane) == JFileChooser.APPROVE_OPTION) {
                        ImageSaver.image = ImageIO.read(new File(fileChooserOpen.getSelectedFile().getPath()));
                        updatePhoto();
                    }
                } catch (Exception eb) {
                    System.out.println(eb.toString());
                }
            }
        });
        copyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clipboard clipboard = new Clipboard("text");
                StringSelection copiedText = new StringSelection(resultArea.getText());
                clipboard.setContents(copiedText, copiedText);
            }
        });

    }

    public void updatePhoto(){
        resultArea.setText(ImageToAsciiConverter.imageToBraille(ImageSaver.image, Integer.max(1,Integer.valueOf(widthSpinner.getValue().toString())), Integer.max(1,Integer.valueOf(heightSpinner.getValue().toString())), brightSlider.getValue(), sharpnessSlider.getValue(),comboBox1.getSelectedIndex() == 0));
        pack();
    }

    public static void main(String[] args) {
        AsciiConverter asciiConverter = new AsciiConverter();
    }
}
