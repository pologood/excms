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

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.publicInfo.internal.dao.IPublicCatalogDao;
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
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年12月23日 <br/>
 */
@Repository("publicCatalogDao")
public class PublicCatalogDaoImpl extends MockDao<PublicCatalogEO> implements IPublicCatalogDao {

    @Override
    public void insertBySql(List<PublicCatalogEO> list) {
        String sql = "INSERT INTO cms_public_catalog " +
                "(ID, PARENT_ID, CODE, NAME, TYPE, LINK, SORT_NUM, " +
                "DESCRIPTION, RECORD_STATUS, CREATE_USER_ID, CREATE_ORGAN_ID, " +
                "CREATE_DATE, UPDATE_DATE, UPDATE_USER_ID, IS_PARENT, LEADER, " +
                "PERSON_LIABLE, PHONE, IS_SHOW, REL_CAT_IDS, REL_CAT_NAMES, UPDATE_CYCLE, " +
                "YELLOW_CARD_WARNING, RED_CARD_WARNING, ATTRIBUTE) VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        for (PublicCatalogEO eo : list) {
            if (eo.getOrganId() != null && eo.getOrganId() != 0) {
                eo.setType(2);
            }
            super.executeUpdateBySql(sql, new Object[]{
                    eo.getId(), eo.getParentId(), eo.getCode(), eo.getName(), eo.getType(), eo.getLink(), eo.getSortNum(),
                    eo.getDescription(), eo.getRecordStatus(), eo.getCreateUserId(), eo.getCreateOrganId(),
                    eo.getCreateDate(), eo.getUpdateDate(), eo.getUpdateUserId(), eo.getIsParent(), eo.getLeader(),
                    eo.getPersonLiable(), eo.getPhone(), eo.getIsShow(), eo.getRelCatIds(), eo.getRelCatNames(), eo.getUpdateCycle(),
                    eo.getYellowCardWarning(), eo.getRedCardWarning(), eo.getAttribute()
            });
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public List<PublicCatalogEO> getAllChildListByCatId(Long catId) {
        StringBuffer sql = new StringBuffer();
        sql.append("select t.id,t.parent_id parentId,t.code,t.name,t.type,t.link,t.sort_num sortNum,t.description,t.is_parent isParent");
        sql.append(",t.is_show isShow,t.leader,t.person_liable as personLiable,t.phone,t.update_cycle as updateCycle");
        sql.append(",t.yellow_card_warning as yellowCardWarning,t.red_card_warning as redCardWarning,t.attribute");
        sql.append(" from cms_public_catalog t where t.record_status = ? start with t.parent_id = ? connect by prior t.id = t.parent_id");
        sql.append(" order by t.SORT_NUM");
        String[] fileds = new String[]{"id", "parentId", "code", "name", "type", "link", "sortNum", "description"
                , "isParent", "isShow", "leader", "personLiable", "phone", "updateCycle", "yellowCardWarning", "redCardWarning", "attribute"};
        Object[] values = new Object[]{AMockEntity.RecordStatus.Normal.toString(), catId};
        return (List<PublicCatalogEO>) getBeansBySql(sql.toString(), values, PublicCatalogEO.class, fileds);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PublicCatalogEO> getAllLeafListByCatId(Long catId) {
        StringBuffer sql = new StringBuffer();
        sql.append("select t.id,t.parent_id parentId,t.code,t.name,t.type,t.link,t.sort_num sortNum,t.description,t.is_parent isParent");
        sql.append(",t.is_show isShow,t.leader,t.person_liable as personLiable,t.phone,t.update_cycle as updateCycle");
        sql.append(",t.yellow_card_warning as yellowCardWarning,t.red_card_warning as redCardWarning,t.attribute");
        sql.append(" from cms_public_catalog t where t.IS_PARENT = ? and t.record_status = ? start with t.parent_id = ? connect by prior t.id = t.parent_id");
        sql.append(" order by t.SORT_NUM");
        String[] fileds = new String[]{"id", "parentId", "code", "name", "type", "link", "sortNum", "description"
                , "isParent", "isShow", "leader", "personLiable", "phone", "updateCycle", "yellowCardWarning", "redCardWarning", "attribute"};
        Object[] values = new Object[]{BooleanUtils.toInteger(false), AMockEntity.RecordStatus.Normal.toString(), catId};
        return (List<PublicCatalogEO>) getBeansBySql(sql.toString(), values, PublicCatalogEO.class, fileds);
    }

    @Override
    public List<PublicCatalogEO> getChildListByCatId(Long catId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("parentId", catId);
        return this.getEntities(PublicCatalogEO.class, paramMap);
    }

    @Override
    public void deleteAll() {
        String hql = "delete PublicCatalogEO";
        this.executeUpdateByHql(hql, null);
    }

    @Override
    public List<Object> getAllCatIdHaveContent(Long organId,Long parentId) {
        StringBuilder sql = new StringBuilder("SELECT c.CAT_ID,c2.PARENT_ID  from CMS_PUBLIC_CONTENT c LEFT JOIN CMS_PUBLIC_CATALOG c2 ON c.CAT_ID = c2.ID WHERE c.RECORD_STATUS = ? AND c2.RECORD_STATUS = ? AND c.ORGAN_ID = ? ");
        List<Object> paramList = new ArrayList<Object>();
        paramList.add(AMockEntity.RecordStatus.Normal.toString());
        paramList.add(AMockEntity.RecordStatus.Normal.toString());
        paramList.add(organId);
        if(!AppUtil.isEmpty(parentId)){
            sql.append("and c2.PARENT_ID = ? ");
            paramList.add(parentId);
        }
        sql.append(" GROUP BY c.CAT_ID ,c2.PARENT_ID");
        return (List<Object>) this.getObjectsBySql(sql.toString(), paramList.toArray());
    }
}