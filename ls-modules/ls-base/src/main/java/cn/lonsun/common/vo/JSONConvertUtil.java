/*
 * 2014-12-14 <br/>
 * 
 */
package cn.lonsun.common.vo;

import cn.lonsun.core.exception.util.Jacksons;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.ArrayList;
import java.util.List;

public class JSONConvertUtil {
    public static String toJsonString(Object object){
        if (object != null) {
            return Jacksons.json().fromObjectToJson(object);
        }
        return "";
    }

    public static <T> T toObejct(String jsonStr, Class<T> clzz) {
        return Jacksons.json().fromJsonToObject(jsonStr, clzz);
    }

    public static <T> List<T> toObejctList(List<String> strList, Class<T> clzz) {
        String jsonStr = null;
        List<T> list = new ArrayList<T>(strList.size());
        for (String str : strList) {
            jsonStr = str;
            list.add(Jacksons.json().fromJsonToObject(str, clzz));
        }
        return list;

    }
}
