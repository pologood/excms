package cn.lonsun.util;

/**
 * @author Hewbing
 * @ClassName: ArrayFormat
 * @Description: 数组格式化
 * @date 2015年10月10日 下午3:28:42
 */
public class ArrayFormat {
    public static String ArrayToString(Object[] arr) {
        String str = "";
        if (arr == null || arr.length == 0) {
            return str;
        }
        for (int i = 0; i < arr.length - 1; i++) {
            str += arr[i] + ",";
        }
        return str + arr[arr.length - 1];
    }

}
