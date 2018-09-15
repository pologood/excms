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
import cn.lonsun.rbac.internal.service.IOrganService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文章列表页 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年8月18日 <br/>
 */
@Component
public class MapInfoBeanService extends AbstractBeanService {

    @DbInject("baseContent")
    private IBaseContentDao contentDao;
    @Autowired
    private IOrganService organService;
    /**
     * 查询指定栏目的第一条数据信息
     *
     * @see AbstractBeanService
     */
    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();

        Long columnId = paramObj.getLong("columnIds");
        // 站点ID
        String strSiteId = context.getParamMap().get("siteId");
        Long siteId = null;
        if (StringUtils.isEmpty(strSiteId)) {
            siteId = context.getSiteId();
        } else {
            siteId = Long.valueOf(strSiteId);
        }
        System.out.println(columnId+"_"+siteId);

        Map<String, Object> map = new HashMap<String, Object>();
        StringBuffer hql = new StringBuffer();
        hql.append("FROM BaseContentEO c WHERE c.columnId  =:columnId ");
        hql.append(" AND c.siteId=:siteId AND c.isPublish=1 AND c.recordStatus =:recordStatus ");
        hql.append(ModelConfigUtil.getOrderByHql(columnId, siteId, BaseContentEO.TypeCode.articleNews.toString()));
        map.put("columnId", columnId);
        map.put("siteId", siteId);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());

        List<BaseContentEO> list = contentDao.getEntities(hql.toString(), map, 1);

        System.out.println(list.size());

        BaseContentEO eo = null;
        String content = "";
        String jd = "";
        String wd = "";
        int i = 0;
        if(list!=null&&list.size()>0){
            eo = list.get(0);
            String path = PathUtil.getLinkPath(eo.getColumnId(), eo.getId());
            //文章新闻详细页地址
            eo.setLink(path);
            content = MongoUtil.queryById(eo.getId());
            //获取内容
            if (!AppUtil.isEmpty(content)) {
                String txtcontent = content.replace("&nbsp;", ""); // 剔出&nbsp;
                //替换成字符串，防止前台filterHTML标签过滤，造成解析出问题
                txtcontent = txtcontent.replace("<script>","&script1");
                txtcontent = txtcontent.replace("</script>","&script2");
                eo.setArticle(txtcontent);

                Pattern wdpattern = Pattern.compile("(?<=subStringLocationLatitude\\()[^\\)]+");//纬度
                Pattern jdpattern = Pattern.compile("(?<=subStringLocationLongitude\\()[^\\)]+");//经度

                Matcher wdmatcher = wdpattern.matcher(txtcontent);
                Matcher jdmatcher = jdpattern.matcher(txtcontent);
                while(wdmatcher.find())
                {
                    wd = wdmatcher.group().replace("\"","");
                }
                while(jdmatcher.find())
                {
                    jd = jdmatcher.group().replace("\"","");
                }
            }

        }

        Map<String,Object> resultMap = new HashMap<String, Object>();
        resultMap.put("eo",eo);
        resultMap.put("wd",wd);
        resultMap.put("jd",jd);
        return resultMap;
    }

}