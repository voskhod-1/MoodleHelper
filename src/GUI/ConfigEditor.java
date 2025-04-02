package GUI;

import Logic.JsonIO;
import Logic.MoodleUser;
import Logic.Schedule;
import com.google.gson.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigEditor extends JFrame {
    private JList<String> weekType;
    private JPanel panel1;
    private JTree tree;
    private JList<String> dayOfWeekType;
    private JTextField timeField;
    private JButton delClassBtn;
    private JButton addClassButton;
    private JButton exitBtn;
    private JTextField lectField;

    static String[] weekTypes = {"numerator", "denominator"};
    static String[] dayOfWeekTypes = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};

    public ConfigEditor(MoodleUser user) {
        super("Config Editor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //setSize(800, 500);
        setContentPane(panel1);

        try {
            // Устанавливаем модели данных для JList
            weekType.setListData(weekTypes);
            dayOfWeekType.setListData(dayOfWeekTypes);

            Logic.Schedule schedule = Schedule.stringAsSchedule(JsonIO.readStringFromFile("classes.json"));
            tree.setModel(new javax.swing.tree.DefaultTreeModel(readStringJsonAsTree(JsonIO.readStringFromFile("classes.json"))));
            System.out.println(readStringJsonAsTree(JsonIO.readStringFromFile("classes.json")));

            List<String> pathList = new ArrayList<>();

            pack();
            setLocationRelativeTo(null);
            setVisible(true);

            // Слушатель для выбора узлов в дереве
            tree.addTreeSelectionListener(new TreeSelectionListener() {
                @Override
                public void valueChanged(TreeSelectionEvent e) {
                    // Очищаем список pathList
                    pathList.clear();

                    // Проверяем, что есть выделенный путь
                    if (tree.getSelectionPath() != null) {
                        pathList.addAll(
                                Arrays.stream(tree.getSelectionPath()
                                                .toString()
                                                .replace("[", "")
                                                .replace("]", "")
                                                .split(","))
                                        .map(String::trim)
                                        .toList()
                        );
                        System.out.println(pathList); // Выводим путь для отладки
                    } else {
                        System.out.println("Ничего не выделено");
                    }
                }
            });

            // Обработчик кнопки удаления
            delClassBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Удаление");

                    if (pathList.size() >= 5) {
                        schedule.removeClass(pathList.get(2), pathList.get(3), pathList.get(4));
                        try {
                            tree.setModel(new javax.swing.tree.DefaultTreeModel(
                                    readStringJsonAsTree(JsonIO.readStringFromFile("classes.json"))
                            ));
                            // Уведомление об успешном удалении
                            JOptionPane.showMessageDialog(ConfigEditor.this, "Занятие успешно удалено.", "Успех", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception ex) {
                            new Error(ex.toString());
                        }
                    } else {
                        JOptionPane.showMessageDialog(ConfigEditor.this, "Недостаточно данных для удаления. Проверьте выделение.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // Обработчик кнопки добавления
            addClassButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Добавление");
                    try {
                        String selectedWeekType = weekType.getSelectedValue();
                        String selectedDayOfWeek = dayOfWeekType.getSelectedValue();
                        String timeText = timeField.getText();
                        String linkText = lectField.getText();

                        if (selectedWeekType == null || selectedDayOfWeek == null || timeText.isEmpty() || linkText.isEmpty()) {
                            JOptionPane.showMessageDialog(ConfigEditor.this, "Заполните все поля", "Ошибка", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        // Проверка формата времени
                        Pattern pattern = Pattern.compile("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$");
                        Matcher matcher = pattern.matcher(timeText);
                        if (!matcher.matches()|| timeText.length() != 5) {
                            JOptionPane.showMessageDialog(ConfigEditor.this, "Неверный формат времени. Используйте HH:MM", "Ошибка", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        schedule.addClass(selectedWeekType, selectedDayOfWeek, timeText, linkText);

                        // Обновляем модель дерева
                        tree.setModel(new javax.swing.tree.DefaultTreeModel(readStringJsonAsTree(JsonIO.readStringFromFile("classes.json"))));
                        // Уведомление об успешном добавлении
                        JOptionPane.showMessageDialog(ConfigEditor.this, "Занятие успешно добавлено.", "Успех", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        new Error(ex.toString());
                    }
                }
            });


            // Обработчик кнопки выхода
            exitBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new Menu(user);
                    dispose();
                }
            });

        } catch (Exception ex) {
            new Error(ex.toString());
        }
    }
    public static DefaultMutableTreeNode readStringJsonAsTree(String jsonString) {
        try {
            JsonElement jsonElement = JsonParser.parseString(jsonString);
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("classes.json");
            parseJson(jsonElement, root);
            return root;
        } catch (Exception e) {
            throw e;
        }
    }

    private static void parseJson(JsonElement jsonElement, DefaultMutableTreeNode parent) {
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            for (String key : jsonObject.keySet()) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(key);
                parent.add(child);
                parseJson(jsonObject.get(key), child);
            }
        } else if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode("[" + i + "]");
                parent.add(child);
                parseJson(jsonArray.get(i), child);
            }
        } else if (jsonElement.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
            parent.add(new DefaultMutableTreeNode(jsonPrimitive.toString()));
        } else if (jsonElement.isJsonNull()) {
            parent.add(new DefaultMutableTreeNode("null"));
        }
    }
}