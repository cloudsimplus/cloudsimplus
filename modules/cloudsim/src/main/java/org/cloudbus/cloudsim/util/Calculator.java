package org.cloudbus.cloudsim.util;

/**
 * Implement basic math operations over values of a generic type that extends 
 * the Number class.
 * Considers a generic variable T a and T b, where the generic type T extends 
 * Number & Comparable&lt;T&gt;.
 * It isn't possible to perform basic math operations such as
 * a + b over Number values.
 * This interface defines methods to perform these basic math operations
 * over Number values.
 * 
 * @author Manoel Campos da Silva Filho <manoelcampos at gmail.com>
 * @param <T> The generic type of the values the calculator deals with
 */
public final class Calculator<T extends Number & Comparable<T>> {
    private enum Operation {add, subtract,  multiple, divide, mod, min, max};
    /**
     * Any number that is just to know what is the type
     * of the numbers the calculator will deal with.
     */
    private final T type;

    /** @see #getZero() */
    private final T zero;
    
    /**
     * Instantiates a Calculator
     * @param type any number that just to know what is the type
     * of the numbers the calculator will deal with.
     */
    public Calculator(final T type){
        this.type = type;
        this.zero = convert(0.0);
    }
    /**
     * Converts the value to a given type
     * @param value the value to be converted
     * @return the converted value
     */
    private T convert(final Double value){
        if(type instanceof Double)
            return (T)(Number)value;
        if(type instanceof Float)
            return (T)(Number)value.floatValue();
        if(type instanceof Long)
            return (T)(Number)value.longValue();
        if(type instanceof Integer)
            return (T)(Number)value.intValue();
        if(type instanceof Short)
            return (T)(Number)value.shortValue();
        if(type instanceof Byte)
            return (T)(Number)value.byteValue();
        
        throw new IllegalArgumentException(
                String.format("Operation over values of the type of %s is not supported", value));
    }
    
    private T calculate(final T a, final T b, final Operation op){
        validate(a, b);
        final Double result;
        switch(op){
            case add:      result = a.doubleValue() + b.doubleValue(); break; 
            case subtract: result = a.doubleValue() - b.doubleValue(); break; 
            case multiple: result = a.doubleValue() * b.doubleValue(); break; 
            case divide:   result = a.doubleValue() / b.doubleValue(); break; 
            case mod:      result = a.doubleValue() % b.doubleValue(); break;                 
            case min:      result = Math.min(a.doubleValue(), b.doubleValue()); break;
            case max:      result = Math.max(a.doubleValue(), b.doubleValue()); break;
            default: 
                throw new IllegalArgumentException(
                        String.format("Math operation not implemented: %s", op.name()));
        }
        
        return convert(result);
    }
    
    public T add(final T a, final T b){
        return calculate(a, b, Operation.add);
    }

    public T subtract(final T a, final T b){
        return calculate(a, b, Operation.subtract);
    }
    
    public T multiple(final T a, final T b){
        return calculate(a, b, Operation.multiple);
    }

    public T divide(final T a, final T b){
        return calculate(a, b, Operation.divide);
    }

    public T mod(final T a, final T b){
        return calculate(a, b, Operation.mod);
    }

    public T min(final T a, final T b){
        return calculate(a, b, Operation.min);
    }

    public T max(final T a, final T b){
        return calculate(a, b, Operation.max);
    }
    
    public T abs(final T a){
        return convert(Math.abs(a.doubleValue()));
    }

    public boolean isNegativeOrZero(final T value) {
        /*It is called the other method instead of making the check in a single instruction
        in order to ensure the validation.*/
        return isNegative(value) || zero.compareTo(value) == 0;
    }

    public boolean isNegative(final T value) {
        if(value == null)
            throw new IllegalArgumentException("Value cannot be null");
        return zero.compareTo(value) > 0;
    }

    private void validate(final T a, final T b) throws IllegalArgumentException {
        if(a == null)
            throw new IllegalArgumentException("The 'a' value cannot be null");
        if(b == null)
            throw new IllegalArgumentException("The 'b' value cannot be null");
    }

    /**
     * @return A generic zero used to make "comparisons with zero" throughout the class,
     * in order to avoid NullPointerException when comparing
     * null to zero. Using this attribute, we compare 
     * zero to null, avoiding exception.
     */
    public T getZero() {
        return (T) zero;
    }
}
