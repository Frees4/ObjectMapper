package ru.hse.homework4;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

public interface Mapper {
    /**
     * Читает сохранённый экземпляр класса {@code clazz} из строки {@code input}
     * и возвращает восстановленный экземпляр класса {@code clazz}.
     * <p>
     * Пример вызова:
     *
     * <pre>
     * String input = """
     * {"comment":"Хорошая работа","resolved":false}""";
     * ReviewComment reviewComment =
     * mapper.readFromString(ReviewComment.class, input);
     * System.out.println(reviewComment);
     * </pre>
     *
     * @param clazz класс, сохранённый экземпляр которого находится в {@code input}
     * @param input строковое представление сохранённого экземпляра класса {@code
     *              clazz}
     * @param <T>   возвращаемый тип метода
     * @return восстановленный экземпляр {@code clazz}
     * @throws MapperException в случае ошибки при десериализации
     * @throws IllegalAccessException в случае ошибки при доступе к полям
     * @throws InvocationTargetException в случае ошибки рефлексивности
     * @throws NoSuchMethodException в случае отсутствия вызываемого метода
     * @throws InstantiationException в случае отсутствия конструктора без параметров
     */
    <T> T readFromString(Class<T> clazz, String input) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException, MapperException;

    /**
     * Читает объект класса {@code clazz} из {@code InputStream}'а
     * и возвращает восстановленный экземпляр класса {@code clazz}.
     * <p>
     * Данный метод закрывает {@code inputStream}.
     * <p>
     * Пример вызова:
     *
     * <pre>
     * String input = """
     * {"comment":"Хорошая работа","resolved":false}""";
     * ReviewComment reviewComment = mapper.read(ReviewComment.class,
     * new
     * ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
     * System.out.println(reviewComment);
     * </pre>
     *
     * @param clazz       класс, сохранённый экземпляр которого находится в {@code
     *                    inputStream}
     * @param inputStream поток ввода, содержащий строку в {@link
     *                    java.nio.charset.StandardCharsets#UTF_8} кодировке
     * @param <T>         возвращаемый тип метода
     * @return восстановленный экземпляр класса {@code clazz}
     * @throws IOException в случае ошибки ввода-вывода
     * @throws MapperException в случае ошибки при десериализации
     * @throws IllegalAccessException в случае ошибки при доступе к полям
     * @throws InvocationTargetException в случае ошибки рефлексивности
     * @throws NoSuchMethodException в случае отсутствия вызываемого метода
     * @throws InstantiationException в случае отсутствия конструктора без параметров
     */
    <T> T read(Class<T> clazz, InputStream inputStream) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, MapperException;

    /**
     * Читает сохранённое представление экземпляра класса {@code clazz} из {@code
     * File}'а
     * и возвращает восстановленный экземпляр класса {@code clazz}.
     * <p>
     * Пример вызова:
     *
     * <pre>
     * ReviewComment reviewComment = mapper.read(ReviewComment.class, new
     * File("/tmp/review"));
     * System.out.println(reviewComment);
     * </pre>
     *
     * @param clazz класс, сохранённый экземпляр которого находится в файле
     * @param file  файл, содержимое которого - строковое представление экземпляра
     *              {@code clazz}
     *              в {@link java.nio.charset.StandardCharsets#UTF_8} кодировке
     * @param <T>   возвращаемый тип метода
     * @return восстановленный экземпляр {@code clazz}
     * @throws IOException в случае ошибки ввода-вывода
     * @throws MapperException в случае ошибки при десериализации
     * @throws IllegalAccessException в случае ошибки при доступе к полям
     * @throws InvocationTargetException в случае ошибки рефлексивности
     * @throws NoSuchMethodException в случае отсутствия вызываемого метода
     * @throws InstantiationException в случае отсутствия конструктора без параметров
     */
    <T> T read(Class<T> clazz, File file) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, MapperException;

    /**
     * Сохраняет {@code object} в строку
     * <p>
     * Пример вызова:
     *
     * <pre>
     * ReviewComment reviewComment = new ReviewComment();
     * reviewComment.setComment("Хорошая работа");
     * reviewComment.setResolved(false);
     *
     * String string = mapper.writeToString(reviewComment);
     * System.out.println(string);
     * </pre>
     *
     * @param object объект для сохранения
     * @return строковое представление объекта в выбранном формате
     * @throws MapperException в случае ошибки при сериализации
     * @throws IllegalAccessException в случае ошибки при доступе к полям
     */
    String writeToString(Object object) throws MapperException, IllegalAccessException;

    /**
     * Сохраняет {@code object} в {@link OutputStream}.
     * <p>
     * То есть после вызова этого метода в {@link OutputStream} должны оказаться
     * байты, соответствующие строковому
     * представлению {@code object}'а в кодировке {@link
     * java.nio.charset.StandardCharsets#UTF_8}
     * <p>
     * Данный метод закрывает {@code outputStream}
     * <p>
     * Пример вызова:
     *
     * <pre>
     * ReviewComment reviewComment = new ReviewComment();
     * reviewComment.setComment("Хорошая работа");
     * reviewComment.setResolved(false);
     *
     * mapper.write(reviewComment, new FileOutputStream("/tmp/review"));
     * </pre>
     *
     * @param object объект для сохранения
     * @throws IOException в случае ошибки ввода-вывода
     * @throws MapperException в случае ошибки при сериализации
     * @throws IllegalAccessException в случае ошибки при доступе к полям
     */
    void write(Object object, OutputStream outputStream) throws IOException, MapperException, IllegalAccessException;

    /**
     * Сохраняет {@code object} в {@link File}.
     * <p>
     * То есть после вызова этого метода в {@link File} должны оказаться байты,
     * соответствующие строковому
     * представлению {@code object}'а в кодировке {@link
     * java.nio.charset.StandardCharsets#UTF_8}
     * <p>
     * Данный метод закрывает {@code outputStream}
     * <p>
     * Пример вызова:
     *
     * <pre>
     * ReviewComment reviewComment = new ReviewComment();
     * reviewComment.setComment("Хорошая работа");
     * reviewComment.setResolved(false);
     *
     * mapper.write(reviewComment, new File("/tmp/review"));
     * </pre>
     *
     * @param object объект для сохранения
     * @throws IOException в случае ошибки ввода-вывода
     * @throws MapperException в случае ошибки при сериализации
     * @throws IllegalAccessException в случае ошибки при доступе к полям
     */
    void write(Object object, File file) throws IOException, MapperException, IllegalAccessException;
}
