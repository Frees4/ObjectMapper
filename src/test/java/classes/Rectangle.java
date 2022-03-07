package classes;

import ru.hse.homework4.Exported;
import ru.hse.homework4.Ignored;
import ru.hse.homework4.PropertyName;
import ru.hse.homework4.UnknownPropertiesPolicy;

@Exported(unknownPropertiesPolicy = UnknownPropertiesPolicy.IGNORE)
public record Rectangle(double length, @Ignored double width, @PropertyName(value = "RName") String name) {
    @Override
    public String toString() {
        return "Rectangle{" +
                "length=" + length +
                ", width=" + width +
                ", name='" + name + '\'' +
                '}';
    }
}
