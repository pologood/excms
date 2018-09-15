
package cn.lonsun.common.util;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 *@date 2014-12-16 11:15  <br/>
 *@author zhusy  <br/>
 *@version v1.0  <br/>
 */
public class ClassInvokeUtil {

    private static Class<?> loadClass (String fullClassName) {
        Class<?> clazz;
        try {
            clazz = Class.forName(fullClassName);
        } catch (ClassNotFoundException e) {
            throw new BaseRunTimeException(TipsMode.Message.toString(),"无法加载反射类");
        }
        return clazz;
    }

    private static Object newInstance(Class<?> clazz) {
        Object target;
        try {
            target = clazz.newInstance();
        } catch (Exception e) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "反射调用类实例化失败");
        }
        return target;
    }


    public static Object execute(String fullClassName,String methodName,Class<?>[] classes,Object[] args){
        Object returnObject = null;
        Class<?> clazz = loadClass(fullClassName);
        Object  target = newInstance(clazz);
        Method method = ReflectionUtils.findMethod(clazz, methodName, classes);
        if (method != null) {
            returnObject = ReflectionUtils.invokeMethod(method,target, args);
        } else {
            throw new BaseRunTimeException(TipsMode.Message.toString(),"反射调用未找到指定的调用方法");
        }
        return returnObject;
    }



}
