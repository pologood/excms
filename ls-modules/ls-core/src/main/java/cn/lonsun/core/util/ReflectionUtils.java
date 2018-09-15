package cn.lonsun.core.util;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ReflectionUtils {
    private static final Logger log = LoggerFactory.getLogger(ReflectionUtils.class);
    @SuppressWarnings({ "rawtypes" })
    private static Map<Class, Map<String, Method>> methodCache = new HashMap<Class, Map<String, Method>>();

    private ReflectionUtils() {
    }

    /**
     * 通过反射,获得定义Class时声明的父类的范型参数的类型. 如public BookManager extends
     * GenricManager<Book>
     * 
     * @param clazz
     *            The class to introspect
     * @return the first generic declaration, or <code>Object.class</code> if
     *         cannot be determined
     */
    @SuppressWarnings({ "rawtypes" })
    public static Class getSuperClassGenricType(Class clazz) {
        return getSuperClassGenricType(clazz, 0);
    }

    /**
     * 通过反射,获得定义Class时声明的父类的范型参数的类型. 如public BookManager extends
     * GenricManager<Book>
     * 
     * @param clazz
     *            clazz The class to introspect
     * @param index
     *            the Index of the generic ddeclaration,start from 0.
     * @return the index generic declaration, or <code>Object.class</code> if
     *         cannot be determined
     */
    @SuppressWarnings({ "rawtypes" })
    public static Class getSuperClassGenricType(Class clazz, int index) {

        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            log.warn("{}'s superclass not ParameterizedType", clazz.getSimpleName());
            if (genType instanceof Class && !((Class) genType).getClass().equals(Object.class)) {
                return getSuperClassGenricType((Class) genType, index);
            } else {
                return Object.class;
            }
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            log.warn("Index: {}, Size of {}'s Parameterized Type: {}", index, clazz.getSimpleName(), params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            log.warn("{} not set the actual class on superclass generic parameter", clazz.getSimpleName());
            return Object.class;
        }
        return (Class) params[index];
    }

    public static Object getValue(Object obj, Object key) {
        if (obj == null || key == null) {
            return null;
        }

        Object v = null;
        if (obj instanceof Map) {
            v = MapUtils.getObject((Map<?, ?>) obj, key);
        } else if (obj.getClass().isArray()) {
            Object[] arr = (Object[]) obj;
            if (key instanceof Number) {
                v = arr[((Number) key).intValue()];
            } else {
                v = arr[NumberUtils.toInt(key.toString(), -1)];
            }
        } else {
            try {
                String fieldName = key.toString();
                Method method = getGetterMethod(obj.getClass(), fieldName);

                if (method != null) {
                    v = method.invoke(obj);
                } else {
                    // if(obj instanceof AbstractEntity){
                    // v = ((AbstractEntity)obj).getValue(fieldName);
                    // }
                    // @ TODO
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return v;
    }

    @SuppressWarnings({ "rawtypes", "unused" })
    private static Method getSetterMethod(Class clazz, String fieldName) {
        return getMethod(clazz, fieldName, "set");
    }

    @SuppressWarnings({ "rawtypes" })
    private static Method getGetterMethod(Class clazz, String fieldName) {
        return getMethod(clazz, fieldName, "get");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Method getMethod(Class clazz, String fieldName, String prefix) {
        Method method = null;
        try {
            String key = prefix + fieldName;
            Map<String, Method> methodMap = methodCache.get(clazz);
            if (methodMap == null) {
                methodMap = new HashMap<String, Method>();
                methodCache.put(clazz, methodMap);
            }

            if (methodMap.containsKey(key)) {// 1.先查已缓存的反射结果
                method = methodMap.get(key);
                return method;
            }

            if (method == null) {// 2.反射查找javabean规范的getter
                try {
                    method = clazz.getMethod(prefix + CamelCaseUtils.toUpperCaseFirstChar(fieldName));
                } catch (Exception e) {
                }
            }

            if (method == null) {// 3.把字段名转驼峰后，反射查找javabean规范的getter
                try {
                    method = clazz.getMethod(prefix + CamelCaseUtils.toCapitalizeCamelCase(fieldName));
                } catch (Exception e) {
                }
            }

            if (method == null) {// 4.去掉下划线（可能来自db字段定义）后，忽略字段名大小写，反射查找javabean规范的getter
                try {
                    String ignoreCaseFieldName = prefix + fieldName.replaceAll("_", "");
                    Method[] methods = clazz.getMethods();
                    if (methods != null && methods.length > 0) {
                        for (Method m : methods) {
                            if (m.getName().equalsIgnoreCase(ignoreCaseFieldName)) {
                                method = m;
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
            methodMap.put(key, method);// 把反射查找的结果缓存（可能没找到，null）
        } catch (Exception e) {
            log.error("", e);
        }
        return method;
    }

    /**
     * 获取实例，如果有单例注解，则调单例接口，否则newInstance
     * 
     * @param <T>
     * @param clazz
     * @return
     */
    public static <T> T newInstance(Class<T> clazz) {
        try {
            // Method[] methods= clazz.getDeclaredMethods();
            // for(Method method :methods){
            // Annotation[] annotations = method.getDeclaredAnnotations();
            // if(annotations!=null){
            // for(Annotation annotation:annotations ){
            // if(annotation.annotationType().equals(SingleInstance.class)){
            // return (T)method.invoke(null, null);
            // }
            // }
            // }
            // }
            return clazz.newInstance();
        } catch (Exception e) {
            log.error("class：" + clazz.getName(), e);
        }
        return null;
    }

    public static void setFieldValue(Object obj,String propertyName,Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(propertyName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }
}