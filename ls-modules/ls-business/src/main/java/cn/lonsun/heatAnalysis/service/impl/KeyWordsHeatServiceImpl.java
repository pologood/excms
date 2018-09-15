/*
 * KeyWordsHeatServiceImpl.java         2016年4月5日 <br/>
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

package cn.lonsun.heatAnalysis.service.impl;

import javax.annotation.Resource;

import cn.lonsun.base.anno.DbInject;
import org.springframework.stereotype.Service;

import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.heatAnalysis.dao.IKeyWordsHeatDao;
import cn.lonsun.heatAnalysis.entity.KeyWordsHeatEO;
import cn.lonsun.heatAnalysis.service.IKeyWordsHeatService;

import java.util.List;

/**
 * TODO <br/>
 *
 * @date 2016年4月5日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Service
public class KeyWordsHeatServiceImpl extends BaseService<KeyWordsHeatEO> implements IKeyWordsHeatService {

    @DbInject("keyWordsHeat")
    private IKeyWordsHeatDao keyWordsHeatDao;

    @Override
    public Pagination getKeyWordsHeatPage(ContentPageVO contentPageVO) {
        return keyWordsHeatDao.getKeyWordsHeatPage(contentPageVO);
    }

    @Override
    public Pagination getKeyWordsSortPage(ContentPageVO contentPageVO) {
        return keyWordsHeatDao.getKeyWordsSortPage(contentPageVO);
    }

    @Override
    public Long saveEntity(KeyWordsHeatEO keyWordsHeatEO) {
        Long id = keyWordsHeatEO.getId();
        if (null == id) {// 新增
            super.saveEntity(keyWordsHeatEO);
        }
        super.updateEntity(keyWordsHeatEO);
        return id;
    }

    @Override
    public void saveToSort(KeyWordsHeatEO keyWordsHeatEO) {
        // 获取最小序号
        Long minNum = keyWordsHeatDao.getMinSortNum(keyWordsHeatEO.getSiteId());
        // 更新所有排序
        keyWordsHeatDao.updateSortNumBySiteId(keyWordsHeatEO.getSiteId());
        // 设置排序值
        keyWordsHeatEO.setSortNum(minNum);
        // 保存
        super.saveEntity(keyWordsHeatEO);
    }

    /**
     * 获取搜索热词列表
     * @param siteId
     *        站点ID
     * @param topCount
     *        获取条数
     * @param sort
     *        0:搜索次数降序 1:搜索次数升序
     * @return
     */
    @Override
    public List<KeyWordsHeatEO> getKeyWordsHeatList(Long siteId, Integer topCount, Integer sort) {
        return keyWordsHeatDao.getKeyWordsHeatList(siteId,topCount,sort);
    }
}