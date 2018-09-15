/*
 * AbstractFormDataService.java         2016年8月1日 <br/>
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

package cn.lonsun.form.impl;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import cn.lonsun.form.FormReturnVO;
import cn.lonsun.form.IFormDataService;

/**
 * 抽象实现 <br/>
 *
 * @date 2016年8月1日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public abstract class AbstractFormDataService implements IFormDataService {

    /**
     * map转对象
     *
     * @author fangtinghua
     * @param obj
     * @param paramMap
     */
    public <T> T mapToBean(Class<T> clazz, Map<String, Object> paramMap) {
        if (null == clazz || null == paramMap) {
            return null;
        }
        try {
            T t = clazz.newInstance();
            BeanUtils.populate(t, paramMap);
            return t;
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * bean转map
     *
     * @author fangtinghua
     * @param bean
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> beanToMap(Object bean) {
        if (null == bean) {
            return null;
        }
        try {
            return BeanUtils.describe(bean);
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 获取map参数
     *
     * @author fangtinghua
     * @param clazz
     * @param key
     * @param paramMap
     * @return
     */
    public <T> T getMapValue(Class<T> clazz, String key, Map<String, Object> paramMap) {
        if (null == paramMap || !paramMap.containsKey(key)) {
            return null;
        }
        Object obj = paramMap.get(key);
        return (null == clazz || !clazz.isInstance(obj)) ? null : clazz.cast(obj);
    }

    /**
     * 构建返回实体
     *
     * @author fangtinghua
     * @param dataId
     * @param title
     * @return
     */
    public FormReturnVO buildReturnVO(Long dataId, String title) {
        return buildReturnVO(dataId, title, null);
    }

    /**
     * 构建返回实体
     *
     * @author fangtinghua
     * @param dataId
     * @param title
     * @param data
     * @return
     */
    public FormReturnVO buildReturnVO(Long dataId, String title, Map<String, Object> data) {
        FormReturnVO vo = new FormReturnVO();
        vo.setDataId(dataId);
        vo.setTitle(title);
        vo.setData(data);
        return vo;
    }
}