package classes;

import ru.hse.homework4.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Exported(unknownPropertiesPolicy = UnknownPropertiesPolicy.FAIL)
public record DateRecord(@DateFormat("uuuu-MMMM-dd")LocalDate localDate, @DateFormat("uuuu-MMMM-dd HH:mm:ss")LocalDateTime localDateTime, @DateFormat("HH:mm:ss") LocalTime localTime) {

}
