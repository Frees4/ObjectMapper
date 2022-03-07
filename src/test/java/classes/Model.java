package classes;

import ru.hse.homework4.Exported;

@Exported
public class Model {
    Car car;
    public Model() {}
    public Model(Car car) {
        this.car = car;
    }
    public Car car() {
        return car;
    }
}
