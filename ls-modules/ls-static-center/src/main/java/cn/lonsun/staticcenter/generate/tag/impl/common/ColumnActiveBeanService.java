package cn.lonsun.staticcenter.generate.tag.impl.common;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 选中当前栏目所属的菜单 <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-12-22<br/>
 */
@Component
public class ColumnActiveBeanService extends AbstractBeanService {
    @Resource(name = "ex_8_IndicatorServiceImpl")
    private cn.lonsun.rbac.indicator.service.IIndicatorService indicatorService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long columnId = context.getColumnId();// 栏目id
        String module = context.getModule();
        String cName = paramObj.getString("name");
        String result = paramObj.getString("result");
        Long cId = paramObj.getLong("id");
        Boolean isIndex = paramObj.getBoolean("isIndex");//是否是首页
        String activeId = context.getParamMap().get("activeId");

        if (isIndex == null || cId == null) {
            return null;
        }

        //自传ID选中
        if (!AppUtil.isEmpty(activeId)) {
            if (activeId.equals(cId + "")) {
                return StringUtils.isEmpty(cName) ? "active" : cName;
            }
        } else {
            IndicatorEO eo = indicatorService.getEntity(IndicatorEO.class, cId);

            if (eo == null) {
                if (!"public".equals(module)) {//不是信息公开栏目页或者文章页
                    return null;
                }
            } else {
                if ("public".equals(module)) {//信息公开首页
                    columnId = context.getSiteId();
                } else if (isIndex && IndicatorEO.Type.CMS_Site.toString().equals(eo.getType()) && columnId == null) {//站点首页
                    if (StringUtils.isEmpty(cName)) {
                        return "active";
                    } else {
                        return cName;
                    }
                } else if (!isIndex && IndicatorEO.Type.CMS_Site.toString().equals(eo.getType())) {
                    return null;
                }
            }
            if (isIndex || columnId == null) {//站点首页后者信息公开首页
                return null;
            }
            boolean isActive = getActiveColumn(columnId, cId);
            if (isActive) {
                if (StringUtils.isEmpty(cName)) {
                    return "active";
                } else {
                    return cName;
                }
            }
        }

        return null;
    }

    public boolean getActiveColumn(Long columnId, Long cId) {
        if (columnId.equals(cId)) {
            return true;
        }
        IndicatorEO eo = indicatorService.getEntity(IndicatorEO.class, columnId);
        if (eo == null) {
            return false;
        }
        IndicatorEO peo = indicatorService.getEntity(IndicatorEO.class, eo.getParentId());
        if (peo != null) {
            if (peo.getIndicatorId().equals(cId)) {
                return true;
            } else {
                return getActiveColumn(peo.getIndicatorId(), cId);
            }
        }
        return false;
    }

    public String objToStr(String content, Object resultObj, JSONObject paramObj) {
        String str = "";
        if (!AppUtil.isEmpty(resultObj)) {
            str = (String) resultObj;
        } else {
            str = paramObj.getString("result");
        }
        return str;
    }
}
