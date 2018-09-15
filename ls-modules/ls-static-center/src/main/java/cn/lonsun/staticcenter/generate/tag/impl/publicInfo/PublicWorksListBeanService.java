/*
 * PublicWorksListBeanService.java         2016年9月22日 <br/>
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

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.publicInfo.internal.dao.IPublicWorksDao;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogOrganRelEO;
import cn.lonsun.publicInfo.internal.entity.PublicWorksEO;
import cn.lonsun.publicInfo.util.PublicUtil;
import cn.lonsun.publicInfo.vo.PublicContentVO;
import cn.lonsun.publicInfo.vo.PublicWorksVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.AssertUtil;
import cn.lonsun.staticcenter.generate.util.MongoUtil;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.util.DateUtil;
import cn.lonsun.util.PublicCatalogUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * TODO <br/>
 * 
 * @date 2016年9月22日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Service
public class PublicWorksListBeanService extends AbstractBeanService {

    @Resource
    private IPublicWorksDao publicWorksDao;

    /**
     * 列表查询前一天数据（前一天必须有数据，没数据的话再往前推一天直到有数据为止）
     * 
     * @see cn.lonsun.staticcenter.generate.tag.BeanService#getObject(com.alibaba.fastjson.JSONObject)
     */
    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();
        Long siteId = ObjectUtils.defaultIfNull(paramObj.getLong("siteId"), context.getSiteId());
        AssertUtil.isEmpty(siteId, "站点id不能为空！");
        context.setSiteId(siteId);
        Long organId = paramObj.getLong(GenerateConstant.ORGAN_ID);
        Integer type = paramObj.getInteger(GenerateConstant.TYPE);
        Integer afterToday = paramObj.getInteger("afterToday");
        if (null == organId) {// 从url地址获取
            organId = NumberUtils.toLong(context.getParamMap().get("organId"));
            paramObj.put("organId",organId);
        }

        String excludeId = paramObj.getString(GenerateConstant.EXCLUDE);// 排除的单位id
        AssertUtil.andEmpty(organId, excludeId, "单位id或者排除单位id不能为空！");
        String action = ObjectUtils.defaultIfNull(paramObj.getString("action"), "list");
        // 查询
        StringBuffer hql = new StringBuffer();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        if (null != organId) {// 单位不为空
            hql.append(" select p from PublicWorksEO as p where p.siteId = :siteId");
            hql.append(" and p.leaders.organId = :organId");
            hql.append(" and p.enable = :enable");
            hql.append(" order by p.workDate desc,p.leaders.sortNum desc");
            paramsMap.put("organId", organId);
        } else {
            hql.append(" select p from PublicWorksEO as p,OrganEO o where p.leaders.organId = o.organId");
            hql.append(" and p.siteId = :siteId and p.leaders.organId not in (:organId)");
            hql.append(" and p.enable = :enable");
            hql.append(" order by p.workDate desc,o.sortNum,p.leaders.sortNum desc");
            paramsMap.put("organId", StringUtils.getArrayWithLong(excludeId, ","));
        }
        paramsMap.put("siteId", siteId);
        paramsMap.put("enable", Boolean.TRUE);
        if ("list".equals(action)) {// 列表
            hql = new StringBuffer();
            paramsMap = new HashMap<String, Object>();
            if (null != organId&&organId!=0L) {// 单位不为空
                hql.append(" select p from PublicWorksEO as p where p.siteId = :siteId");
                hql.append(" and p.leaders.organId = :organId");
                if(afterToday!=null&&afterToday==1){//查询今天的安排
                    hql.append(" and p.workDate >= :startDate");
                    hql.append(" and p.workDate < :edDate");
                    paramsMap.put("startDate", DateUtil.getAnyday(0));
                    paramsMap.put("edDate", DateUtil.getAnyday(1));
                }else{//查询今天之前的最新安排
                    hql.append(" and p.workDate >= (select max(w.workDate) from PublicWorksEO w where w.leaders.organId = :organId and w.workDate< :edDate )");
                    hql.append(" and p.workDate < :edDate");
                    paramsMap.put("edDate", DateUtil.getToday());
                }
                hql.append(" and p.enable = :enable");
                if(type!=null&&type==2){//副秘书长
                    hql.append(" and p.leaders.sortNum < 500 ");
                }else{//市政府领导
                    hql.append(" and p.leaders.sortNum > 500 ");
                }

                hql.append(" order by p.leaders.sortNum desc");
                paramsMap.put("organId", organId);

            } else {
                hql.append(" select p from PublicWorksEO as p,OrganEO o where p.leaders.organId = o.organId");
                hql.append(" and p.siteId = :siteId and p.leaders.organId not in (:organId)");
                if(afterToday!=null&&afterToday==1){//查询今天的安排
                    hql.append(" and p.workDate >= :startDate");
                    hql.append(" and p.workDate < :edDate");
                    paramsMap.put("startDate", DateUtil.getAnyday(0));
                    paramsMap.put("edDate", DateUtil.getAnyday(1));
                }else{//查询今天之前的最新安排
                    hql.append(" and p.workDate >= (select max(w.workDate) from PublicWorksEO w where w.leaders.organId = :organId and w.workDate< :edDate )");
                    hql.append(" and p.workDate < :edDate");
                    paramsMap.put("edDate", DateUtil.getToday());
                }
                hql.append(" and p.enable = :enable");
                hql.append(" order by o.sortNum,p.leaders.sortNum desc");
                paramsMap.put("organId", StringUtils.getArrayWithLong(excludeId, ","));
            }
            paramsMap.put("siteId", siteId);
            paramsMap.put("enable", Boolean.TRUE);
            List<PublicWorksEO> worksEOList = publicWorksDao.getEntitiesByHql(hql.toString(), paramsMap);
            //不能直接在EO里进行替换，否则会影响数据库，新建vo列表
            List<PublicWorksVO> worksVOList = new ArrayList<PublicWorksVO>();
            for(PublicWorksEO worksEO:worksEOList){
                PublicWorksVO vo = new PublicWorksVO();
                vo.setLeaders(worksEO.getLeaders());
                vo.setRemark(worksEO.getRemark());
                vo.setWorkDate(worksEO.getWorkDate());
                String jobContent = worksEO.getJobContent();
                //替换换行标签，前台展示用
                vo.setJobContent(jobContent.replace("\n","<br/>"));
                worksVOList.add(vo);
            }
            return worksVOList;
        }
        Integer pageSize = paramObj.getInteger(GenerateConstant.PAGE_SIZE);
        return publicWorksDao.getPagination(context.getPageIndex() - 1, pageSize, hql.toString(), paramsMap);
    }
}