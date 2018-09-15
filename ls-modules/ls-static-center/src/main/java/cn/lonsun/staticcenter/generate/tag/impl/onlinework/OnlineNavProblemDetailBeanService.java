package cn.lonsun.staticcenter.generate.tag.impl.onlinework;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.net.service.entity.CmsCommonProblemEO;
import cn.lonsun.net.service.service.ICommonProblemService;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2015-12-14 13:56
 */
@Component
public class OnlineNavProblemDetailBeanService extends AbstractBeanService {

    @Autowired
    private ICommonProblemService commonProblemService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Map<String,Object> map = new HashMap<String, Object>();
        //来自url参数
        Map<String, String> pmap = context.getParamMap();

        Long problemId = null;
        if (pmap != null && pmap.size() > 0) {
            if (!AppUtil.isEmpty(pmap.get("problemId"))) {
                problemId = Long.valueOf(pmap.get("problemId"));
            }
        }
        CmsCommonProblemEO eo = commonProblemService.getEntity(CmsCommonProblemEO.class,problemId);
        map.put("eo",eo);
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
