package mapper;

import classes.*;
import classes.Date;
import org.junit.jupiter.api.Test;
import ru.hse.homework4.Mapper;
import ru.hse.homework4.MapperException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MapperTest {

    @Test
    public void writeToStringShouldSerializeObjectToString() throws MapperException, IllegalAccessException {
        // Contains all supported types.
        Rectangle r = new Rectangle(3, 4, "rectangle1");
        Author a = new Author("Автор", r);
        Integer iq = 150;
        List<List<Double>> creditCard = new ArrayList<>();
        List<Double> l1 = new ArrayList<>();
        List<Double> l2 = new ArrayList<>();
        l1.add(5552.0);
        l1.add(1567.4);
        l2.add(1879.0);
        l2.add(4987.3);
        creditCard.add(l1);
        creditCard.add(l2);
        Set<String> friends = new HashSet<>();
        friends.add("Юрий");
        LocalDate localDate = LocalDate.of(2022, 2, 23);
        LocalTime localTime = LocalTime.of(23, 0);
        LocalDateTime localDateTime = LocalDateTime.of(2022, 2, 23, 23, 0);
        Person p = new Person("Обрубов", "Илья", 18, 62, a, iq, creditCard, friends, Sex.MAN, localDate, localTime, localDateTime);
        Mapper m = new DefaultMapper();
        String propertiesString = m.writeToString(p);

        String expectedString = """
                firstName=Илья
                name=Обрубов
                age=18
                author.name=Автор
                author.rec.length=3.0
                author.rec.RName=rectangle1
                iq=150
                creditCard=[[5552.0, 1567.4], [1879.0, 4987.3]]
                friends=[Юрий]
                sex=MAN
                localDate=2022-02-23
                localTime=23:00
                localDateTime=2022-02-23T23:00
                """;
        assertEquals(expectedString, propertiesString);
    }

    @Test
    public void writeShouldSerializeObjectToOutputStream() throws IOException, MapperException, IllegalAccessException {
        Rectangle r = new Rectangle(3, 4, "rectangle1");
        Author a = new Author("Автор", r);
        Mapper m = new DefaultMapper();
        String string = "name=Автор\nrec.length=3.0\nrec.RName=rectangle1\n";
        String path = "src/test/resources/outputFileStream.txt";
        OutputStream stream = new FileOutputStream(path);
        stream.write(Arrays.toString(string.getBytes(StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8));
        stream.close();

        String path2 = "src/test/resources/outputFileStream2.txt";
        OutputStream stream2 = new FileOutputStream(path2);
        m.write(a, stream2);

        byte[] file1Bytes = Files.readAllBytes(Paths.get(path));
        byte[] file2Bytes = Files.readAllBytes(Paths.get(path2));
        String fileString1 = new String(file1Bytes, StandardCharsets.UTF_8);
        String fileString2 = new String(file2Bytes, StandardCharsets.UTF_8);
        assertEquals(fileString1, fileString2);
    }

    @Test
    public void writeShouldSerializeObjectToFile() throws IOException, MapperException, IllegalAccessException {
        Rectangle r = new Rectangle(3, 4, "rectangle1");
        Author a = new Author("Автор", r);
        Mapper m = new DefaultMapper();
        String string = "name=Автор\nrec.length=3.0\nrec.RName=rectangle1\n";
        String path = "src/test/resources/outputFileStream.txt";
        File file = new File(path);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(Arrays.toString(string.getBytes(StandardCharsets.UTF_8)));
        writer.close();

        String path2 = "src/test/resources/outputFileStream2.txt";
        File file2 = new File(path2);
        m.write(a, file2);

        byte[] file1Bytes = Files.readAllBytes(Paths.get(path));
        byte[] file2Bytes = Files.readAllBytes(Paths.get(path2));
        String fileString1 = new String(file1Bytes, StandardCharsets.UTF_8);
        String fileString2 = new String(file2Bytes, StandardCharsets.UTF_8);
        assertEquals(fileString1, fileString2);
    }

    @Test
    public void readFromStringShouldDeserializeObjectFromString() throws MapperException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Rectangle r = new Rectangle(3, 0, "rectangle1");
        Author a = new Author("Автор", r);
        Mapper m = new DefaultMapper();
        String string = "name=Автор\nrec.length=3.0\nrec.RName=rectangle1\n";

        Author newAuthor = m.readFromString(Author.class, string);
        assertEquals(newAuthor.toString(), a.toString());
    }

    @Test
    public void readFromStringShouldSupportAllDeclaredTypes() throws MapperException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Rectangle r = new Rectangle(3, 0, "rectangle1");
        Author a = new Author("Автор", r);
        Integer iq = 150;
        List<List<Double>> creditCard = new ArrayList<>();
        List<Double> l1 = new ArrayList<>();
        List<Double> l2 = new ArrayList<>();
        l1.add(5552.0);
        l1.add(1567.4);
        l2.add(1879.0);
        l2.add(4987.3);
        creditCard.add(l1);
        creditCard.add(l2);
        Set<String> friends = new HashSet<>();
        friends.add("Юрий");
        LocalDate localDate = LocalDate.of(2022, 2, 23);
        LocalTime localTime = LocalTime.of(23, 0);
        LocalDateTime localDateTime = LocalDateTime.of(2022, 2, 23, 23, 0);
        Person p = new Person("Обрубов", "Илья", 18, 0, a, iq, creditCard, friends, Sex.MAN, localDate, localTime, localDateTime);
        Mapper m = new DefaultMapper();
        String input = """
                firstName=Илья
                name=Обрубов
                age=18
                author.name=Автор
                author.rec.length=3.0
                author.rec.RName=rectangle1
                iq=150
                creditCard=[[5552.0, 1567.4], [1879.0, 4987.3]]
                friends=[Юрий]
                sex=MAN
                localDate=2022-02-23
                localTime=23:00
                localDateTime=2022-02-23T23:00
                """;
        Person newP = m.readFromString(Person.class, input);
        assertEquals(p.toString(), newP.toString());
    }

    @Test
    public void readShouldDeserializeObjectFromInputStream() throws MapperException, IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Rectangle r = new Rectangle(3, 0, "rectangle1");
        Author a = new Author("Автор", r);
        Mapper m = new DefaultMapper();

        String string = "name=Автор\nrec.length=3.0\nrec.RName=rectangle1\n";
        Author newAuthor = m.read(Author.class, new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));
        assertEquals(newAuthor.toString(), a.toString());
    }

    @Test
    public void readShouldDeserializeObjectFromFile() throws MapperException, IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Rectangle r = new Rectangle(3, 0, "rectangle1");
        Author a = new Author("Автор", r);
        Mapper m = new DefaultMapper();

        Author newAuthor = m.read(Author.class, new File("src/test/resources/author.txt"));
        assertEquals(newAuthor.toString(), a.toString());
    }

    @Test
    public void classWithoutExportedAnnotationShouldThrowMapperException() {
        Book book = new Book("Илья", "Книга");
        Mapper m = new DefaultMapper();
        assertThrows(MapperException.class, () -> m.writeToString(book));

        String input = "name=Илья\nname=Книга\n";
        assertThrows(MapperException.class, () -> m.readFromString(Book.class, input));
    }

    @Test
    public void nullHandlingIncludeShouldWriteNullValues() throws MapperException, IllegalAccessException {
        Triangle triangle = new Triangle("triangle", 3, 4, 5);
        Mapper m = new DefaultMapper();

        String expected = "name=null\na=3\nb=4\nc=5\n";
        assertEquals(expected, m.writeToString(triangle));
    }

    @Test
    public void nullHandlingExcludeShouldPassNullValues() throws MapperException, IllegalAccessException {
        Rectangle rectangle = new Rectangle(3, 4, null);
        Mapper m = new DefaultMapper();

        String expected = "length=3.0\n";
        assertEquals(expected, m.writeToString(rectangle));
    }

    @Test
    public void unknownPropertiesPolicyFailShouldThrowMapperException() {
        String string = "name=Автор\nbook=Книга\nrec.length=3.0\nrec.RName=rectangle1\n";
        Mapper m = new DefaultMapper();

        assertThrows(MapperException.class, () -> m.readFromString(Author.class, string));
    }

    @Test
    public void unknownPropertiesPolicyIgnoreShouldIgnoreUnknownProperty() {
        String string = "length=3.0\nRName=rectangle1\nsquare=9\n";
        Mapper m = new DefaultMapper();

        assertDoesNotThrow(() -> m.readFromString(Rectangle.class, string));
    }

    @Test
    public void propertyNameAnnotationShouldChangeFieldKey() throws MapperException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        Rectangle rectangle = new Rectangle(3, 0, "rectangle1");
        Mapper m = new DefaultMapper();

        String expected = "length=3.0\nRName=rectangle1\n";
        assertEquals(expected, m.writeToString(rectangle));

        String input = "length=3.0\nRName=rectangle1\n";
        Rectangle rectangle1 = m.readFromString(Rectangle.class, input);
        assertEquals("rectangle1", rectangle1.name());
    }

    @Test
    public void fieldAnnotatedWithIgnoredShouldNotBeSerialized() throws MapperException, IllegalAccessException {
        Triangle triangle = new Triangle("triangle", 3, 4, 5);
        triangle.setSquare("Square");
        Mapper m = new DefaultMapper();

        String expected = "name=null\na=3\nb=4\nc=5\n";
        assertEquals(expected, m.writeToString(triangle));
    }

    @Test
    public void fieldAnnotatedWithIgnoredShouldNotBeDeserialized() throws MapperException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        Mapper m = new DefaultMapper();

        String input = "name=triangle\na=3\nb=4\nc=5\nsquare=Square\n";
        Triangle triangle1 = m.readFromString(Triangle.class, input);
        assertNull(triangle1.square);
    }

    @Test
    public void DateFormatAnnotationShouldChangeDatePatternInSerialization() throws MapperException, IllegalAccessException {
        Mapper m = new DefaultMapper();
        LocalDate ld = LocalDate.of(2022, 2, 24);
        LocalTime lt = LocalTime.of(18, 0);
        LocalDateTime ldt = LocalDateTime.of(2022, 2, 24, 18, 0);
        Date date = new Date(ld, lt, ldt);

        String expected = "localDate=2022-02-24\nlocalTime=18:00\nlocalDateTime=2022-February-24 18:00:00\n";
        assertEquals(expected, m.writeToString(date));
    }

    @Test
    public void DateFormatAnnotationShouldChangeDatePatternInDeserialization() throws MapperException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        Mapper m = new DefaultMapper();
        // LocalDateTime deserialized with pattern.
        String expected = "localDate=2022-02-24\nlocalTime=18:00\nlocalDateTime=2022-February-24 18:00:00\n";
        Date date = m.readFromString(Date.class, expected);
        assertEquals(expected, m.writeToString(date));
    }

    @Test
    public void DateFormatAnnotationOnRecordShouldChangeDatePatternInSerialization() throws MapperException, IllegalAccessException {
        Mapper m = new DefaultMapper();
        LocalDate ld = LocalDate.of(2022, 2, 24);
        LocalTime lt = LocalTime.of(18, 0);
        LocalDateTime ldt = LocalDateTime.of(2022, 2, 24, 18, 0);
        DateRecord date = new DateRecord(ld, ldt, lt);

        String expected = "localDate=2022-February-24\nlocalDateTime=2022-February-24 18:00:00\nlocalTime=18:00:00\n";
        assertEquals(expected, m.writeToString(date));
    }

    @Test
    public void DateFormatAnnotationOnRecordShouldChangeDatePatternInDeserialization() throws MapperException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        Mapper m = new DefaultMapper();
        String expected = "localDate=2022-February-24\nlocalDateTime=2022-February-24 18:00:00\nlocalTime=18:00:00\n";
        DateRecord date = m.readFromString(DateRecord.class, expected);
        assertEquals(expected, m.writeToString(date));
    }

    @Test
    public void mapperShouldThrowMapperExceptionInCycleDetectedCase() {
        Mapper m = new DefaultMapper();
        Car car = new Car();
        Model model = new Model(car);
        car.model = model;
        assertThrows(MapperException.class, () -> m.writeToString(model));
    }

    @Test
    public void mapperShouldThrowMapperExceptionIfClassExtendsByAnotherClass() {
        Mapper m = new DefaultMapper();
        Child child = new Child("Дитя");
        assertThrows(MapperException.class, () -> m.writeToString(child));
    }

    @Test
    public void mapperShouldThrowMapperExceptionIfClassHasNoPublicDefaultConstructor() {
        Mapper m = new DefaultMapper();
        String input = "name=Пес\n";
        assertThrows(MapperException.class, () -> m.readFromString(Dog.class, input));
    }
}
