/*
 * PublicOrganListBeanService.java         2016年3月15日 <br/>
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

package cn.lonsun.staticcenter.generate.tag.impl.publicInfo;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogService;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.AssertUtil;
import cn.lonsun.util.PublicCatalogUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 查找信息公开公共目录列表 <br/>
 *
 * @author liukun <br/>
 * @version v1.0 <br/>
 * @date 2016年8月16日 <br/>
 */
@Component
public class PublicCatalogListBeanService extends AbstractBeanService {

    @Resource
    private IPublicCatalogService publicCatalogService;

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();
        Long organId = paramObj.getLong("organId");
        Long parentId = paramObj.getLong("parentId");
        String exceptCatIds = paramObj.getString("exceptCatIds");
        Long type = paramObj.getLong("type");

        AssertUtil.isEmpty(parentId, "父节点id不能为空！");
        List<PublicCatalogEO> list = new ArrayList<PublicCatalogEO>();
        if (type == 1) {//只查询直接子节点
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("parentId", parentId);
            map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
            list = publicCatalogService.getEntities(PublicCatalogEO.class, map);
        } else {
            list = publicCatalogService.getAllChildListByCatId(parentId);
        }
        if (null == list) {
            return Collections.emptyList();
        } else {
            if (!AppUtil.isEmpty(exceptCatIds)) {
                String[] exceptCatId = exceptCatIds.split(",");
                List exceptCatIdList = Arrays.asList(exceptCatId);
                List<PublicCatalogEO> tempList = new ArrayList<PublicCatalogEO>();
                tempList.addAll(list);
                for (PublicCatalogEO eo : tempList) {
                    if (exceptCatIdList.contains(eo.getId().toString())) {
                        list.remove(eo);
                    }
                }
            }
            Map<Long, Integer> sortNumMap = new HashMap<Long, Integer>();
            Map<Long, PublicCatalogEO> eoMap = new HashMap<Long, PublicCatalogEO>();
            for (PublicCatalogEO eo : list) {
                sortNumMap.put(eo.getId(), eo.getSortNum());
                eoMap.put(eo.getId(), eo);
            }
            list = new ArrayList<PublicCatalogEO>();
            ArrayList<Map.Entry<Long, Integer>> entries = sortMap(sortNumMap);//根据sortNum排序
            for (Map.Entry<Long, Integer> map : entries) {
                list.add(eoMap.get(map.getKey()));
            }
        }
        if (null != organId) {//过滤隐藏的目录
            PublicCatalogUtil.filterCatalogList(list, organId, true);
        }
        return list;
    }

    public static ArrayList<Map.Entry<Long, Integer>> sortMap(Map map) {
        List<Map.Entry<Long, Integer>> entries = new ArrayList<Map.Entry<Long, Integer>>(map.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<Long, Integer>>() {
            public int compare(Map.Entry<Long, Integer> obj1, Map.Entry<Long, Integer> obj2) {
                return obj1.getValue() - obj2.getValue();
            }
        });
        return (ArrayList<Map.Entry<Long, Integer>>) entries;
    }
}