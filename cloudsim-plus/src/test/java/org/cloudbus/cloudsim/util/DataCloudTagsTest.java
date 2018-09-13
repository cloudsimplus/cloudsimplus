package org.cloudbus.cloudsim.util;

import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.CloudSimTagsTest;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Manoel Campos da Silva Filho
 */
public class DataCloudTagsTest {
    /**
     * List of constants from the class under test.
     */
    private static List<Field> constants;

    @BeforeClass
    public static void setUp(){
        constants = CloudSimTagsTest.getDeclaredConstants(CloudSimTags.class);
    }

    /**
     * Checks if there are different constants defined with the same value.
     */
    @Test
    public void testConstantsWithSameValue(){
        for (final Field field : constants) {
            final Field anotherField = CloudSimTagsTest.getAnotherConstWithSameValue(constants, field);
            assertEquals(CloudSimTagsTest.msgFieldsWithDuplicatedValue(field, anotherField), field, anotherField);
        }
    }

}
