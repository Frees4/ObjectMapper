package classes;

import ru.hse.homework4.Exported;

@Exported
public class Author {
    public String name;

    public Rectangle rec;

    public Author() {
    }

    public Author(String name, Rectangle rec) {
        this.name = name;
        this.rec = rec;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Author{" +
                "name='" + name + '\'' +
                "rec='" + rec + '\'' +
                '}';
    }
}
