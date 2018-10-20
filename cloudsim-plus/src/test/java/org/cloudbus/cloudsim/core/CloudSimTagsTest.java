package org.cloudbus.cloudsim.core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Manoel Campos da Silva Filho
 */
public class CloudSimTagsTest {
    /**
     * List of constants from the class under test.
     */
    private static List<Field> constants;

    @BeforeAll
    public static void setUpClass(){
        constants = getDeclaredConstants(CloudSimTags.class);
    }

    /**
     * Checks if there are different constants defined with the same value.
     */
    @Test
    public void testConstantsWithSameValue(){
        for (final Field field : constants) {
            final Field anotherField = getAnotherConstWithSameValue(constants, field);
            assertEquals(field, anotherField, msgFieldsWithDuplicatedValue(field, anotherField));
        }
    }

    public static List<Field> getDeclaredConstants(Class aClass) {
        final List<Field> list = Stream.of(aClass.getDeclaredFields())
            .filter(CloudSimTagsTest::isFieldConstant)
            .collect(Collectors.toList());

        list.forEach(CloudSimTagsTest::setAccessible);
        return list;
    }

    public static String msgFieldsWithDuplicatedValue(final Field field, final Field anotherField) {
        return "The constant " + field.getName() + " has the same value of the constant " + anotherField.getName();
    }

    /**
     * Sets a field to be accessible, enabling to get its value using Reflection.
     * @param field the field to make accessible via Reflection
     */
    private static void setAccessible(final Field field) {
        field.setAccessible(true);
    }

    /**
     * Checks if a given field is a constant or not
     * @param field the field to check
     * @return true if the field is a constant, false otherwise
     */
    private static boolean isFieldConstant(final Field field){
        return Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers());
    }

    /**
     * Try to get a constant with the same value of the given one.
     *
     * @param fields The list of Fields referring to the constants belonging to a class
     * @param field the {@link Field} object that gives access to the
     *              constant.
     * @return another {@link Field} referring to a constant with the same
     * value of the given one; or the given field if there is no other constant with
     * the same value of the given field.
     */
    public static Field getAnotherConstWithSameValue(final List<Field> fields, final Field field){
        Object fValue = null;
        Object fieldValue = null;
        for (final Field f : fields) {
            try {
                fValue = f.get(fValue);
                fieldValue = field.get(fieldValue);
                if(!f.equals(field) && fValue.equals(fieldValue)){
                    return f;
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return field;
    }

}
