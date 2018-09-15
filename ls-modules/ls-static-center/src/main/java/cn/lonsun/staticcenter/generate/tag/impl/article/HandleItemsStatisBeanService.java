package cn.lonsun.staticcenter.generate.tag.impl.article;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.AssertUtil;
import cn.lonsun.util.DateUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2016-12-08 14:37
 */
@Component
public class HandleItemsStatisBeanService extends AbstractBeanService {

    @DbInject("baseContent")
    private IBaseContentDao baseContentDao;

    public static final String TODAY = "today";//今日
    public static final String CUR_MONTH = "curmonth";// 本月
    public static final String ACCEPT = "accept";//接收
    public static final String OVER = "over";// 完成

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        String strSiteId = context.getParamMap().get("siteId");
        Long siteId = null;
        if (StringUtils.isEmpty(strSiteId)) {
            siteId = context.getSiteId();
        } else {
            siteId = Long.valueOf(strSiteId);
        }

        if(null == siteId) {
            siteId = paramObj.getLong("siteId");
        }
        AssertUtil.isEmpty(siteId, "站点id不能为空！");
        Long[] columnIds = super.getQueryColumnIdByChild(paramObj, BaseContentEO.TypeCode.handleItems.toString());
        AssertUtil.isEmpty(columnIds, "栏目不能为空！");
        // 查询
        Integer size = columnIds.length;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        StringBuffer hql = new StringBuffer();
        hql.append(" from BaseContentEO c where c.siteId = :siteId and c.isPublish = :isPublish ");
        hql.append(" and c.columnId ").append(size == 1 ? " = :columnIds" : " in (:columnIds) ");
        // 搜索关键字
        String keyWords = context.getParamMap().get("SearchWords");
        if (StringUtils.isNotEmpty(keyWords)) {
            hql.append(" and c.title like '%" + SqlUtil.prepareParam4Query(keyWords) + "%' ");
        }

        //时间模式
        String timeMode = paramObj.getString("timeMode");
        //项目类型
        String itemType = paramObj.getString("itemType");

        AssertUtil.isEmpty(timeMode, "标签属性timeMode不能为空！");
        AssertUtil.isEmpty(itemType, "标签属性iType不能为空！");
        hql.append(" and c.publishDate >= :startDate");
        hql.append(" and c.publishDate <= :endDate");

        if(timeMode.equals(CUR_MONTH)) {
            //当月
            paramMap.put("startDate", DateUtil.getMonth());
        } else {
            //当天
            paramMap.put("startDate", DateUtil.getToday());
        }
        paramMap.put("endDate", new Date());

        //办理结果统计
        hql.append(" and c.handleStatus in (:handleStatus)");
        if(itemType.equals(ACCEPT)) {
            //接收
            paramMap.put("handleStatus", new Integer[]{0,1});
        } else {
            //完成
            paramMap.put("handleStatus", new Integer[]{1});
        }

        hql.append(" and c.recordStatus = :recordStatus ");
        paramMap.put("siteId", siteId);
        paramMap.put("isPublish", 1);// 发布
        paramMap.put("columnIds", size == 1 ? columnIds[0] : columnIds);
        paramMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        Long count = baseContentDao.getCount(hql.toString(), paramMap);
        return count;
    }

    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        return new HashMap<String, Object>();
    }
}
