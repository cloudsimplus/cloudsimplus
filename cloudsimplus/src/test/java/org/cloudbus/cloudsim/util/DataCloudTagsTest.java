package org.cloudbus.cloudsim.util;

import org.cloudbus.cloudsim.core.CloudSimTag;
import org.cloudbus.cloudsim.core.CloudSimTagTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Manoel Campos da Silva Filho
 */
public class DataCloudTagsTest {
    /**
     * List of constants from the class under test.
     */
    private static List<Field> constants;

    @BeforeAll
    public static void setUpClass(){
        constants = CloudSimTagTest.getDeclaredConstants(CloudSimTag.class);
    }

    /**
     * Checks if there are different constants defined with the same value.
     */
    @Test
    public void testConstantsWithSameValue(){
        for (final Field field : constants) {
            final Field anotherField = CloudSimTagTest.getAnotherConstWithSameValue(constants, field);
            final String msg = CloudSimTagTest.msgFieldsWithDuplicatedValue(field, anotherField);
            assertEquals(field, anotherField, msg);
        }
    }
}
