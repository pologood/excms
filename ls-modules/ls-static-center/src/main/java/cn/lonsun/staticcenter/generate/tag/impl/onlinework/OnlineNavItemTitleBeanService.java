package cn.lonsun.staticcenter.generate.tag.impl.onlinework;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.lonsun.net.service.entity.CmsWorkGuideEO;
import cn.lonsun.net.service.service.IWorkGuideService;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;

import com.alibaba.fastjson.JSONObject;

/**
 * @author gu.fei
 * @version 2015-12-14 13:56
 */
@Component
public class OnlineNavItemTitleBeanService extends AbstractBeanService {

    @Autowired
    private IWorkGuideService workGuideService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();

        //来自传参的ID
        Long contentId = context.getContentId();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("contentId",contentId);
        CmsWorkGuideEO eo = workGuideService.getEntity(CmsWorkGuideEO.class,params);
        return eo;
    }
}
