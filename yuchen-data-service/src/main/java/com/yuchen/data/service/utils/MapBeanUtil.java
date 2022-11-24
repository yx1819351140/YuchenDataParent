package com.yuchen.data.service.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * map转bean，bean转map
 *
 * @classname MapBeanUtil
 * @author lizhiwei
 * @date 2019/11/26
 **/
public class MapBeanUtil {
    /**
     * 实体对象转成Map
     *
     * @param obj 实体对象
     * @return
     */
    public static Map<String, Object> object2Map(Object obj) {
        Map<String, Object> map = new HashMap<>(10);
        if (obj == null) {
            return map;
        }
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * Map转成实体对象
     *
     * @param map   实体对象包含属性
     * @param clazz 实体对象类型
     * @return
     */
    public static Object map2Object(Map<String, Object> map, Class<?> clazz) {
        if (map == null) {
            return null;
        }
        Object obj = null;
        try {
            obj = clazz.newInstance();

            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                field.setAccessible(true);
                field.set(obj, map.get(field.getName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static <T> T mapToBean(Class<T> clazz, Map<String, Object> map){
        T bean = null;
        try {
            bean = clazz.newInstance();
            for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()){
                String key = stringObjectEntry.getKey();
                Object value = stringObjectEntry.getValue();

                Field field = getClassField(clazz, key);
                if(field != null){
                    String methodName = "set" + key.substring(0,1).toUpperCase() + key.substring(1);
                    Method method = clazz.getMethod(methodName, field.getType());
                    method.invoke(bean, value);
                }
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |  InvocationTargetException e) {
            e.printStackTrace();
        }
        return bean;
    }

    private static Field getClassField(Class<?> clazz, String fieldName){
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if(declaredField.getName().equals(fieldName)){
                return declaredField;
            }
        }
        Class<?> superclass = clazz.getSuperclass();
        if(null != superclass){
            return getClassField(superclass, fieldName);
        }
        return null;
    }
}
