package classes;

import ru.hse.homework4.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Exported()
public class Person {

    private String firstName;

    @PropertyName(value = "name")
    public String lastName;

    public int age;

    public Author author;

    public Integer iq;

    public List<List<Double>> creditCard;

    public Set<String> friends;

    public Sex sex;

    public LocalDate localDate;

    public LocalTime localTime;

    public LocalDateTime localDateTime;

    @Ignored
    public int weight;

    public Person() {
    }

    public Person(String lastname, String firstname, int age, int weight, Author author, Integer iq,
                  List<List<Double>> creditCard, Set<String> friends, Sex sex, LocalDate localDate, LocalTime localTime,
                  LocalDateTime localDateTime) {
        this.lastName = lastname;
        this.firstName = firstname;
        this.age = age;
        this.weight = weight;
        this.author = author;
        this.iq = iq;
        this.creditCard = creditCard;
        this.friends = friends;
        this.sex = sex;
        this.localDate = localDate;
        this.localTime = localTime;
        this.localDateTime = localDateTime;
    }

    public int getAge() {
        return age;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }


    public String getLastname() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Person{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", author=" + author +
                ", iq=" + iq +
                ", creditCard=" + creditCard +
                ", friends=" + friends +
                ", sex=" + sex +
                ", localDate=" + localDate +
                ", localTime=" + localTime +
                ", localDateTime=" + localDateTime +
                ", weight=" + weight +
                '}';
    }
}
