/*
 * Properties.java         2016年4月13日 <br/>
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

package cn.lonsun;

import org.springframework.beans.factory.annotation.Value;

import java.util.Properties;

/**
 * 公共配置需要释放给页面使用的变量配置 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年4月13日 <br/>
 */
public class GlobalConfig extends Properties {

    /**
     * serialVersionUID:TODO.
     */
    private static final long serialVersionUID = 1L;

    public String getFileServerPath() {
        return super.getProperty("fileServerPath");
    }

    @Value("${fileServerPath}")
    public void setFileServerPath(String fileServerPath) {
        super.setProperty("fileServerPath", fileServerPath);
    }

    public String getFileServerNamePath() {
        return super.getProperty("fileServerNamePath");
    }

    @Value("${fileServerNamePath}")
    public void setFileServerNamePath(String fileServerNamePath) {
        super.setProperty("fileServerNamePath", fileServerNamePath);
    }

    public String getCotextUrl() {
        return super.getProperty("cotextUrl");
    }

    @Value("${cotextUrl}")
    public void setCotextUrl(String cotextUrl) {
        super.setProperty("cotextUrl", cotextUrl);
    }

    // IOS配置
    @Value("${baiduIOS.apiKey}")
    public void setBaiduIOSApiKey(String apikey) {
        super.setProperty("BaiduIOSApiKey", apikey);
    }

    public String getBaiduIOSApiKey() {
        return super.getProperty("BaiduIOSApiKey");
    }

    @Value("${baiduIOS.DeployStatus}")
    public void setBaiduIOSDeployStatus(String deployStatus) {
        super.setProperty("BaiduIOSDeployStatus", deployStatus);
    }

    public Integer getBaiduIOSDeployStatus() {
        return Integer.valueOf(super.getProperty("BaiduIOSDeployStatus"));
    }

    @Value("${baiduIOS.secretKey}")
    public void setBaiduIOSSecretKey(String secretKey) {
        super.setProperty("BaiduIOSSecretKey", secretKey);
    }

    public String getBaiduIOSSecretKey() {
        return super.getProperty("BaiduIOSSecretKey");
    }

    // Android 配置
    @Value("${baiduAndroid.apiKey}")
    public void setBaiduAndroidApiKey(String apikey) {
        super.setProperty("BaiduAndroidApiKey", apikey);
    }

    public String getBaiduAndroidApiKey() {
        return super.getProperty("BaiduAndroidApiKey");
    }

    @Value("${baiduAndroid.secretKey}")
    public void setBaiduAndroidSecretKey(String secretKey) {
        super.setProperty("BaiduAndroidSecretKey", secretKey);
    }

    public String getBaiduAndroidSecretKey() {
        return super.getProperty("BaiduAndroidSecretKey");
    }

    @Value("${jdbc.driverClassName}")
    public void setJdbcDriverClassName(String jdbcDriverClassName) {
        super.setProperty("jdbcDriverClassName", jdbcDriverClassName);
    }

    public String getJdbcDriverClassName() {
        return super.getProperty("jdbcDriverClassName");
    }

    @Value("${font.path}")
    public void setFontPath(String fontPath) {
        super.setProperty("font.path", fontPath);
    }

    public String getFontPath() {
        return super.getProperty("font.path");
    }

    @Value("${special.template}")
    public void setSpecialPath(String fontPath) {
        super.setProperty("special.template", fontPath);
    }

    public String getSpecialPath() {
        return super.getProperty("special.template");
    }

    @Value("${sysStaticPath}")
    public void setSysStaticPath(String path) {
        super.setProperty("sysStaticPath", path);
    }

    public String getSysStaticPath() {
        return super.getProperty("sysStaticPath");
    }

    @Value("${frontStaticPath}")
    public void setFrontStaticPath(String path) {
        super.setProperty("frontStaticPath", path);
    }

    public String getFrontSysStaticPath() {
        return super.getProperty("frontStaticPath");
    }
    public String getSoftVersion() {
        return super.getProperty("soft.version");
    }
    @Value("${soft.version}")
    public void setSoftVersion(String softVersion) {
        super.setProperty("soft.version", softVersion);
    }
}