/**
 * @author DooCal
 * @ClassName: ObjectUtils
 * @Description:
 * @date 2016/1/6 09:40
 */
package cn.lonsun.util;

import org.apache.commons.beanutils.BeanUtils;

import java.util.Map;

/**
 * 对象操作工具类, 继承org.apache.commons.lang3.ObjectUtils类
 */
public class ObjectUtils extends org.apache.commons.lang3.ObjectUtils {

    //将javabean实体类转为map类型，然后返回一个map类型的值
    public static Map<String, String> beanToMap(Object bean) {
        if (null == bean) {
            return null;
        }
        try {
            return BeanUtils.describe(bean);
        } catch (Throwable e) {
            return null;
        }
    }
}
