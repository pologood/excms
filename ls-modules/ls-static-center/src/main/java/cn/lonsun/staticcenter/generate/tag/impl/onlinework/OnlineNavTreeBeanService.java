package cn.lonsun.staticcenter.generate.tag.impl.onlinework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;

import com.alibaba.fastjson.JSONObject;

/**
 * @author gu.fei
 * @version 2015-12-14 13:56
 */
@Component
public class OnlineNavTreeBeanService extends AbstractBeanService {

    @Autowired
    private IColumnConfigService columnConfigService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long _columnId = context.getColumnId();
        Long  columnId = paramObj.getLong(GenerateConstant.ID);

        //查询条件
        String where = paramObj.getString("where");

        List<ColumnMgrEO> columns = columnConfigService.getLevelColumnTree(columnId, new int[]{2}, where,null);
        Map<Long,Boolean> pIds = columnConfigService.getParentIndicatorIds(_columnId);

        for(ColumnMgrEO eo : columns) {
            if(null != pIds) {
                eo.setActive(pIds.get(eo.getIndicatorId())!=null?"active":"");
            }
            List<ColumnMgrEO> childs = columnConfigService.getLevelColumnTree(eo.getIndicatorId(), new int[]{2}, where,null);
            for(ColumnMgrEO _eo : childs) {
                _eo.setActive(pIds.get(_eo.getIndicatorId())!=null?"active":"");
            }
            eo.setChilds(childs);
        }

        return columns;
    }

    /**
     * 预处理结果
     * @param resultObj
     * @param paramObj
     * @return
     */
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) {

        // 数据预处理
        Map<String, Object> map = new HashMap<String, Object>();

        //打开方式
        String target = paramObj.getString("target");
        map.put("target",target);

        return map;
    }
}
