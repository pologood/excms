//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.lonsun.staticcenter.generate.tag.impl.special;

import cn.lonsun.GlobalConfig;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.MapUtil;
import com.alibaba.fastjson.JSONObject;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GuiDangPicBeanService extends AbstractBeanService {
    @Autowired
    private IBaseContentService baseContentService;

    public GuiDangPicBeanService() {
    }

    public Object getObject(JSONObject paramObj) {
        MapUtil.unionContextToJson(paramObj);
        Context context = ContextHolder.getContext();
        Long siteId = context.getSiteId();
        Long columnId = context.getColumnId();
        Long contentId = context.getContentId();
        if (siteId == null || siteId.longValue() == 0L) {
            siteId = paramObj.getLong("siteId");
        }

        if (columnId == null || columnId.longValue() == 0L) {
            columnId = paramObj.getLong("columnId");
        }

        if (contentId == null || contentId.longValue() == 0L) {
            contentId = paramObj.getLong("contentId");
        }

        if (siteId == null || siteId.longValue() == 0L) {
            System.out.println("站点siteId为空");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String fileserverpath = ((GlobalConfig)SpringContextHolder.getBean(GlobalConfig.class)).getFileServerNamePath();
        String width = paramObj.getString("width");
        if (width == null || width.equals("")) {
            paramObj.put("width", "297px");
        }

        String height = paramObj.getString("height");
        if (height == null || height.equals("")) {
            paramObj.put("height", "297px");
        }

        String bgurl = paramObj.getString("bgurl");
        if (bgurl == null || bgurl.equals("")) {
            paramObj.put("bgurl", "/bg/guidang.png");
        }

        paramObj.put("istofile", "0");
        if (contentId != null && contentId.longValue() > 0L) {
            BaseContentEO eo = (BaseContentEO)this.baseContentService.getEntity(BaseContentEO.class, contentId);
            if (eo != null && eo.getIsToFile() != null && eo.getIsToFile().intValue() == 1 && eo.getToFileId() != null && eo.getToFileDate() != null) {
                paramObj.put("fileurl", fileserverpath + eo.getToFileId());
                paramObj.put("istofile", "1");
                paramObj.put("content", "归档时间:" + sdf.format(eo.getToFileDate()));
                paramObj.put("contentId", contentId);
            }

            return paramObj;
        } else if (columnId != null && columnId.longValue() >= 1L) {
            ColumnMgrEO cme = (ColumnMgrEO)CacheHandler.getEntity(ColumnMgrEO.class, new Serializable[]{columnId});
            if (cme != null && cme.getIsToFile() != null && cme.getIsToFile().intValue() == 1 && cme.getToFileId() != null && cme.getToFileDate() != null) {
                paramObj.put("fileurl", fileserverpath + cme.getToFileId());
                paramObj.put("istofile", "1");
                paramObj.put("content", "归档时间:" + sdf.format(cme.getToFileDate()));
                paramObj.put("columnId", columnId);
            }

            return paramObj;
        } else {
            System.out.println("栏目columnId为空");
            return paramObj;
        }
    }

    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) {
        return new HashMap();
    }
}
