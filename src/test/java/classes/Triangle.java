package classes;

import ru.hse.homework4.Exported;
import ru.hse.homework4.Ignored;
import ru.hse.homework4.NullHandling;

@Exported(nullHandling = NullHandling.INCLUDE)
public class Triangle {
    String name;
    int a;
    int b;
    int c;

    @Ignored
    public String square;

    public Triangle() {}

    public Triangle(String name, int a, int b, int c) {
        this.name = null;
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public void setSquare(String square) {
        this.square = square;
    }
}
