package Logic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.*;

public class Schedule {

    private Map<String, Map<String, Map<String, ClassInfo>>> classes;

    public Map<String, Map<String, Map<String, ClassInfo>>> getPeriod() {
        return classes;
    }

    public Map<String, Map<String, ClassInfo>> getWeek(String weekType) {
        weekType = weekType.toLowerCase();
        return classes.get(weekType);
    }

    public Map<String, ClassInfo> getDay(String weekType, String dayType) {
        weekType = weekType.toLowerCase();
        dayType = dayType.toUpperCase();
        return classes.get(weekType).get(dayType);
    }

    public void setClasses(Map<String, Map<String, Map<String, ClassInfo>>> classes) {
        this.classes = classes;
    }

    public List<String> getAllDays(String weekType) {
        weekType = weekType.toLowerCase();
        if (classes == null || !classes.containsKey(weekType)) {
            return Collections.emptyList();
        }
        Set<String> allDays = new HashSet<>(classes.get(weekType).keySet());
        return allDays.stream().map(String::toUpperCase).toList();
    }

    public List<String> getAllTime(String weekType, String dayOfWeek) {
        weekType = weekType.toLowerCase();
        dayOfWeek = dayOfWeek.toUpperCase();
        if (classes == null || !classes.containsKey(weekType) || !classes.get(weekType).containsKey(dayOfWeek)) {
            return Collections.emptyList();
        }
        Set<String> allTime = new HashSet<>(classes.get(weekType).get(dayOfWeek).keySet());
        return allTime.stream().toList();
    }

    public ClassInfo getNowClassInfo(String weekType, String dayOfWeek, String time) {
        weekType = weekType.toLowerCase();
        dayOfWeek = dayOfWeek.toUpperCase();
        time = time.trim();
        if (classes != null && getAllDays(weekType).contains(dayOfWeek) && getAllTime(weekType, dayOfWeek).contains(time)) {
            return classes.get(weekType).get(dayOfWeek).get(time);
        }
        return null;
    }

    public boolean removeClass(String weekType, String dayOfWeek, String time) {
        weekType = weekType.toLowerCase();
        dayOfWeek = dayOfWeek.toUpperCase();
        time = time.trim();

        if (classes == null) {
            System.out.printf("Расписание не инициализировано.\n");
            return false;
        }

        Map<String, Map<String, ClassInfo>> week = classes.get(weekType);
        if (week == null) {
            System.out.printf("Тип недели %s не найден.\n", weekType);
            return false;
        }

        Map<String, ClassInfo> day = week.get(dayOfWeek);
        if (day == null) {
            System.out.printf("День %s не найден.\n", dayOfWeek);
            return false;
        }

        if (!day.containsKey(time)) {
            System.out.printf("Занятие в %s на %s (%s) не найдено.\n", dayOfWeek, time, weekType);
            return false;
        }

        day.remove(time);
        System.out.printf("Занятие в %s на %s (%s) удалено.\n", dayOfWeek, time, weekType);

        if (day.isEmpty()) {
            week.remove(dayOfWeek);
            System.out.printf("День %s удален, так как больше не содержит занятий.\n", dayOfWeek);
        }

        if (week.isEmpty()) {
            classes.remove(weekType);
            System.out.printf("Тип недели %s удален, так как больше не содержит дней.\n", weekType);
        }

        try {
            JsonIO.writeStringToFile(this.saveClassesToString(), "classes.json");
            System.out.println("Сохранено");
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean addClass(String weekType, String dayOfWeek, String time, String url) {
        weekType = weekType.toLowerCase();
        dayOfWeek = dayOfWeek.toUpperCase();
        time = time.trim();

        if (classes == null) {
            classes = new HashMap<>();
        }

        if (classes.containsKey(weekType) && classes.get(weekType).containsKey(dayOfWeek) && classes.get(weekType).get(dayOfWeek).containsKey(time)) {
            System.out.printf("Ошибка: В %s на %s (%s) уже есть занятие: %s.\n",
                    dayOfWeek, time, weekType, classes.get(weekType).get(dayOfWeek).get(time).getUrl());
            return false;
        }

        classes.computeIfAbsent(weekType, k -> new HashMap<>())
                .computeIfAbsent(dayOfWeek, k -> new HashMap<>())
                .put(time, new ClassInfo(url));

        System.out.printf("Занятие \"%s\" добавлено в %s на %s (%s).\n", url, dayOfWeek, time, weekType);

        try {
            JsonIO.writeStringToFile(this.saveClassesToString(), "classes.json");
            System.out.println("Сохранено");
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении: " + e.getMessage());
            return false;
        }
        return true;
    }

    private String saveClassesToString() {
        if (classes == null) {
            return "{}";
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            String output = "{\n\t\"classes\": " + gson.toJson(classes) + "\n}";
            System.out.println(output);
            return output;
        } catch (Exception e) {
            System.err.println("Ошибка при сохранении: " + e.getMessage());
            return "";
        }
    }

    public static Schedule stringAsSchedule(String data) throws com.google.gson.JsonSyntaxException {
        Gson gson = new Gson();
        Schedule schedule = gson.fromJson(data, Schedule.class);
        return schedule;
    }

    public static void main(String[] args) {
        try {
            Schedule s = stringAsSchedule(JsonIO.readStringFromFile("classes.json"));
            System.out.println(s.getNowClassInfo("numeratOr", "monday", "13:25").getUrl());
        } catch (IOException | com.google.gson.JsonSyntaxException e) {
            e.printStackTrace();
        }
    }


    public static class ClassInfo {
        private String url;
        public ClassInfo(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

    }
}