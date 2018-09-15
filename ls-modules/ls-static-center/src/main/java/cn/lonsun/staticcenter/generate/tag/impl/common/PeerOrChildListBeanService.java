package cn.lonsun.staticcenter.generate.tag.impl.common;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.AssertUtil;
import cn.lonsun.staticcenter.generate.util.ColumnUtil;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取子栏目或者同级栏目<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-12-15<br/>
 */
@Component
public class PeerOrChildListBeanService extends AbstractBeanService {

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
        AssertUtil.isEmpty(siteId, "站点id不能为空！");
        String strColumnId = context.getParamMap().get("columnId");
        Long columnId = null;
        if (StringUtils.isEmpty(strColumnId)) {
            columnId = context.getColumnId();
        } else {
            columnId = Long.valueOf(strColumnId);
        }
        //AssertUtil.isEmpty(columnId, "栏目id不能为空！");

        List<ColumnMgrEO> list1 = new ArrayList<ColumnMgrEO>();
        if (!AppUtil.isEmpty(columnId)) {
            List<ColumnMgrEO> list = ColumnUtil.getColumnByPId(columnId, siteId);
            if (list == null || list.size() <= 0) {
                ColumnMgrEO eo = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
                if (eo != null) {
                    list = ColumnUtil.getColumnByPId(eo.getParentId(), siteId);
                }
            }

            if (list != null && list.size() > 0) {
                for (ColumnMgrEO eo : list) {
                    if (eo.getIsShow() == 1 && !BaseContentEO.TypeCode.linksMgr.toString().equals(eo.getColumnTypeCode())) {
                        list1.add(eo);
                    }
                    eo.setUri(PathUtil.getLinkPath(eo.getIndicatorId(), null));
                }
            }
        }

        return list1;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String objToStr(String content, Object resultObj, JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long columnId = paramObj.getLong(GenerateConstant.ID);
        if (null == columnId) {// 如果栏目id为空说明，栏目id是在页面传入的
            columnId = context.getColumnId();
        }
        StringBuffer sb = new StringBuffer();
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) resultObj;
        // 预处理处理栏目连接
        if (null != list && !list.isEmpty()) {
            for (ColumnMgrEO eo : list) {
                if (eo.getIndicatorId().equals(columnId)) {
                    sb.append(" <li class='active'><a title='").append(eo.getName()).append("'");
                } else {
                    sb.append(" <li><a title='").append(eo.getName()).append("'");
                }
                String target = paramObj.getString("target");
                if (eo.getIsStartUrl() != null && eo.getIsStartUrl() == 1) {// 跳转地址
                    sb.append(" href='").append(eo.getTransUrl()).append("' ");
                    if (eo.getTransWindow() != null && eo.getTransWindow() == 1) {// 新窗口打开
                        sb.append(" target='_blank' ");
                    } else {
                        if (!StringUtils.isEmpty(target)) {
                            sb.append(" target='" + target + "' ");
                        }
                    }
                    sb.append(">");
                } else {
                    sb.append(" href='").append(PathUtil.getLinkPath(eo.getIndicatorId(), null)).append("' ");
                    if (!StringUtils.isEmpty(target)) {
                        sb.append(" target='" + target + "' ");
                    }
                    sb.append(">");
                }
                sb.append(eo.getName()).append("</a></li>");
            }
        }
        //return sb.toString();
        String file = paramObj.getString(GenerateConstant.FILE);
        if (StringUtils.isEmpty(file)) {
            return super.objToStr(sb.toString(), resultObj, paramObj);
        } else {
            return super.objToStr(null, resultObj, paramObj);
        }

    }


}