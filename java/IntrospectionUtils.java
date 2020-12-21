import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class IntrospectionUtils {
    /**
     * Tries to get {@code field} on the given {@link Object}.<br/>
     *
     * @param o
     * @param field
     * @return
     */
    public static Object runGetter(Object o, Field field) {
        return runGetter(o, field.getName());
    }

    /**
     * Tries to get {@code fieldName} on the given {@link Object}.<br/>
     * For instance, runGetter(obj, "door") will try to run obj.getDoor() and return
     * the result or null if it failed.<br/>
     * <b>Note:</b> when using ProGuard, all objects on which this method is called
     * must NOT be obfuscated
     *
     * @param o         the {@link Object} on which to run the getter
     * @param fieldName the name of the field to get, such as get"fieldName" is a
     *                  method of the given object. The field name is case
     *                  insensitive.
     * @return
     */
    public static Object runGetter(Object o, String fieldName) {
        for (Method method : o.getClass().getMethods()) {
            if ((method.getName().startsWith("get")) && (method.getName().length() == (fieldName.length() + 3))) {
                if (method.getName().toLowerCase(Locale.getDefault()).endsWith(fieldName.toLowerCase())) {
                    try {
                        return method.invoke(o);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        System.out.println("Could not determine method: " + method.getName());
                    }
                }
            }
        }
        return null;
    }

    /**
     * Gets all {@link Field}s for a given {@link Class}.
     *
     * @param type
     * @return
     */
    public static List<Field> getAllFields(Class<?> type) {
        return _getAllFields(new LinkedList<>(), type);
    }

    /**
     * Recursive part of {@link #getAllFields(Class)}.
     *
     * @param fields
     * @param type
     * @return
     */
    private static List<Field> _getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            _getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

    public static Object getFieldValue(Object o, String fieldName) {
        try {
            final Field field = o.getClass().getField(fieldName);
            // final Class fieldClass = field.getType();
            return field.get(o);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }

    }

    /**
     * Returns a cloned {@link Object} if the parameter is {@link Cloneable}, or
     * directly the value if not.
     *
     * @param o
     * @return
     */
    public static Object cloneIfPossible(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Cloneable) {
            try {
                return o.getClass().getMethod("clone").invoke(o);
            } catch (Exception e) {
                e.printStackTrace();
                return o;
            }
        } else {
            return o;
        }
    }
}