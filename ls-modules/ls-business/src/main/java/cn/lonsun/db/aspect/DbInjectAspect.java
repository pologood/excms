package cn.lonsun.db.aspect;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.db.DBConfig;
import cn.lonsun.db.DBConstant;
import cn.lonsun.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 根据不同的数据库类型动态注入dao层类
 * @author gu.fei
 * @version 2016-07-14 11:19
 */
@Component
public class DbInjectAspect extends SpringContextHolder implements BeanPostProcessor,PriorityOrdered {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * sprig容器完成实例化后动态注入对象
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> cls = bean.getClass();
        for (Field field : cls.getDeclaredFields()) {
            if (field.isAnnotationPresent(DbInject.class)) {

                DbInject dbInject = field.getAnnotation(DbInject.class);

                String clazzName = "";//动态bean的名称

                String driverClass = DBConfig.getDriverClass();//数据库驱动类

                if(!AppUtil.isEmpty(driverClass)) {
                    clazzName = getBeanName(dbInject,driverClass);
                }
                logger.info("自定义注解DbInject注入数据持久层BeanName["+clazzName+"]");
                //通过反射动态注入依赖类
                try {
                    Object instance = getBean(clazzName);
                    if(null == instance){
                        instance = getImplInstanceByInterface(field);
                        if(null == instance){
                            logger.error("自定义注解DbInject注入数据持久层BeanName["+clazzName+"]出现异常,未能够创建实例");
                        }
                    }
                    ReflectionUtils.setFieldValue(bean, field.getName(),instance);
                }catch (Exception e){
                    logger.error("自定义注解DbInject注入数据持久层BeanName["+clazzName+"]出现异常");
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }


    private String getBeanName(DbInject dbInject, String driverClass){
        String clazzName = "";
        if(driverClass.equals(DBConstant.DRIVER_ORACLE)) {
            if(!AppUtil.isEmpty(dbInject.oracle())){
                clazzName = dbInject.oracle();
            }else if(!AppUtil.isEmpty(dbInject.value())){
                clazzName = dbInject.value() + "Dao";
            }
        } else if(driverClass.equals(DBConstant.DRIVER_MYSQL)) {
            if(!AppUtil.isEmpty(dbInject.mysql())){
                clazzName = dbInject.mysql();
            }else if(!AppUtil.isEmpty(dbInject.value())){
                clazzName = dbInject.value() + DBConstant.BEAN_SUFFIX_MYSQL;
            }
        } else if(driverClass.equals(DBConstant.DRIVER_MSSQL)) {
            if(!AppUtil.isEmpty(dbInject.mssql())){
                clazzName = dbInject.mssql();
            }else if(!AppUtil.isEmpty(dbInject.value())){
                clazzName = dbInject.value() + DBConstant.BEAN_SUFFIX_MSSQL;
            }
        } else if(driverClass.equals(DBConstant.DRIVER_SYBASE)) {
            if(!AppUtil.isEmpty(dbInject.sybase())){
                clazzName = dbInject.sybase();
            }else if(!AppUtil.isEmpty(dbInject.value())){
                clazzName = dbInject.value() + DBConstant.BEAN_SUFFIX_SYBASE;
            }
        } else if(driverClass.equals(DBConstant.DRIVER_DB2)) {
            if(!AppUtil.isEmpty(dbInject.db2())){
                clazzName = dbInject.db2();
            }else if(!AppUtil.isEmpty(dbInject.value())){
                clazzName = dbInject.value() + DBConstant.BEAN_SUFFIX_DB2;
            }
        }
        return clazzName;

    }

    /**
     * spring容器实例化完成前
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    /**
     * 必须
     * @return
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    /**
     * 根据接口获取接口实现类实例
     * @return
     */
    public Object getImplInstanceByInterface(Field field){
        if(null == field) return null;
        Class clazz = field.getType();
        return getInstanceByNameRule(clazz);
    }


    /**
     * 根据命名规则创建实例
     * @param c
     * @return
     */
    public Object getInstanceByNameRule(Class c){
        if(null == c) return null;
        String simpleName = c.getSimpleName();
        if(simpleName.startsWith("I")){
            simpleName = simpleName.substring(1,simpleName.length());
        }
        String clazzName = c.getPackage().getName().concat(".impl.").concat(simpleName).concat("Impl");
        Class clazz = null;
        try {
            clazz = Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            clazzName = c.getPackage().getName().concat(".imp.").concat(simpleName).concat("Impl");
            try {
                clazz = Class.forName(clazzName);
            } catch (ClassNotFoundException e1) {
                clazzName = c.getPackage().getName().concat(".impl.").concat(simpleName);
                try {
                    clazz = Class.forName(clazzName);
                } catch (ClassNotFoundException e2) {}
            }
        }
        if(null != clazz){
            try {
                return SpringContextHolder.getBean(clazz);
            } catch (Exception e) {}
        }
        return null;
    }

}
