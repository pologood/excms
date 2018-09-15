package cn.lonsun.util;

import java.util.UUID;

/**
 * Uuid工具类
 * Created by zhushouyong on 2017-11-28.
 */
public class UuidUtil {

    public static String getId(){
        
        return UUID.randomUUID().toString().replace("-","");
    }

}
