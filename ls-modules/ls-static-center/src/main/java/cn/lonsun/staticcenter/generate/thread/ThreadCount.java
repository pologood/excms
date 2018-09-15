/*
 * ThreadCount.java         2016年1月26日 <br/>
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

package cn.lonsun.staticcenter.generate.thread;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程计数 <br/>
 *
 * @date 2016年1月26日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class ThreadCount {

    private static AtomicInteger count = new AtomicInteger(0);

    public static int add() {
        return count.incrementAndGet();
    }

    public static int minus() {
        return count.decrementAndGet();
    }

    public static int getCount() {
        return count.get();
    }
}