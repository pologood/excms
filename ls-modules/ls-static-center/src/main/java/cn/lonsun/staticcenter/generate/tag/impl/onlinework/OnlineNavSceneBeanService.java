package cn.lonsun.staticcenter.generate.tag.impl.onlinework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.net.service.entity.CmsWorkGuideEO;
import cn.lonsun.net.service.service.IWorkGuideService;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.site.template.internal.entity.ParamDto;
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
public class OnlineNavSceneBeanService extends AbstractBeanService {

    @Autowired
    private IColumnConfigService columnConfigService;

    @Autowired
    private IWorkGuideService workGuideService;

    @Override
    public Object getObject(JSONObject paramObj) {
        // 数据预处理
        Map<String, Object> map = new HashMap<String, Object>();
        Context context = ContextHolder.getContext();
        //来自传参的ID
        Long columnId = context.getColumnId();

        if(AppUtil.isEmpty(columnId)) {
            //来自标签的ID
            columnId = paramObj.getLong(GenerateConstant.ID);
        }

        List<ColumnMgrEO> list = columnConfigService.getLevelColumnTree(columnId,new int[]{2},null,null);
        if(null == list || list.isEmpty()) {
            List<ColumnMgrEO> _list = columnConfigService.getLevelColumnTree(columnId,new int[]{1},null,null);
            ParamDto dto = new ParamDto();
            List<Long> cIds = new ArrayList<Long>();
            for(ColumnMgrEO eo : _list) {
                cIds.add(eo.getIndicatorId());
                dto.setSiteId(eo.getSiteId());
            }
            List<CmsWorkGuideEO> guideEOs = workGuideService.getEOsByCIds(dto, cIds);
            map.put("eos",guideEOs);
            map.put("leaf",true);
            map.put("columnId",columnId);
        } else {
            map.put("eos",list);
            map.put("leaf",false);
        }

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
