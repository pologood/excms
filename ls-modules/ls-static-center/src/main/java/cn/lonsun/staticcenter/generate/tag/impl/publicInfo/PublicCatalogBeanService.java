/*
 * PublicCatalogTreeBeanService.java         2015年12月29日 <br/>
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import cn.lonsun.publicInfo.vo.OrganCatalogVO;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.publicInfo.internal.entity.OrganConfigEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogOrganRelEO;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogService;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.util.PublicCatalogUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 主动公开目录 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年12月29日 <br/>
 */
@Component
public class PublicCatalogBeanService extends AbstractBeanService {

    public static final String subOrgan = "subOrgan";

    @Resource
    private IOrganService organService;
    @Resource
    private IPublicCatalogService publicCatalogService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Long siteId = paramObj.getLong(GenerateConstant.ID);// 站点id
        Map<Long, Object> resultMap = new HashMap<Long, Object>();
        // 查询出站点下所有的部门
        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, siteId);
        if (null == siteConfigEO || StringUtils.isEmpty(siteConfigEO.getUnitIds())) {
            return resultMap;
        }
        List<OrganEO> list = organService.getPublicOrgans(Long.valueOf(siteConfigEO.getUnitIds()));
        // 查询单位
        if (null == list || list.isEmpty()) {
            return resultMap;
        }
        for (OrganEO organEO : list) {
            Long organId = organEO.getOrganId();
            OrganConfigEO organConfigEO = CacheHandler.getEntity(OrganConfigEO.class, CacheGroup.CMS_ORGAN_ID, organId);
            if (null == organConfigEO || null == organConfigEO.getCatId() || !organConfigEO.getIsEnable()) {
                continue;
            }
            List<PublicCatalogEO> allChildrenList = publicCatalogService.getAllChildListByCatId(organConfigEO.getCatId());
            if (null == allChildrenList || allChildrenList.isEmpty()) {
                continue;
            }
            // 查询出单位配置的私有目录和隐藏目录
            PublicCatalogUtil.filterCatalogList(allChildrenList, organId, true);// 过滤目录
            Map<Long, Long> childNum = PublicCatalogUtil.getChildNumMap(allChildrenList);// 获取每个目录的子节点数量
            PublicCatalogUtil.sortCatalog(allChildrenList);// 排序
            List<OrganCatalogVO> organResult = new ArrayList<OrganCatalogVO>();
            for (PublicCatalogEO eo : allChildrenList) {
                OrganCatalogVO vo = new OrganCatalogVO();
                BeanUtils.copyProperties(eo, vo);
                if (StringUtils.isNotEmpty(eo.getLink())) {
                    vo.setUri(eo.getLink());
                } else {
                    vo.setUri("/public/column/" + organId + "?type=4&catId=" + eo.getId() + "&action=list");
                }
                vo.setIsParent(eo.getIsParent());
                if(vo.getIsParent() && childNum.get(eo.getId()) == null){
                    vo.setIsParent(false);
                }
                organResult.add(vo);
            }
            resultMap.put(organId, organResult);
        }
        return resultMap;
    }

    @Override
    public String objToStr(String content, Object resultObj, JSONObject paramObj) throws GenerateException {
        String result = "var catalogs = %s";
        if(paramObj.getLong(subOrgan) != null){
            Map<Long, List<PublicCatalogEO>> results = (Map<Long, List<PublicCatalogEO>>)resultObj;
            Map<Long, List<PublicCatalogEO>> catalogMap = new HashMap<Long, List<PublicCatalogEO>>();
            List<PublicCatalogEO> catalogs = results.get(paramObj.getLong(subOrgan));
            catalogMap.put(paramObj.getLong(subOrgan), catalogs);
            return String.format(result, JSONArray.toJSONString(catalogMap));
        }
        return String.format(result, JSON.toJSONString(resultObj));
    }

}