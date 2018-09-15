/*
 * GenerateRecord.java         2015年9月21日 <br/>
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

import cn.lonsun.site.label.internal.entity.LabelEO;
import cn.lonsun.staticcenter.generate.util.MessageUtil;
import org.apache.commons.lang3.RandomUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 生成记录，防止重复生成，和线程绑定，记录生成总数，已生成数，生成失败数<br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年9月21日 <br/>
 */
public class GenerateRecord {

    private String name;// 生成名称
    private boolean todb = false;// 是否入库，当任务使用，默认不入库
    // 计数、锁
    private Long total = 0L;// 总数
    private Long complete = 0L;// 完成数
    private Long error = 0L;// 失败数
    private int random = 0;// 随机数
    private Lock countLock = new ReentrantLock();// 锁
    // 异常、锁
    private Throwable exception;
    private Lock exceptionLock = new ReentrantLock();// 锁
    // 生成缓存
    private Map<Long, String> contentMap = new ConcurrentHashMap<Long, String>();// 模板内容缓存本地
    private Map<String, LabelEO> labelMap = new ConcurrentHashMap<String, LabelEO>();// 标签缓存
    private Map<String, Map<String, String>> paramMap = new ConcurrentHashMap<String, Map<String, String>>();// 标签解析参数缓存

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTodb() {
        return todb;
    }

    public void setTodb(boolean todb) {
        this.todb = todb;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getComplete() {
        return complete;
    }

    public Long getError() {
        return error;
    }

    public void complete(Throwable exception) {
        countLock.lock();
        try {
            if (null == exception) {
                complete++;
            } else {
                error++;
                this.exception(exception);
            }
            if (!this.isTodb()) {
                return;
            }
            // 任务才发送消息
            if (random-- == 0 || total.equals(complete + error)) {
                // 发送消息
                MessageUtil.sendMessage();
                // 随机数发送消息
                random = RandomUtils.nextInt(3 * 50, 3 * 200);// 这里的数字是根据每秒生成的文章数*dwr推送频率，这里估算每秒50-200篇文章
            }
        } finally {
            countLock.unlock();
        }
    }

    public void exception(Throwable exception) {
        exceptionLock.lock();
        try {
            this.exception = exception;
        } finally {
            exceptionLock.unlock();
        }
    }

    public boolean isException() {
        exceptionLock.lock();
        try {
            return null != this.exception;
        } finally {
            exceptionLock.unlock();
        }
    }

    public Throwable getException() {
        return exception;
    }

    public boolean containsKey(Long tempId) {
        return contentMap.containsKey(tempId);
    }

    public String getContent(Long tempId) {
        return contentMap.get(tempId);
    }

    public String putContent(Long tempId, String content) {
        contentMap.put(tempId, content);
        return content;
    }

    public LabelEO getLabelEO(String labelName) {
        return labelMap.get(labelName);
    }

    public LabelEO putLabelEO(String labelName, LabelEO labelEO) {
        labelMap.put(labelName, labelEO);
        return labelEO;
    }

    public Map<String, Map<String, String>> getParamMap() {
        return paramMap;
    }
}