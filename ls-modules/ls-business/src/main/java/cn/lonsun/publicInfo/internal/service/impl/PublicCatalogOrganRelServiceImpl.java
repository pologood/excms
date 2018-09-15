/*
 * PublicCatalogOrganRelServiceImpl.java         2015年12月7日 <br/>
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

package cn.lonsun.publicInfo.internal.service.impl;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.publicInfo.internal.dao.IPublicCatalogOrganRelDao;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogOrganRelEO;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogOrganRelService;
import cn.lonsun.util.PublicCatalogUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年12月7日 <br/>
 */
@Component
public class PublicCatalogOrganRelServiceImpl extends MockService<PublicCatalogOrganRelEO> implements IPublicCatalogOrganRelService {

    @Resource
    private IPublicCatalogOrganRelDao publicCatalogOrganRelDao;

    @Override
    public void delete(Long catId, Long organId) {
        publicCatalogOrganRelDao.delete(catId, organId);
    }

    public void deleteAll() {
        publicCatalogOrganRelDao.deleteAll();
    }

    /**
     * 查询该目录所有的单位私有配置且是隐藏的
     *
     * @param catId 目录id
     * @return
     */
    @Override
    public List<PublicCatalogOrganRelEO> getHideRelByCatId(Long catId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("catId", catId);
        paramMap.put("isShow", Boolean.FALSE);//隐藏的
        paramMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        return publicCatalogOrganRelDao.getEntities(PublicCatalogOrganRelEO.class, paramMap);
    }

    /**
     * 根据单位id和目录id查询私有目录
     *
     * @param organId 单位id
     * @param catId   目录id
     * @return
     */
    @Override
    public PublicCatalogOrganRelEO getByOrganIdCatId(Long organId, Long catId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("organId", organId);
        paramMap.put("catId", catId);
        paramMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        return publicCatalogOrganRelDao.getEntity(PublicCatalogOrganRelEO.class, paramMap);
    }

    /**
     * 保存私有目录
     *
     * @param organId         单位id
     * @param publicCatalogEO 目录信息
     * @param isShow          是否显示
     * @param isParent        是否父节点
     */
    @Override
    public void savePrivateCatalog(Long organId, PublicCatalogEO publicCatalogEO, Boolean isShow, Boolean isParent) {
        PublicCatalogOrganRelEO relEO = new PublicCatalogOrganRelEO();
        PublicCatalogUtil.filterCatalogRel(relEO, publicCatalogEO);
        relEO.setOrganId(organId);
        relEO.setCatId(publicCatalogEO.getId());
        relEO.setIsShow(isShow);
        relEO.setIsParent(isParent);
        publicCatalogOrganRelDao.save(relEO);
    }

    /**
     * 更新单位私有目录，当私有目录不存在时，新建私有目录，复制目录的属性
     * 当私有目录存在时，判断是否为父节点，如果和传入的值不一致则更新，否则不处理
     *
     * @param organId         单位id
     * @param publicCatalogEO 目录信息
     * @param isShow          是否显示
     * @param isParent        是否为父节点
     */
    @Override
    public boolean updatePrivateCatalog(Long organId, PublicCatalogEO publicCatalogEO, Boolean isShow, Boolean isParent) {
        Long catId = publicCatalogEO.getId();
        PublicCatalogOrganRelEO relEO = this.getByOrganIdCatId(organId, catId);
        if (null == relEO) {//  publicCatalogEO 该对象可能为父节点也有可能为子节点
            isShow = null == isShow ? publicCatalogEO.getIsShow() : isShow;
            isParent = null == isParent ? publicCatalogEO.getIsParent() : isParent;
            this.savePrivateCatalog(organId, publicCatalogEO, isShow, isParent);
        } else {
            if (relEO.getIsShow() == isShow && relEO.getIsParent() == isParent) {
                return false; // 属性相等无需更新
            }
            PublicCatalogUtil.filterCatalogRel(relEO, publicCatalogEO);// 复制属性
            relEO.setIsShow(null == isShow ? relEO.getIsShow() : isShow);// 是否显示
            relEO.setIsParent(null == isParent ? relEO.getIsParent() : isParent);// 是否父节点
            publicCatalogOrganRelDao.update(relEO);
        }
        return true;
    }

    /**
     * 是否查询所有，为false只查询显示的目录
     *
     * @param organId
     * @param all
     * @return
     */
    @Override
    public List<Long> getListByOrganId(Long organId, boolean all) {
        List<Object> paramList = new ArrayList<Object>();
        paramList.add(organId);
        paramList.add(AMockEntity.RecordStatus.Normal.toString());
        String hql = "SELECT distinct p.catId from PublicCatalogOrganRelEO p where p.organId = ? and p.recordStatus = ? ";
        if (!all) { // 查询显示的目录
            hql += " and p.isShow = ? ";
            paramList.add(Boolean.TRUE);
        }
        return (List<Long>) publicCatalogOrganRelDao.getObjects(hql, paramList.toArray());
    }

    @Override
    public Long getSourceColumnCount(String referColumnId){
        return publicCatalogOrganRelDao.getSourceColumnCount(referColumnId);
    }

    @Override
    public Long getSourceOrganCatCount(String referOrganCatId){
        return publicCatalogOrganRelDao.getSourceOrganCatCount(referOrganCatId);
    }
}