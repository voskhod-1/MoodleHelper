package Logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class CreateClassesJson {

    public static void check() {
        String filePath = "classes.json";

        File file = new File(filePath);

        if (!file.exists()) {
            try {
                // Создаем пустой JSON объект
                Map<String, Object> data = new HashMap<>();
                Files.write(file.toPath(), data.toString().getBytes());
                System.out.println(filePath + " создан.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(filePath + " уже существует.");
        }
    }
}