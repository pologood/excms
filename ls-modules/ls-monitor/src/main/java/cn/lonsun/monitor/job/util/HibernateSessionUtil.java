/*
 * HibernateSessionUtil.java         2016年3月2日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved.	<br/>
 *
 * This software is the confidential and proprietary information of AnHui	<br/>
 * LonSun, Inc. ("Confidential Information").  You shall not	<br/>
 * disclose such Confidential Information and shall use it only in	<br/>
 * accordance with the terms of the license agreement you entered into	<br/>
 * with Sun. <br/>
 */

package cn.lonsun.monitor.job.util;

import cn.lonsun.core.util.SpringContextHolder;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate线程内调用工具类 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年3月2日 <br/>
 */
public class HibernateSessionUtil {

    // 日志
    private static final Logger logger = LoggerFactory.getLogger(HibernateSessionUtil.class);

    /**
     * 方法调用
     *
     * @param hibernateHandler
     * @author fangtinghua
     */
    public static <T> T execute(HibernateHandler<T> hibernateHandler) {
        T result = null;// 结果对象
        Throwable exception = null;// 异常
        // 绑定session至当前线程中
        SessionFactory sessionFactory = SpringContextHolder.getBean(SessionFactory.class);
        boolean participate = ConcurrentUtil.bindHibernateSessionToThread(sessionFactory);
        try {
            result = hibernateHandler.execute();
        } catch (Throwable e) {
            exception = e;
            logger.error("hibernate调用失败.", e);// 失败打印日志
        } finally {// 关闭session
            ConcurrentUtil.closeHibernateSessionFromThread(participate, sessionFactory);
        }
        return hibernateHandler.complete(result, exception);
    }

    /**
     * 获取异常堆栈信息
     *
     * @param e
     * @param clazz
     * @return
     * @author fangtinghua
     */
    public static String getStackTrace(Throwable e, Class<?> clazz) {
        // 判断是否为生成异常
        if (null != clazz && clazz.isInstance(e)) {// 如果是指定异常类，返回异常类message
            return e.getMessage();
        }
        return ExceptionUtils.getFullStackTrace(e);
    }
}