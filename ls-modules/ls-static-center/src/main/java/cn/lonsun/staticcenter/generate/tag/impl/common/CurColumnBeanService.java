package cn.lonsun.staticcenter.generate.tag.impl.common;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import static cn.lonsun.cache.client.CacheHandler.getEntity;

/**
 * 获取当前栏目<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-12-3<br/>
 */
@Component
public class CurColumnBeanService extends AbstractBeanService {
    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long columnId = paramObj.getLong(GenerateConstant.ID);
        if (null == columnId) {// 如果栏目id为空说明，栏目id是在页面传入的
            columnId = context.getColumnId();
        }
        if (columnId == null) {
            Long contentId = context.getContentId();// 根据文章id查询文章
            BaseContentEO contentEO = CacheHandler.getEntity(BaseContentEO.class, contentId);
            if (contentEO == null) {
                return null;
            }
            columnId = contentEO.getColumnId();
            if (columnId == null) {
                return null;
            }
        }
        ColumnMgrEO eo = getEntity(ColumnMgrEO.class, columnId);
        return eo;
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
