package cn.lonsun.mailserver;

import java.lang.reflect.Field;
import java.util.Properties;

/**
 * Created by zhusy on 2015-3-15.
 */
public class MailServerConfig {

    /**
     * sendMail 邮件服务器地址
     */
    public static  String SENDMAIL_SERVERHOST = "";

    /**
     * sendMail 邮件服务器账号
     */
    public static  String SENDMAIL_SERVERACCOUNT = "";

    /**
     * sendMail 邮件服务器密码
     */
    public static  String SENDMAIL_SERVERPASSWORD = "";


    public static Properties p = new Properties();

    /**
     * 初始化赋值
     */
    static {
        load();
    }

    /**
     * 利用反射机制自动将propertis文件属为类中属性赋值
     * TODO
     *
     * @author zhusy
     */
    public static void load() {
        try {
            p.loadFromXML(Thread.currentThread().getContextClassLoader().getResourceAsStream("sendMail-server.xml"));
            Field[] fields = MailServerConfig.class.getDeclaredFields();
            for (Field field : fields) {
                if (null != p.getProperty(field.getName())) {
                    field.set(field, p.getProperty(field.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
