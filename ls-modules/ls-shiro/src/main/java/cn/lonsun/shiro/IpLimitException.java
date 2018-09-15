/*
 * IpLimitException.java         2016年6月29日 <br/>
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

package cn.lonsun.shiro;

import org.apache.shiro.authc.AuthenticationException;

/**
 * ip限制登录异常 <br/>
 *
 * @date 2016年6月29日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class IpLimitException extends AuthenticationException {

    /**
     * serialVersionUID:TODO.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Creates a new IpLimitException.
     */
    public IpLimitException() {
        super();
    }

    /**
     * Constructs a new IpLimitException.
     *
     * @param message the reason for the exception
     */
    public IpLimitException(String message) {
        super(message);
    }

    /**
     * Constructs a new IpLimitException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public IpLimitException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new IpLimitException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public IpLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}