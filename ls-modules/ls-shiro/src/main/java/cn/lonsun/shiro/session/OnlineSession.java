/*
 * OnlineSession.java         2016年6月14日 <br/>
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

package cn.lonsun.shiro.session;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.apache.shiro.session.mgt.SimpleSession;

import cn.lonsun.rbac.login.InternalAccount;

/**
 * 自定义session <br/>
 * 
 * @date 2016年6月14日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class OnlineSession extends SimpleSession {

    /**
     * serialVersionUID:TODO.
     */
    private static final long serialVersionUID = 1L;

    /**
     * 设备类型 ADD REASON. <br/>
     * 
     * @date: 2016年6月21日 下午7:30:37 <br/>
     * @author fangtinghua
     */
    public enum DeviceType {
        Android, iPhone, iPad, PC, Other
    }

    private String ip;// ip地址
    private String userAgent;// 浏览器信息
    private String deviceType = DeviceType.Other.toString();// 设备类型
    private boolean isChanged = false;// 是否改变

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    // 重写方法
    public OnlineSession() {
        super();
        this.setChanged(true);
    }

    public OnlineSession(String host) {
        super(host);
        this.setChanged(true);
    }

    @Override
    public void setId(Serializable id) {
        super.setId(id);
        this.setChanged(true);
    }

    @Override
    public void setStopTimestamp(Date stopTimestamp) {
        super.setStopTimestamp(stopTimestamp);
        this.setChanged(true);
    }

    @Override
    public void setExpired(boolean expired) {
        super.setExpired(expired);
        this.setChanged(true);
    }

    @Override
    public void setTimeout(long timeout) {
        super.setTimeout(timeout);
        this.setChanged(true);
    }

    @Override
    public void setHost(String host) {
        super.setHost(host);
        this.setChanged(true);
    }

    @Override
    public void setAttributes(Map<Object, Object> attributes) {
        super.setAttributes(attributes);
        this.setChanged(true);
    }

    @Override
    public void setAttribute(Object key, Object value) {
        super.setAttribute(key, value);
        this.setChanged(true);
    }

    @Override
    public Object removeAttribute(Object key) {
        this.setChanged(true);
        return super.removeAttribute(key);
    }

    @Override
    public void setLastAccessTime(Date lastAccessTime) {
        this.setChanged(false);
        super.setLastAccessTime(lastAccessTime);
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        super.stop();
        this.setChanged(true);
    }

    /**
     * 设置过期
     */
    @Override
    protected void expire() {
        this.stop();
        this.setExpired(true);
    }

    /**
     * 只有当不同session中的设备类型和用户id一致，返回true，其余返回false
     * 
     * @see org.apache.shiro.session.mgt.SimpleSession#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        boolean self = super.equals(obj);
        if (self) {
            return false;// 当为自己时，返回false
        }
        OnlineSession other = (OnlineSession) obj;
        Object selfId = this.getAttribute(InternalAccount.USER_USERID);// 用户id
        Object otherId = other.getAttribute(InternalAccount.USER_USERID);// 用户id
        return this.getDeviceType().equals(other.getDeviceType()) && selfId.equals(otherId);
    }
}