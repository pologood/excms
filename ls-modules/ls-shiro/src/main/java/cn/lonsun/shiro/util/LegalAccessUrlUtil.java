package cn.lonsun.shiro.util;

import cn.lonsun.core.base.util.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by zhangchao on 2017/1/19.
 */
public class LegalAccessUrlUtil {

    private static Properties properties = new Properties();

    static {
        try {
            Resource resource = new ClassPathResource("legalAccessUrls.properties");
            properties.load(resource.getInputStream());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否有参数value
     *
     * @param value
     * @return
     */
    public static boolean containsValue(String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        return properties.containsValue(value);
    }
}
