package cn.lonsun.staticcenter.generate.tag.impl.onlinework;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.net.service.entity.CmsGuideResRelatedEO;
import cn.lonsun.net.service.entity.CmsWorkGuideEO;
import cn.lonsun.net.service.service.IWorkGuideService;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.RegexUtil;
import cn.lonsun.staticcenter.generate.util.UrlUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2016-5-28 9:10
 */
@Component
public class SceneServiceDetailBeanService extends AbstractBeanService {

    @Autowired
    private IWorkGuideService workGuideService;

    @Autowired
    private IOrganService organService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Map<String, String> pmap = context.getParamMap();
        Long guideId = null;
        if(pmap != null && pmap.size() > 0) {
            if(!AppUtil.isEmpty(pmap.get("guideId"))) {
                guideId = Long.valueOf(pmap.get("guideId"));
            }
        }
        List<CmsGuideResRelatedEO> rleos = null;
        CmsWorkGuideEO eo = null;
        if(null != guideId) {
            eo = workGuideService.getEntity(CmsWorkGuideEO.class,guideId);
        }

        Long columnId = context.getColumnId();
        if(null != eo &&  !AppUtil.isEmpty(columnId)) {
            IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, columnId);
            if(null != indicatorEO) {
                eo.setColumnName(indicatorEO.getName());
                //获取表格资源
                rleos = CacheHandler.getList(CmsGuideResRelatedEO.class, CacheGroup.CMS_PARENTID,eo.getId());
            }
        }

        if(null != rleos && null != eo) {
            for(CmsGuideResRelatedEO rleo : rleos) {
                String type = rleo.getType();
                if(null != type && rleo.getType().equals(CmsGuideResRelatedEO.TYPE.TABLE.toString())) {
                    String tbids = eo.getTableIds();
                    if(null != rleo.getResId()) {
                        if(null == tbids) {
                            eo.setTableIds(rleo.getResId() + "");
                        } else {
                            eo.setTableIds(tbids + "," + rleo.getResId());
                        }
                    }
                } else if(null != type && rleo.getType().equals(CmsGuideResRelatedEO.TYPE.RULE.toString())) {
                    String ruleIds = eo.getRuleIds();
                    if(null != rleo.getResId()) {
                        if(null == ruleIds) {
                            eo.setRuleIds(rleo.getResId() + "");
                        } else {
                            eo.setRuleIds(ruleIds + "," + rleo.getResId());
                        }
                    }
                }
            }
        }

        if(null != eo && null != eo.getOrganId()) {
            OrganEO organEO = organService.getEntity(OrganEO.class,Long.valueOf(eo.getOrganId()));
            if(null != organEO) {
                eo.setOrganName(organEO.getName());
            }
        }

        return eo;
    }

    @Override
    public String objToStr(String content, Object resultObj, JSONObject paramObj) throws GenerateException {

        Context context = ContextHolder.getContext();
        Map<String, String> pmap = context.getParamMap();
        CmsWorkGuideEO eo = null;

        try {
            Object neo = BeanUtils.cloneBean(resultObj);
            eo = (CmsWorkGuideEO) neo;
        } catch (Exception e) {
            e.printStackTrace();
        }

        String tableColumnId = null;
        if(pmap != null && pmap.size() > 0) {
            if(!AppUtil.isEmpty(pmap.get("tableColumnId"))) {
                tableColumnId = String.valueOf(pmap.get("tableColumnId"));
            }
        }

        String relateColumnId = null;
        if(pmap != null && pmap.size() > 0) {
            if(!AppUtil.isEmpty(pmap.get("relateColumnId"))) {
                relateColumnId = String.valueOf(pmap.get("relateColumnId"));
            }
        }

        if(AppUtil.isEmpty(eo.getZxLink())) {
            eo.setZxLink("#");
        } else {
            eo.setZxLink(UrlUtil.addParam(eo.getZxLink(), "organId=" + (AppUtil.isEmpty(eo.getOrganId())?"":eo.getOrganId())));
        }

        if(AppUtil.isEmpty(eo.getTsLink())) {
            eo.setTsLink("#");
        } else {
            eo.setTsLink(UrlUtil.addParam(eo.getTsLink(), "organId=" + (AppUtil.isEmpty(eo.getOrganId())?"":eo.getOrganId())));
        }

        if(AppUtil.isEmpty(eo.getSbLink())) {
            eo.setSbLink("#");
        } else {
            eo.setSbLink(UrlUtil.addParam(eo.getSbLink(), "organId=" + (AppUtil.isEmpty(eo.getOrganId())?"":eo.getOrganId())));
            eo.setSbLink(UrlUtil.addParam(eo.getSbLink(), "tableIds=" + (AppUtil.isEmpty(eo.getTableIds())?"":eo.getTableIds())));
        }

        if(!AppUtil.isEmpty(tableColumnId)) {
            eo.setRelateTablesUrl("/content/column/" + tableColumnId + "?guideId=" + (AppUtil.isEmpty(eo.getId()) ? "" : eo.getId()));
        }

        if(!AppUtil.isEmpty(relateColumnId)) {
            eo.setRelateRulesUrl("/content/column/" + relateColumnId + "?guideId=" + (AppUtil.isEmpty(eo.getId()) ? "" : eo.getId()));
        }
        return RegexUtil.parseProperty(content, eo);
    }
}