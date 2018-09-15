package cn.lonsun.staticcenter.generate.tag.impl.common;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.site.internal.entity.ColumnConfigRelEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.util.ColumnRelUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * <br/>
 * 获取父栏目
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-12-21<br/>
 */
@Component
public class ParentColumnBeanService extends AbstractBeanService {

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long columnId = paramObj.getLong(GenerateConstant.ID);
        if (null == columnId) {// 如果栏目id为空说明，栏目id是在页面传入的
            columnId = context.getColumnId();
        }
        if (columnId == null || columnId == 0) {
            return null;
        }
        // columnId=2999304L;
        ColumnMgrEO eo = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
        ColumnMgrEO pEO = null;
        if (eo != null) {
            pEO = CacheHandler.getEntity(ColumnMgrEO.class, eo.getParentId());
            if (pEO == null) {//站点
                return eo;
            } else {
                if (IndicatorEO.Type.CMS_Section.toString().equals(pEO.getType())) {
                    List<ColumnMgrEO> list = CacheHandler.getList(ColumnMgrEO.class, CacheGroup.CMS_PARENTID, eo.getIndicatorId());
                    if (list == null || list.size() <= 0) {
                        return pEO;
                    }
                    return eo;
                } else {
                    //ColumnConfigRelEO relEO= ColumnRelUtil.getByIndicatorId(columnId,1191767L);
                    ColumnConfigRelEO relEO = ColumnRelUtil.getByIndicatorId(columnId, context.getSiteId());
                    if (relEO != null) {
                        eo.setIndicatorId(relEO.getIndicatorId());
                        eo.setName(relEO.getName());
                        eo.setSortNum(relEO.getSortNum());
                        eo.setColumnTypeCode(relEO.getColumnTypeCode());
                        eo.setContentModelCode(relEO.getContentModelCode());
                        eo.setKeyWords(relEO.getKeyWords());
                        eo.setDescription(relEO.getDescription());
                        eo.setIsShow(relEO.getIsShow());
                        eo.setTransUrl(relEO.getTransUrl());
                        eo.setTransWindow(relEO.getTransWindow());
                    }
                    return eo;
                }
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String objToStr(String content, Object resultObj, JSONObject paramObj) {
        StringBuffer sb = new StringBuffer();
        ColumnMgrEO eo = (ColumnMgrEO) resultObj;
        Boolean isLink = paramObj.getBoolean("isLink");
        if (isLink == null) {
            isLink = false;
        }
        // 预处理处理栏目连接
        if (!AppUtil.isEmpty(eo)) {
            if (!isLink) {
                sb.append(eo.getName());
            } else {
                sb.append(" <li><a title='").append(eo.getName()).append("'");
                if (eo.getIsStartUrl() == 1) {
                    sb.append(" href='").append(eo.getTransUrl()).append("'>");
                } else {
                    sb.append(" href='").append(PathUtil.getLinkPath(eo.getIndicatorId(), null)).append("'>");
                }
                sb.append(eo.getName()).append("</a></li>");
            }
        }
        return sb.toString();
    }
}
