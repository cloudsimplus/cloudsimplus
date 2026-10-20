/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.core;

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
public class CloudSimTagTest {
    /**
     * List of constants from the class under test.
     */
    private static List<Field> constants;

    @BeforeAll
    public static void setUpClass(){
        constants = getDeclaredConstants(CloudSimTag.class);
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
            .filter(CloudSimTagTest::isFieldConstant)
            .collect(Collectors.toList());

        list.forEach(CloudSimTagTest::setAccessible);
        return list;
    }

    public static String msgFieldsWithDuplicatedValue(final Field field, final Field anotherField) {
        String value = "";
        try {
            value = ": " + field.get(CloudSim.class).toString();
        } catch (IllegalAccessException e) {
        }

        return "The constant %s has the same value of the constant %s%s"
            .formatted(field.getName(), anotherField.getName(), value);
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
