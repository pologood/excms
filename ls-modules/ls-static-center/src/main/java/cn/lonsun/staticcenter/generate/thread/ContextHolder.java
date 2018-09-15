/*
 * ColumnContextHolder.java         2015年9月8日 <br/>
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

/**
 * 上下文环境<br/>
 *
 * @date 2015年9月8日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class ContextHolder {

    private static final ThreadLocal<Context> contextHolder = new ThreadLocal<Context>();

    public static void setContext(Context context) {
        contextHolder.set(context);
    }

    public static Context getContext() {
        return contextHolder.get();
    }

    public static void clearContext() {
        contextHolder.remove();
    }
}