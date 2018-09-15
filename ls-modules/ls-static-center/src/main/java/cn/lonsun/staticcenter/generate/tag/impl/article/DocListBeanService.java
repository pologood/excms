/*
 * ListBeanService.java         2015年8月18日 <br/>
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

package cn.lonsun.staticcenter.generate.tag.impl.article;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.MongoUtil;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.util.ModelConfigUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文章列表页 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年8月18日 <br/>
 */
@Component
public class DocListBeanService extends AbstractBeanService {

    @DbInject("baseContent")
    private IBaseContentDao contentDao;
    @Autowired
    private IOrganService organService;
    /**
     * 查询文章列表
     *
     * @see cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService
     */
    @Override
    public Object getObject(JSONObject paramObj) {
        Long[] ids = super.getQueryColumnIdByChild(paramObj, BaseContentEO.TypeCode.articleNews.toString());
        if (null == ids) {
            return null;
        }
        Context context = ContextHolder.getContext();
        Long columnId = context.getColumnId();
        String inIds = paramObj.getString(GenerateConstant.ID);
        if (StringUtils.isNotEmpty(inIds)) {// 当传入多栏目时，依第一个栏目为准
            columnId = Long.valueOf(StringUtils.split(inIds, ",")[0]);
        }

        // 站点ID
        String strSiteId = context.getParamMap().get("siteId");
        Long siteId = null;
        if (StringUtils.isEmpty(strSiteId)) {
            if(!AppUtil.isEmpty(paramObj.getLong("siteId"))){
                siteId = paramObj.getLong("siteId");
            }else{
                siteId = context.getSiteId();
            }
        } else {
            siteId = Long.valueOf(strSiteId);
        }
        Boolean isTitleTop = paramObj.getBoolean("isTitleTop");

        Integer size = ids.length;
        Map<String, Object> map = new HashMap<String, Object>();
        Integer num = paramObj.getInteger(GenerateConstant.NUM);
        String where = paramObj.getString(GenerateConstant.WHERE);
        Integer exceptTitle = paramObj.getInteger("exceptTitle");
        Integer exceptImg = paramObj.getInteger("exceptImg");
        List<Long> exceptContentIds = new ArrayList<Long>();
        if(exceptTitle!=null&&exceptTitle==1){//隐藏头条新闻
            Integer exceptNum = paramObj.getInteger("exceptNum");
            exceptContentIds.addAll(getTitleContentId(siteId,ids,exceptNum));
        }

        if(exceptImg!=null&&exceptImg==1){//隐藏指定数量的带缩略图的新闻
            Integer exceptImgNum = paramObj.getInteger("exceptImgNum");
            exceptContentIds.addAll(getImgContentId(siteId,ids,exceptImgNum));
        }

        StringBuffer hql = new StringBuffer();
        hql.append("FROM BaseContentEO c WHERE c.columnId ").append(size == 1 ? " =:ids " : " in (:ids) ");
        hql.append(" AND c.siteId=:siteId AND c.isPublish=1 AND c.recordStatus =:recordStatus ");
        hql.append(StringUtils.isEmpty(where) ? "" : " AND " + where.replace("小于","<").replace("大于",">"));

        if(exceptContentIds!=null&&exceptContentIds.size()>0){//隐藏新闻
            hql.append(" AND c.id  not in (:exceptContentIds) ");
            map.put("exceptContentIds", exceptContentIds.toArray());
        }

        hql.append(ModelConfigUtil.getOrderByHql(columnId, context.getSiteId(), BaseContentEO.TypeCode.articleNews.toString()));
        if (size == 1) {
            map.put("ids", ids[0]);
        } else {
            map.put("ids", ids);
        }
        map.put("siteId", siteId);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());

        List<BaseContentEO> list = contentDao.getEntities(hql.toString(), map, num);
        List<BaseContentEO> resultList = new ArrayList<BaseContentEO>();
        List<BaseContentEO> tempList = new ArrayList<BaseContentEO>();
        if(isTitleTop!=null&&isTitleTop){//标题新闻放列表最前面
            for (BaseContentEO eo : list) {
                if (eo.getIsTitle()!=null&&eo.getIsTitle()==1){
                    resultList.add(eo);
                }else{
                    tempList.add(eo);
                }
            }
            resultList.addAll(tempList);
        }else{
            resultList.addAll(list);
        }
        return resultList;
    }


    /**
     * 获取头条新闻的contentid
     * @param siteId
     * @param ids
     * @return
     */
    public List<Long> getTitleContentId(Long siteId,Long[] ids,Integer exceptNum){
        Map<String, Object> map = new HashMap<String, Object>();
        StringBuffer hql = new StringBuffer();
        hql.append("FROM BaseContentEO c WHERE c.columnId in (:ids) ");
        hql.append(" AND c.siteId=:siteId AND c.isPublish=1 AND c.recordStatus =:recordStatus ");
        hql.append(" AND c.isTitle = 1");
        hql.append(ModelConfigUtil.getOrderByHql(ids[0], siteId, BaseContentEO.TypeCode.articleNews.toString()));

        map.put("ids", ids);

        map.put("siteId", siteId);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());

        if(exceptNum==null){
            exceptNum = 1;
        }
        List<BaseContentEO> list = contentDao.getEntities(hql.toString(), map, exceptNum);
        List<Long> id = new ArrayList<Long>();

        if(list!=null&&list.size()>0){
            for(BaseContentEO eo : list){
                id.add(eo.getId());
            }
        }
        return id;
    }

    /**
     * 获取图片新闻的contentid
     * @param siteId
     * @param ids
     * @return
     */
    public List<Long> getImgContentId(Long siteId,Long[] ids,Integer exceptImgNum){
        Map<String, Object> map = new HashMap<String, Object>();
        StringBuffer hql = new StringBuffer();
        hql.append("FROM BaseContentEO c WHERE c.columnId in (:ids) ");
        hql.append(" AND c.siteId=:siteId AND c.isPublish=1 AND c.recordStatus =:recordStatus AND c.imageLink <> '' AND c.imageLink IS NOT NULL ");
        hql.append(ModelConfigUtil.getOrderByHql(ids[0], siteId, BaseContentEO.TypeCode.articleNews.toString()));

        map.put("ids", ids);

        map.put("siteId", siteId);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());

        if(exceptImgNum==null){
            exceptImgNum = 1;
        }
        List<BaseContentEO> list = contentDao.getEntities(hql.toString(), map, exceptImgNum);
        List<Long> id = new ArrayList<Long>();

        if(list!=null&&list.size()>0){
            for(BaseContentEO eo : list){
                id.add(eo.getId());
            }
        }
        return id;
    }


    /**
     * 预处理数据
     *
     * @throws GenerateException
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        // 预处理数据，处理文章链接问题
        List<BaseContentEO> list = (List<BaseContentEO>) resultObj;
        Integer hasContent = paramObj.getInteger("hasContent");//是否查询内容
        Boolean flag = false;
        if(hasContent!=null&&hasContent==1){
            flag = true;
        }
        if (null != list && !list.isEmpty()) {
            // 处理文章链接
            for (BaseContentEO eo : list) {
                String path = PathUtil.getLinkPath(eo.getColumnId(), eo.getId());
                eo.setLink(path);

                if(flag){
                    String content = MongoUtil.queryById(eo.getId());
                    eo.setArticle(content);
                }

                if(!AppUtil.isEmpty(eo.getCreateOrganId())){
                    OrganEO organ = organService.getDirectlyUpLevelUnit(eo.getCreateOrganId());
                    if(organ!=null){
                        eo.setOrganId(organ.getOrganId());
                        eo.setOrganName(organ.getName());
                    }
                }
            }
        }
        return super.doProcess(resultObj, paramObj);
    }
}