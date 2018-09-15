/*
 * PublicCatalogDaoImpl.java         2015年12月23日 <br/>
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

package cn.lonsun.publicInfo.internal.dao.impl;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO <br/>
 *
 * @author liukun <br/>
 * @version v1.0 <br/>
 * @date 2016年8月26日 <br/>
 */
@Repository("publicCatalogMySqlDao")
public class PublicCatalogMySqlDaoImpl extends PublicCatalogDaoImpl {

    private String getSql(boolean path) {
        StringBuffer sql = new StringBuffer();
        if (path) {
            sql.append(" select id as id,paths as paths ");
        } else {
            sql.append(" select id as id,concat(paths, ',', id) as likeStr ");
        }
        sql.append(" from (select id, ");
        //sql.append(" @le \\:= IF (parent_id = 0,0,IF (LOCATE(CONCAT('|', parent_id, '\\:') ,@pathlevel) > 0, ");
        //sql.append(" SUBSTRING_INDEX(SUBSTRING_INDEX(@pathlevel, CONCAT('|', parent_id, '\\:') ,-1),'|',1) + 1 ,@le + 1)) levels, ");
        //sql.append(" @pathlevel \\:= CONCAT( @pathlevel, '|', id, '\\:', @le, '|') pathlevel, ");
        sql.append(" @pathnodes \\:= IF (parent_id = 0,0,CONCAT_WS(',',IF (LOCATE(CONCAT('|', parent_id, '\\:') ,@pathall) > 0, ");
        sql.append(" SUBSTRING_INDEX(SUBSTRING_INDEX(@pathall,CONCAT('|', parent_id, '\\:') ,-1),'|',1) ,@pathnodes), parent_id)) paths, ");
        sql.append(" @pathall \\:= CONCAT(@pathall,'|',id,'\\:',@pathnodes,'|') pathall from cms_public_catalog,(SELECT @le \\:= 0 , ");
        //sql.append(" @pathlevel \\:= '',");
        sql.append(" @pathall \\:= '' ,@pathnodes \\:= '') vv ");
        sql.append(" order by parent_id, id) src ");
        return sql.toString();
    }

    private void getChildListByCatId(List<PublicCatalogEO> resultList, Long catId) {
        List<PublicCatalogEO> catalogEOList = CacheHandler.getList(PublicCatalogEO.class, CacheGroup.CMS_PARENTID, catId);
        if (null != catalogEOList && !catalogEOList.isEmpty()) {
            for (PublicCatalogEO eo : catalogEOList) {
                resultList.add(eo);
                // if (eo.getIsParent()) { 去掉这一句话，防止源目录为false，单位自己修改而目录没有修改的情况导致无限展开
                getChildListByCatId(resultList, eo.getId());
                // }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PublicCatalogEO> getAllChildListByCatId(Long catId) {
        List<PublicCatalogEO> catalogEOList = new ArrayList<PublicCatalogEO>();
        this.getChildListByCatId(catalogEOList, catId);
        return catalogEOList;
        /*StringBuffer sql = new StringBuffer();
        sql.append("select t.id,t.parent_id parentId,t.code,t.name,t.type,t.link,t.sort_num sortNum,t.description,t.is_parent isParent ");
        sql.append(" from cms_public_catalog t left join (").append(this.getSql(true)).append(") temp on t.id = temp.id where t.record_status = ? ");
        sql.append(" and temp.paths like CONCAT((select likeStr from (").append(this.getSql(false)).append(") likeTemp where likeTemp.id = ?),'%') order by t.sort_num ");
        String[] fileds = new String[]{"id", "parentId", "code", "name", "type", "link", "sortNum", "description", "isParent"};
        Object[] values = new Object[]{AMockEntity.RecordStatus.Normal.toString(), catId};
        return (List<PublicCatalogEO>) getBeansBySql(sql.toString(), values, PublicCatalogEO.class, fileds);*/
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PublicCatalogEO> getAllLeafListByCatId(Long catId) {
//        StringBuffer sql = new StringBuffer();
//        sql.append("select t.id,t.parent_id parentId,t.code,t.name,t.type,t.link,t.sort_num sortNum,t.description,t.is_parent isParent ");
//        sql.append(" from cms_public_catalog t left join (").append(this.getSql(true)).append(") temp on t.id = temp.id where t.is_parent = ? and t.record_status = ? ");
//        sql.append(" and temp.paths like CONCAT((select likeStr from (").append(this.getSql(false)).append(") likeTemp where likeTemp.id = ?),'%') order by t.sort_num ");
//        String[] fileds = new String[]{"id", "parentId", "code", "name", "type", "link", "sortNum", "description", "isParent"};
//        Object[] values = new Object[]{BooleanUtils.toInteger(false), AMockEntity.RecordStatus.Normal.toString(), catId};
//        return (List<PublicCatalogEO>) getBeansBySql(sql.toString(), values, PublicCatalogEO.class, fileds);

        List<PublicCatalogEO> catalogEOList = new ArrayList<PublicCatalogEO>();
        this.getChildListByCatId(catalogEOList, catId, true);
        return catalogEOList;
    }

    /**
     * 获取该目录下所有子节点或者获取所有叶子节点
     *
     * @param resultList 列表
     * @param catId      目录id
     * @param queryLeaf  是否只获取叶子目录
     */
    private void getChildListByCatId(List<PublicCatalogEO> resultList, Long catId, boolean queryLeaf) {
        List<PublicCatalogEO> catalogEOList = CacheHandler.getList(PublicCatalogEO.class, CacheGroup.CMS_PARENTID, catId);
        if (null != catalogEOList && !catalogEOList.isEmpty()) {
            for (PublicCatalogEO eo : catalogEOList) {
                if (!queryLeaf || !eo.getIsParent()) {// 不取叶子节点或者取时isParent为false
                    resultList.add(eo);
                }
                getChildListByCatId(resultList, eo.getId(), queryLeaf);
            }
        }
    }


    @Override
    public void deleteAll() {
        String hql = "delete PublicCatalogEO";
        this.executeUpdateByHql(hql, null);
    }
}