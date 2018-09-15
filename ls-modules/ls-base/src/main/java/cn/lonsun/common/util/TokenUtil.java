package cn.lonsun.common.util;

import java.io.IOException;

/**
 * Created by Dzl on 2015-1-18.
 */
public class TokenUtil {
    /**
     * 生成Token， A(hashcode>0)|B + |name的Hash值| +_+size的值
     *
     * @param name
     * @param size
     * @return
     * @throws Exception
     */
    public static String generateToken(String name, String size)
            throws IOException {
        if (name == null || size == null)
            return "";
        int code = name.hashCode();
        try {
            String token = (code > 0 ? "A" : "B") + Math.abs(code) + "_" + size.trim();
            /** TODO: store your token, here just create a file */
             return token;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
