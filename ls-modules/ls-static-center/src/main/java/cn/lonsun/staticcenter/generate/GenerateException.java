/*
 * GenerateException.java         2015年12月3日 <br/>
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

package cn.lonsun.staticcenter.generate;

/**
 * 异常定义 <br/>
 *
 * @date 2015年12月3日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class GenerateException extends RuntimeException {

    /**
     * serialVersionUID:TODO.
     */
    private static final long serialVersionUID = 1L;

    private Object[] param;

    public GenerateException(String message) {
        super(message);
    }

    public GenerateException(String message, Object[] param) {
        super(message);
        this.param = param;
    }

    public GenerateException(String message, Throwable e) {
        super(e instanceof GenerateException?((GenerateException) e).getMessage(false):message, e);
        if(e instanceof GenerateException){
            this.param = ((GenerateException) e).getParam();
        }
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getMessage());
        if(this.param  != null && this.param.length > 0){
            for(Object p : this.param){
                sb.append(",").append(p);
            }
        }
        return sb.toString();
    }

    public String getMessage(boolean withparam) {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getMessage());
        if(this.param  != null && this.param.length > 0 && withparam){
            for(Object p : this.param){
                sb.append(",").append(p);
            }
        }
        return sb.toString();
    }

    public GenerateException(Throwable e) {
        super(e);
    }

    public Object[] getParam() {
        return param;
    }

    public void setParam(Object[] param) {
        this.param = param;
    }
}