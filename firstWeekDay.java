import java.time.*;
import java.time.temporal.*;

public class firstWeekDay {
    public static void main(String[] args) {
        LocalDate today = LocalDate.now();
        LocalDate ca = today.with(TemporalAdjusters.firstDayOfNextMonth());
        LocalDate next_wednesday=ca.with(TemporalAdjusters.nextorSame(DayOfWeek.WEDNESDAY));
        System.out.println(next_wednesday);
    }
}
