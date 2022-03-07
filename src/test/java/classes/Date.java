package classes;

import ru.hse.homework4.DateFormat;
import ru.hse.homework4.Exported;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Exported
public class Date {
    public LocalDate localDate;

    public LocalTime localTime;

    @DateFormat("uuuu-MMMM-dd HH:mm:ss")
    public LocalDateTime localDateTime;

    public Date() {}

    public Date(LocalDate localDate, LocalTime localTime, LocalDateTime localDateTime) {
        this.localDate = localDate;
        this.localTime = localTime;
        this.localDateTime = localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
}
