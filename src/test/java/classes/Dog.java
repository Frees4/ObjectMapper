package classes;

import ru.hse.homework4.Exported;

@Exported
public class Dog {
    String name;

    public Dog(String name) {
        this.name = name;
    }

    Dog() {}

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
