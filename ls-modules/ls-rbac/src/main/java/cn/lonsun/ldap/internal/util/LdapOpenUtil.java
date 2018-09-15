package cn.lonsun.ldap.internal.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by zhangchao on 2016/8/20.
 */
public class LdapOpenUtil {

    public static Boolean isOpen = false;

    private static Properties props = new Properties();

    static{
        try {
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("ldap.properties"));
            isOpen = props.getProperty("ldap.isopen").equals("1")?true:false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}