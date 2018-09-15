/*
 * JedisHandler.java         2015年8月21日 <br/>
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

package cn.lonsun.cache.jedis;

import redis.clients.jedis.Jedis;

/**
 * jedis操作接口 <br/>
 *
 * @date 2015年8月21日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public interface JedisHandler<T> {

    public T execute(Jedis jedis);
}