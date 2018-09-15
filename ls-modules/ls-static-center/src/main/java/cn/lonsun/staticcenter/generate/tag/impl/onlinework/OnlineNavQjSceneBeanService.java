package cn.lonsun.staticcenter.generate.tag.impl.onlinework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.lonsun.common.util.AppUtil;
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
public class OnlineNavQjSceneBeanService extends AbstractBeanService {

    @Autowired
    private IColumnConfigService columnConfigService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Map<String, Object> map = new HashMap<String, Object>();
        Context context = ContextHolder.getContext();
        //来自url参数
        Map<String, String> pmap = context.getParamMap();

        //来自传参的ID
        Long ckcolumnId = context.getColumnId();
        Long tagcolumnId = paramObj.getLong(GenerateConstant.ID);
        Long columnId = null;
        if(pmap != null && pmap.size() > 0) {
            if(!AppUtil.isEmpty(pmap.get("columnId"))) {
                columnId = Long.valueOf(pmap.get("columnId"));
            }
        }
        List<ColumnMgrEO> columnMgrEOs = null;
        if(null != columnId) {
            columnMgrEOs = columnConfigService.getParentColumns(columnId);
        }

        List<ColumnMgrEO> eos = columnConfigService.getLevelColumnTree(tagcolumnId,new int[]{2},null,null);
        for(ColumnMgrEO eo :eos) {
            if(ckcolumnId.intValue() == eo.getIndicatorId().intValue()) {
                eo.setActive("active");
            } else if(null != columnId && columnId.intValue() ==
                    eo.getIndicatorId().intValue()) {
                eo.setActive("active");
            } else if(null != columnMgrEOs){
                for(ColumnMgrEO columnMgrEO : columnMgrEOs) {
                    if(eo.getIndicatorId().intValue() ==
                            columnMgrEO.getIndicatorId().intValue()) {
                        eo.setActive("active");
                    }
                }
            }
        }
        map.put("eos",eos);
        return map;
    }

    /**
     * 预处理结果
     * @param resultObj
     * @param paramObj
     * @return
     */
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) {
        Map<String, Object> map = (Map<String, Object>) resultObj;
        return map;
    }
}
