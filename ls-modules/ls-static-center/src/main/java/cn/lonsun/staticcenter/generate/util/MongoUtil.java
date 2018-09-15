/*
 * MongoUtil.java         2016年3月8日 <br/>
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

package cn.lonsun.staticcenter.generate.util;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.staticcenter.generate.CallBack;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.GenerateRecord;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;

/**
 * 获取mongodb文件内容 <br/>
 *
 * @date 2016年3月8日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class MongoUtil {
    // mongodb服务
    private static ContentMongoServiceImpl fileServer = SpringContextHolder.getBean(ContentMongoServiceImpl.class);

    /**
     * 从本地缓存中读取mongodb文件内容
     *
     * @author fangtinghua
     * @param tempId
     * @return
     * @throws GenerateException
     */
    public static String queryCacheById(Long tempId) throws GenerateException {
        return queryById(tempId, true);
    }

    /**
     * 根据id查询mongodb文件内容
     *
     * @author fangtinghua
     * @param tempId
     * @return
     * @throws GenerateException
     */
    public static String queryById(Long tempId) throws GenerateException {
        return queryById(tempId, false);
    }

    /**
     * 根据id查询mongodb文件内容
     *
     * @author fangtinghua
     * @param tempId
     * @param cache
     * @return
     * @throws GenerateException
     */
    private static String queryById(final Long tempId, boolean cache) throws GenerateException {
        Context context = ContextHolder.getContext();
        if (null == context) {// 直接查询，不做缓存
            return getContent(tempId, 0);
        }
        final GenerateRecord generateRecord = context.getGenerateRecord();
        if (cache && generateRecord.containsKey(tempId)) {
            return generateRecord.getContent(tempId);
        }
        String content = getContent(tempId, 0);// 获取内容
        return !cache ? content : LabelUtil.replaceCommonLabel(content, new CallBack<String>() {

            @Override
            public String execute(String content) throws GenerateException {
                return generateRecord.putContent(tempId, content);// 放入本地缓存
            }
        });
    }

    /**
     * 重试3次
     *
     * @author fangtinghua
     * @param tempId
     * @param retry
     * @return
     * @throws GenerateException
     */
    private static String getContent(Long tempId, int retry) throws GenerateException {
        if (retry++ >= 3) {
            throw new GenerateException("获取模板文件内容错误，模板id：" + tempId);
        }
        try {
            ContentMongoEO contentMongoEO = fileServer.queryById(tempId);
            return null != contentMongoEO ? contentMongoEO.getContent() : "";
        } catch (Throwable e) {
            return getContent(tempId, retry);
        }
    }
}