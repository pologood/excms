package cn.lonsun.staticcenter.generate.tag.impl.common;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
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

import java.util.ArrayList;
import java.util.List;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2017-5-11<br/>
 */
@Component
public class SiteMapOldBeanService  extends AbstractBeanService {
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
            list = getSiteMap1(lev, siteId, columnIds, link);
        } else {
            if (siteMgrEO.getComColumnId() != null) {
                list = getSiteMap2(lev, siteId, siteMgrEO.getComColumnId(), columnIds, link);
            }
        }

        return list;
    }

    private List<ColumnMgrEO> getSiteMap1(String lev, Long siteId, String columnIds, Boolean link) {
        StringBuffer sql = new StringBuffer("select * from (")
                .append("select level as lev, r.indicator_Id as indicatorId,r.name as name,r.parent_id as parentId,r.sort_num as sortNum,r.is_parent as isParent,r.create_date as createDate ")
                .append(",r.site_id as siteId,r.type as type,r.record_status as recordStatus,c.is_show as isShow,c.column_type_code as columnTypeCode,c.is_start_url as isStartUrl, c.trans_url as transUrl ")
                .append(" from rbac_indicator  r left join cms_column_config c on r.indicator_id=c.indicator_id connect by prior r.indicator_id = r.parent_id start with  r.indicator_id = " + siteId + ") ");
        sql.append(" where lev in (" + lev + ")");
        sql.append(" and type='" + IndicatorEO.Type.CMS_Section.toString() + "' and recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'")
                .append(" and isShow=1 ");


        if (link != null && link) {
            sql.append(" and columnTypeCode <> '" + BaseContentEO.TypeCode.linksMgr.toString() + "'");
        } else {
            sql.append(" and columnTypeCode not in ('" + BaseContentEO.TypeCode.linksMgr.toString() + "','redirect')");
        }
        if (!StringUtils.isEmpty(columnIds)) {
            sql.append(" and indicatorId not in (" + columnIds + ")");
        }
        sql.append(" order by lev asc, sortNum asc,createDate asc");
        List<String> fields = new ArrayList<String>();
        fields.add("lev");
        fields.add("indicatorId");
        fields.add("name");
        fields.add("parentId");
        fields.add("sortNum");
        fields.add("isParent");
        fields.add("createDate");
        fields.add("siteId");
        fields.add("type");
        fields.add("recordStatus");
        fields.add("isShow");
        fields.add("columnTypeCode");
        fields.add("isStartUrl");
        fields.add("transUrl");
        String[] strArr = new String[fields.size()];
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) configDao.getBeansBySql(sql.toString(), new Object[]{}, ColumnMgrEO.class, fields.toArray(strArr));
        return list;
    }

    private List<ColumnMgrEO> getSiteMap2(String lev, Long siteId, Long columnId, String columnIds, Boolean link) {
        StringBuffer sql = new StringBuffer("select * from (")
                .append("select level as lev, r.indicator_Id as indicatorId,r.name as name,r.parent_id as parentId, url_path as urlPath,r.sort_num as sortNum,r.is_parent as isParent,r.create_date as createDate ")
                .append(",r.site_id as siteId,r.type as type,r.record_status as recordStatus,r.is_show as isShow,r.column_type_code as columnTypeCode,r.is_start_url as isStartUrl, r.trans_url as transUrl ")
                .append(" from cms_column_mgr r connect by prior r.indicator_id = r.parent_id start with  r.indicator_id = " + columnId + ")");
        sql.append(" where lev in (" + lev + ")");
        sql.append(" and recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'")
                .append(" and isShow=1 ");
        if (link != null && link) {
            sql.append(" and columnTypeCode <> '" + BaseContentEO.TypeCode.linksMgr.toString() + "'");
        } else {
            sql.append(" and columnTypeCode not in ('" + BaseContentEO.TypeCode.linksMgr.toString() + "','redirect')");
        }
        if (!StringUtils.isEmpty(columnIds)) {
            sql.append(" and indicatorId not in (" + columnIds + ")");
        }
        sql.append(" order by lev asc, sortNum asc,createDate asc");
        List<String> fields = new ArrayList<String>();
        fields.add("lev");
        fields.add("indicatorId");
        fields.add("name");
        fields.add("parentId");
        fields.add("sortNum");
        fields.add("isParent");
        fields.add("createDate");
        fields.add("siteId");
        fields.add("type");
        fields.add("recordStatus");
        fields.add("isShow");
        fields.add("columnTypeCode");
        fields.add("isStartUrl");
        fields.add("transUrl");
        String[] strArr = new String[fields.size()];
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) configDao.getBeansBySql(sql.toString(), new Object[]{}, ColumnMgrEO.class, fields.toArray(strArr));
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
