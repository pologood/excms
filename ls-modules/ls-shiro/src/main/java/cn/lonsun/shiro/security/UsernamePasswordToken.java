/*
 * UsernamePasswordToken.java         2016年6月14日 <br/>
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

package cn.lonsun.shiro.security;

/**
 * token <br/>
 *
 * @date 2016年6月14日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class UsernamePasswordToken extends org.apache.shiro.authc.UsernamePasswordToken {

    /**
     * serialVersionUID:TODO.
     */
    private static final long serialVersionUID = 1L;

    private String code;// 验证码

    private  String geetest_challenge;
    private String geetest_validate;
    private String geetest_seccode;
    public UsernamePasswordToken(final String username, final String password, final String geetest_challenge, final  String geetest_validate,final  String geetest_seccode) {
        super(username, password);
        this.geetest_challenge=geetest_challenge;
        this.geetest_validate=geetest_validate;
        this.geetest_seccode=geetest_seccode;
    }

    public UsernamePasswordToken(final String username, final String password, final String code) {
        super(username, password);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGeetest_challenge() {
        return geetest_challenge;
    }

    public void setGeetest_challenge(String geetest_challenge) {
        this.geetest_challenge = geetest_challenge;
    }

    public String getGeetest_validate() {
        return geetest_validate;
    }

    public void setGeetest_validate(String geetest_validate) {
        this.geetest_validate = geetest_validate;
    }

    public String getGeetest_seccode() {
        return geetest_seccode;
    }

    public void setGeetest_seccode(String geetest_seccode) {
        this.geetest_seccode = geetest_seccode;
    }
}