/*
 * TimeoutCallback.java         2016年6月2日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved. <br/>
 *
 * This software is the confidential and proprietary information of AnHui   <br/>
 * LonSun, Inc. ("Confidential Information").  You shall not    <br/>
 * disclose such Confidential Information and shall use it only in  <br/>
 * accordance with the terms of the license agreement you entered into  <br/>
 * with Sun. <br/>
 */
package cn.lonsun.shiro.map;

/**
 * 超时map回调函数 ADD REASON. <br/>
 *
 * @date: 2016年6月2日 下午3:04:14 <br/>
 * @author fangtinghua
 */
public interface TimeoutCallback<K, V> {

    /**
     * fangtinghua
     *
     * @author fangtinghua
     * @param k
     * @param v
     */
    public void onTimeout(K k, V v);
}