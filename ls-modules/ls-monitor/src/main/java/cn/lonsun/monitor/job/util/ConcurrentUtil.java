/*
 * ConcurrentUtil.java         2015年9月9日 <br/>
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

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * hibernate线程池新起线程获取session解决方案 <br/>
 *
 * @date 2015年9月9日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class ConcurrentUtil {

    /**
     * 
     * 绑定session到thread
     *
     * @author fangtinghua
     * @param sessionFactory
     * @return
     */
    public static boolean bindHibernateSessionToThread(SessionFactory sessionFactory) {
        if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
            return true;
        } else {
            Session session = sessionFactory.openSession();
            session.setFlushMode(FlushMode.MANUAL);
            SessionHolder sessionHolder = new SessionHolder(session);
            TransactionSynchronizationManager.bindResource(sessionFactory, sessionHolder);
        }
        return false;
    }

    /**
     * 
     * 关闭session
     *
     * @author fangtinghua
     * @param participate
     * @param sessionFactory
     */
    public static void closeHibernateSessionFromThread(boolean participate, Object sessionFactory) {
        if (!participate) {
            SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
            SessionFactoryUtils.closeSession(sessionHolder.getSession());
        }
    }
}