package cn.lonsun.base.anno;

import java.lang.annotation.*;

/**
 * @author gu.fei
 * @version 2016-07-14 11:16
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbInject {

    //默认
    public String value() default "";

    //Orcale数据库
    public String oracle() default "";

    //Mysql数据库
    public String mysql() default "";

    //Sqlserver数据库 版本：2000/2005
    public String mssql() default "";

    //Sybase数据库
    public String sybase() default "";

    //Db2数据库
    public String db2() default "";
}
