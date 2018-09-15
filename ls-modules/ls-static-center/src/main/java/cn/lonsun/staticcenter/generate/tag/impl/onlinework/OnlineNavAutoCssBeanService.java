package cn.lonsun.staticcenter.generate.tag.impl.onlinework;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;

import com.alibaba.fastjson.JSONObject;

/**
 * @author gu.fei
 * @version 2015-12-14 13:56
 */
@Component
public class OnlineNavAutoCssBeanService extends AbstractBeanService {

    @Autowired
    private IColumnConfigService columnConfigService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();

        //样式
        String cls = paramObj.getString("css");

        //来自传参的ID
        Long columnId = context.getColumnId();
        //来自标签的ID
        Long _columnId = paramObj.getLong("id");

        Map<String,String> paramMap = context.getParamMap();
        if(!AppUtil.isEmpty(paramMap) && paramMap.size() > 0) {
            String organId = paramMap.get("organId");
            if(!AppUtil.isEmpty(organId)) {
                return cls;
            }
        }

        List<ColumnMgrEO> columns = columnConfigService.getLevelColumnTree(_columnId, new int[]{2,3}, null, null);

        for(ColumnMgrEO eo : columns) {
            if(eo.getIndicatorId().intValue() == columnId && eo.getIsParent() == 0) {
                return cls;
            }
        }

        return " ";
    }

    @Override
    public String objToStr(String content, Object resultObj, JSONObject paramObj) {
        return String.valueOf(resultObj);
    }
}
