/*
 * FileEO.java         2015年8月19日 <br/>
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

package cn.lonsun.staticcenter.eo;

/**
 * 文件分发EO载体 <br/>
 *
 * @date 2015年8月19日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class FileEO {

    private String path = "";// 文件路径
    private String name = "";// 文件名
    private String content = "";// 内容

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}