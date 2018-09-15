package cn.lonsun.staticcenter.generate.tag.impl.common;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.site.internal.dao.IColumnConfigDao;
import cn.lonsun.site.site.internal.entity.ColumnConfigRelEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.util.ColumnRelUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-5-24<br/>
 */
@Component
public class SiteMapBeanService extends AbstractBeanService {

    @DbInject("columnConfig")
    private IColumnConfigDao configDao;

    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        String siteIdStr = context.getParamMap().get("siteId");
        Long siteId = null;
        if (!StringUtils.isEmpty(siteIdStr)) {
            siteId = Long.parseLong(siteIdStr);
        } else {
            siteId = paramObj.getLong(GenerateConstant.ID);
        }
        if (siteId == null) {
            return null;
        }
        SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class, siteId);
        if (siteMgrEO == null) {
            return null;
        }
        String columnIds = paramObj.getString("columnIds");
        String lev = paramObj.getString("lev");//2,3
        Boolean link = paramObj.getBoolean("link");//2,3
        List<ColumnMgrEO> list = null;
        if (IndicatorEO.Type.CMS_Site.toString().equals(siteMgrEO.getType())) {
            list = configDao.getSiteMap(lev, siteId,null, columnIds, link,false);
        } else {
            if (siteMgrEO.getComColumnId() != null) {
                list = getSiteMap2(lev, siteId, siteMgrEO.getComColumnId(), columnIds, link);
            }
        }

        return list;
    }

    private List<ColumnMgrEO> getSiteMap2(String lev, Long siteId, Long columnId, String columnIds, Boolean link) {
        List<ColumnMgrEO> list = configDao.getSiteMap(lev, siteId,columnId, columnIds, link,true);
        if (list != null && list.size() > 0) {
            for (ColumnMgrEO eo : list) {
                if (IndicatorEO.Type.COM_Section.toString().equals(eo.getType())) {
                    ColumnConfigRelEO relEO = ColumnRelUtil.getByIndicatorId(eo.getIndicatorId(), siteId);
                    if (relEO != null) {
                        eo.setIndicatorId(relEO.getIndicatorId());
                        eo.setIsParent(relEO.getIsParent());
                        eo.setName(relEO.getName());
                        eo.setSortNum(relEO.getSortNum());
                        eo.setColumnTypeCode(relEO.getColumnTypeCode());
                        eo.setContentModelCode(relEO.getContentModelCode());
                        eo.setIsShow(relEO.getIsShow());
                        eo.setTransUrl(relEO.getTransUrl());
                        eo.setTransWindow(relEO.getTransWindow());
                    }
                }
            }
        }
        return list;

    }

    @Override
    @SuppressWarnings("unchecked")
    public String objToStr(String content, Object resultObj, JSONObject paramObj) {
        StringBuffer sb = new StringBuffer("<div class='dty_contain'>");
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) resultObj;
        // 预处理处理栏目连接
        if (null != list && !list.isEmpty()) {
            String path = "";
            for (ColumnMgrEO pEO : list) {
                if (pEO.getLev() == 2) {
                    if (pEO.getIsStartUrl() == 1) {//直接跳转
                        path = pEO.getTransUrl();
                    } else {
                        path = PathUtil.getLinkPath(pEO.getIndicatorId(), null);
                    }

                    sb.append("<div class='dty_mutitle'>").append("<a target='_blank' href='" + path + "'>").append(pEO.getName() + "</a></div>")
                        .append("<div class='dty_sublb'><ul>");
                    for (ColumnMgrEO eo : list) {
                        if (pEO.getIndicatorId().equals(eo.getParentId())) {
                            if (eo.getIsStartUrl() == 1) {//直接跳转
                                path = eo.getTransUrl();
                            } else {
                                path = PathUtil.getLinkPath(eo.getIndicatorId(), null);
                            }
                            sb.append(" <li><a title='" + eo.getName() + "' target='_blank' href='" + path
                                + "'>" + eo.getName() + "</a></li>");
                        }
                    }
                    sb.append("</ul></div>");
                }
            }
        }
        sb.append("</div>");
        return sb.toString();
    }


}
