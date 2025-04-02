package Logic;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;

public class NowDateTime {

    public static boolean isNumerator(boolean invert) {
        LocalDateTime today = LocalDateTime.now();
        int invertInt = 0;
        if (invert) invertInt = 1;
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY,1);
        int weekNumber = today.get(weekFields.weekOfYear());
        return (weekNumber + invertInt) % 2 == 0;
    }

    public static String getDayOfWeek() {
        LocalDateTime today = LocalDateTime.now();
        return today.getDayOfWeek().toString();
    }

    public static String getTime(){
        LocalDateTime today = LocalDateTime.now();
        return today.toLocalTime().toString().substring(0, 5);
    }

    public static String getWeekType(boolean invert) {
        return isNumerator(invert) ? "numerator" : "denominator";
    }
}
