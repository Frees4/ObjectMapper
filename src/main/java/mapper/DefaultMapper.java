package mapper;

import ru.hse.homework4.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStream;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DefaultMapper implements Mapper {

    @SuppressWarnings("unchecked cast")
    @Override
    public <T> T readFromString(Class<T> clazz, String input) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException, MapperException {
        String[] properties = input.split("\n");
        T instance = null;
        try {
            if (clazz.getDeclaredConstructors().length == 0) {
                throw new MapperException("Class" + clazz.getName() + "must have public default constructor");
            }
        } catch (IllegalArgumentException ex) {
            throw new MapperException("Class" + clazz.getName() + "must have public default constructor");
        }
        if (!clazz.isAnnotationPresent(Exported.class)) {
            throw new MapperException("Class " + clazz.getName() + " has no @Exported annotation");
        }
        if (!Object.class.isAssignableFrom(clazz) && !clazz.isRecord()) {
            throw new MapperException("Exported class should extends from java.lang.Object or java.lang.Record");
        }
        try {
            if (Object.class.isAssignableFrom(clazz) && !clazz.isRecord()) {
                instance = clazz.getDeclaredConstructor().newInstance();
            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new MapperException("Class" + clazz.getName() + "must have public default constructor");
        }
        List<Object> parameters;
        if (clazz.isRecord()) {
            parameters = readRecordFromString(clazz, properties);
            return (T) clazz.getDeclaredConstructors()[0].newInstance(parameters.toArray());
        } else {
            return readClassFromString(clazz, properties, instance);
        }
    }

    private <T> T readClassFromString(Class<?> clazz, String[] properties, T instance) throws MapperException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        String[] keyValue;
        Map<String, Set<String>> subClassMap = new HashMap<>();
        for (String property : properties) {
            keyValue = property.split("=");
            if (keyValue[0].contains(".")) {
                String subClass = keyValue[0].substring(0, keyValue[0].indexOf('.'));
                String subString = keyValue[0].substring(keyValue[0].indexOf('.') + 1) + '=' + keyValue[1];
                if (!subClassMap.containsKey(subClass)) {
                    subClassMap.put(subClass, new HashSet<>());
                }
                var set = subClassMap.get(subClass);
                set.add(subString);
                subClassMap.put(subClass, set);
            } else {
                boolean existKey = false;
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.getName().equals(keyValue[0]) || field.isAnnotationPresent(PropertyName.class) &&
                            field.getAnnotation(PropertyName.class).value().equals(keyValue[0])) {
                        existKey = true;
                        break;
                    }
                }
                if (!existKey) {
                    if (clazz.getAnnotation(Exported.class).unknownPropertiesPolicy() == UnknownPropertiesPolicy.FAIL) {
                        throw new MapperException(property + " is unknown property");
                    }
                }
            }
        }
        for (Field field : clazz.getDeclaredFields()) {
            boolean access = field.canAccess(instance);
            field.setAccessible(true);
            if (field.isSynthetic() || java.lang.reflect.Modifier.isStatic(field.getModifiers()) || field.isAnnotationPresent(Ignored.class)) {
                field.setAccessible(access);
                continue;
            }
            for (String property : properties) {
                keyValue = property.split("=");
                if (Objects.equals(keyValue[1], "null")) {
                    continue;
                }
                if (field.getName().equals(keyValue[0]) || field.isAnnotationPresent(PropertyName.class) &&
                        field.getAnnotation(PropertyName.class).value().equals(keyValue[0])) {
                    var type = field.getType();
                    if (type == LocalDate.class) {
                        if (field.isAnnotationPresent(DateFormat.class)) {
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(field.getAnnotation(DateFormat.class).value(), Locale.US);
                            LocalDate ld = LocalDate.parse(keyValue[1], dtf);
                            field.set(instance, ld);
                        } else {
                            field.set(instance, LocalDate.parse(keyValue[1]));
                        }
                    } else if (type == LocalDateTime.class) {
                        if (field.isAnnotationPresent(DateFormat.class)) {
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(field.getAnnotation(DateFormat.class).value(), Locale.US);
                            LocalDateTime ldt = LocalDateTime.parse(keyValue[1], dtf);
                            field.set(instance, ldt);
                        } else {
                            field.set(instance, LocalDateTime.parse(keyValue[1]));
                        }
                    } else if (type == LocalTime.class) {
                        if (field.isAnnotationPresent(DateFormat.class)) {
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(field.getAnnotation(DateFormat.class).value(), Locale.US);
                            LocalTime lt = LocalTime.parse(keyValue[1], dtf);
                            field.set(instance, lt);
                        } else {
                            field.set(instance, LocalTime.parse(keyValue[1]));
                        }
                    } else {
                        field.set(instance, parseStringElement(type, keyValue[1]));
                    }
                }
            }
            field.setAccessible(access);
        }
        StringBuilder newInputString = new StringBuilder();
        for (var subKey : subClassMap.keySet()) {
            for (var subValue : subClassMap.get(subKey)) {
                newInputString.append(subValue).append("\n");
            }
            for (Field field : clazz.getDeclaredFields()) {
                boolean access = field.canAccess(instance);
                field.setAccessible(true);
                if (field.isAnnotationPresent(Ignored.class)) {
                    field.setAccessible(access);
                    continue;
                }
                if (field.getName().equals(subKey) || field.isAnnotationPresent(PropertyName.class) &&
                        field.getAnnotation(PropertyName.class).value().equals(subKey)) {
                    var object = readFromString(field.getType(), newInputString.toString());
                    field.set(instance, object);
                }
                field.setAccessible(access);
            }
        }
        return instance;
    }

    private List<Object> readRecordFromString(Class<?> clazz, String[] properties) throws MapperException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        String[] keyValue;
        List<Object> parameters = new ArrayList<>();
        RecordComponent[] components = clazz.getRecordComponents();
        boolean assignedFlag;
        if (clazz.getAnnotation(Exported.class).unknownPropertiesPolicy() == UnknownPropertiesPolicy.FAIL) {
            for (String property : properties) {
                keyValue = property.split("=");
                boolean keyExist = false;
                for (RecordComponent component : components) {
                    if (!keyValue[0].contains(".") && component.getName().equals(keyValue[0]) || component.isAnnotationPresent(PropertyName.class) &&
                            component.getAnnotation(PropertyName.class).value().equals(keyValue[0])) {
                        keyExist = true;
                        break;
                    }
                }
                if (!keyExist) {
                    throw new MapperException(property + " is unknown property");
                }
            }
        }
        for (RecordComponent component : components) {
            if (component.isAnnotationPresent(Ignored.class)) {
                parameters.add(getDefaultValue(component.getType()));
                continue;
            }
            assignedFlag = false;
            for (String property : properties) {
                keyValue = property.split("=");
                if (keyValue[0].contains(".")) {
                    String subClass = keyValue[0].substring(0, keyValue[0].indexOf('.'));
                    String subString = keyValue[0].substring(keyValue[0].indexOf('.') + 1) + '=' + keyValue[1];
                    if (component.getName().equals(subClass) || component.isAnnotationPresent(PropertyName.class) &&
                            component.getAnnotation(PropertyName.class).value().equals(subClass)) {
                        parameters.add(readFromString(component.getType(), subString));
                        assignedFlag = true;
                    }
                }
                if (component.getName().equals(keyValue[0]) || component.isAnnotationPresent(PropertyName.class) &&
                        component.getAnnotation(PropertyName.class).value().equals(keyValue[0])) {
                    var type = component.getType();
                    if (type == LocalDate.class) {
                        if (component.isAnnotationPresent(DateFormat.class)) {
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(component.getAnnotation(DateFormat.class).value(), Locale.US);
                            LocalDate ld = LocalDate.parse(keyValue[1], dtf);
                            parameters.add(ld);
                        } else {
                            parameters.add(LocalDate.parse(keyValue[1]));
                        }
                    } else if (type == LocalDateTime.class) {
                        if (component.isAnnotationPresent(DateFormat.class)) {
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(component.getAnnotation(DateFormat.class).value(), Locale.US);
                            LocalDateTime ldt = LocalDateTime.parse(keyValue[1], dtf);
                            parameters.add(ldt);
                        } else {
                            parameters.add(LocalDateTime.parse(keyValue[1]));
                        }
                    } else if (type == LocalTime.class) {
                        if (component.isAnnotationPresent(DateFormat.class)) {
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(component.getAnnotation(DateFormat.class).value(), Locale.US);
                            LocalTime lt = LocalTime.parse(keyValue[1], dtf);
                            parameters.add(lt);
                        } else {
                            parameters.add(LocalTime.parse(keyValue[1]));
                        }
                    } else {
                        parameters.add(parseStringElement(type, keyValue[1]));
                    }
                    assignedFlag = true;
                }
            }
            if (!assignedFlag) {
                parameters.add(getDefaultValue(component.getType()));
            }
        }
        return parameters;
    }

    private Object parseStringElement(Class<?> type, String element) {
        if (int.class.isAssignableFrom(type) || type == Integer.class) {
            return Integer.parseInt(element);
        } else if (short.class.isAssignableFrom(type) || type == Short.class) {
            return Short.parseShort(element);
        } else if (long.class.isAssignableFrom(type) || type == Long.class) {
            return Long.parseLong(element);
        } else if (byte.class.isAssignableFrom(type) || type == Byte.class) {
            return Byte.parseByte(element);
        } else if (float.class.isAssignableFrom(type) || type == Float.class) {
            return Float.parseFloat(element);
        } else if (double.class.isAssignableFrom(type) || type == Double.class) {
            return Double.parseDouble(element);
        } else if (char.class.isAssignableFrom(type) || type == Character.class) {
            return element;
        } else if (boolean.class.isAssignableFrom(type) || type == Boolean.class) {
            return Boolean.parseBoolean(element);
        } else if (type.isEnum()) {
            return Enum.valueOf(type.asSubclass(Enum.class), element);
        } else if (type == List.class || type == Set.class) {
            var arrayType = type.arrayType();
            List<Object> list = new ArrayList<>();
            Set<Object> set = new HashSet<>();
            if (element.lastIndexOf('[') > 0) {
                String arraysString = element.substring(1, element.lastIndexOf(']'));
                String[] arrays = arraysString.split("], ");
                for (int i = 0; i < arrays.length - 1; i++) {
                    arrays[i] += ']';
                }
                for (String array : arrays) {
                    if (type == List.class) {
                        list.add(parseStringElement(arrayType, array));
                    } else {
                        set.add(parseStringElement(arrayType, array));
                    }
                }
            } else {
                String arraysString = element.substring(1, element.lastIndexOf(']'));
                String[] elements = arraysString.split(", ");
                for (String elem : elements) {
                    if (type == List.class) {
                        list.add(parseStringElement(arrayType, elem));
                    } else {
                        set.add(parseStringElement(arrayType, elem));
                    }
                }
            }
            if (type == List.class) {
                return list;
            }
            return set;
        } else {
            return element;
        }
    }

    @SuppressWarnings("unchecked cast")
    private static <T> T getDefaultValue(Class<T> clazz) {
        return (T) Array.get(Array.newInstance(clazz, 1), 0);
    }

    @Override
    public <T> T read(Class<T> clazz, InputStream inputStream) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, MapperException {
        byte[] bytes = inputStream.readAllBytes();
        String input = new String(bytes, StandardCharsets.UTF_8);
        T instance = readFromString(clazz, input);
        inputStream.close();
        return instance;
    }

    @Override
    public <T> T read(Class<T> clazz, File file) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, MapperException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder inputString = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            inputString.append(line).append("\n");
        }
        reader.close();
        return readFromString(clazz, inputString.toString());
    }

    private void checkIfSerializable(Object object) throws MapperException {
        Class<?> clazz = object.getClass();
        if (!clazz.isAnnotationPresent(Exported.class)) {
            throw new MapperException("Class " + clazz.getName() + " has no @Exported annotation");
        }
    }

    private String getPropertiesString(Object object, String tail) throws IllegalArgumentException, IllegalAccessException, MapperException {
        Class<?> clazz = object.getClass();
        StringBuilder propertiesString = new StringBuilder();
        for (Field field : clazz.getDeclaredFields()) {
            boolean access = field.canAccess(object);
            field.setAccessible(true);
            if (field.isSynthetic() || java.lang.reflect.Modifier.isStatic(field.getModifiers()) || field.isAnnotationPresent(Ignored.class)) {
                field.setAccessible(access);
                continue;
            }
            List<Class<?>> types = new ArrayList<>();
            Collections.addAll(types, String.class, Integer.class, Short.class, Long.class, Byte.class, Float.class,
                    Double.class, Character.class, Boolean.class, List.class, Set.class, Enum.class,
                    LocalDate.class, LocalTime.class, LocalDateTime.class);
            if (!field.getType().isPrimitive() && !types.contains(field.getType()) && !field.getType().isEnum()) {
                tail += getKey(field) + '.';
                checkIfSerializable(field.get(object));
                try {
                    propertiesString.append(getPropertiesString(field.get(object), tail));
                } catch (StackOverflowError error) {
                    throw new MapperException("Cycle detected during deserialization");
                }
                tail = "";
                continue;
            } else {
                var propertyObject = field.get(object);
                if (propertyObject == null) {
                    if (clazz.getAnnotation(Exported.class).nullHandling() == NullHandling.EXCLUDE) {
                        continue;
                    }
                }
                propertiesString.append(tail);
                propertiesString.append(getKey(field));
                propertiesString.append("=");
                if (propertyObject == null) {
                    if (clazz.getAnnotation(Exported.class).nullHandling() == NullHandling.INCLUDE) {
                        propertiesString.append("null");
                    }
                } else if (field.getType() == LocalDate.class) {
                    if (field.isAnnotationPresent(DateFormat.class)) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(field.getAnnotation(DateFormat.class).value(), Locale.US);
                        propertiesString.append(dtf.format((LocalDate) field.get(object)));
                    } else {
                        propertiesString.append(field.get(object).toString());
                    }
                } else if (field.getType() == LocalDateTime.class) {
                    if (field.isAnnotationPresent(DateFormat.class)) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(field.getAnnotation(DateFormat.class).value(), Locale.US);
                        propertiesString.append(dtf.format((LocalDateTime) field.get(object)));
                    } else {
                        propertiesString.append(field.get(object).toString());
                    }
                } else if (field.getType() == LocalTime.class) {
                    if (field.isAnnotationPresent(DateFormat.class)) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(field.getAnnotation(DateFormat.class).value(), Locale.US);
                        propertiesString.append(dtf.format((LocalTime) field.get(object)));
                    } else {
                        propertiesString.append(field.get(object).toString());
                    }
                } else {
                    propertiesString.append(field.get(object).toString());
                }
            }
            propertiesString.append("\n");
            field.setAccessible(access);
        }
        return propertiesString.toString();
    }

    private String getKey(Field field) {
        if (field.isAnnotationPresent(PropertyName.class)) {
            return field.getAnnotation(PropertyName.class).value();
        } else {
            return field.getName();
        }
    }

    @Override
    public String writeToString(Object object) throws MapperException, IllegalAccessException {
        checkIfSerializable(object);
        return getPropertiesString(object, "");
    }

    @Override
    public void write(Object object, OutputStream outputStream) throws IOException, MapperException, IllegalAccessException {
        outputStream.write(Arrays.toString(writeToString(object).getBytes(StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8));
        outputStream.close();
    }

    @Override
    public void write(Object object, File file) throws IOException, MapperException, IllegalAccessException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(Arrays.toString(writeToString(object).getBytes(StandardCharsets.UTF_8)));
        writer.close();
    }
}
