package cn.lonsun.staticcenter.generate.tag.impl.govbbs;

import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangchao on 2017/2/7.
 */
@Component
public class GovOrgansBeanService extends AbstractBeanService {

    @Autowired
    private IOrganService organService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Map<String, Object> objects = new HashMap<String, Object>();
        Context context = ContextHolder.getContext();
        Long siteId = paramObj.getLong("siteId");
        if (!(siteId != null && siteId != 0)) {
            siteId = context.getSiteId();
        }
        return organService.getOrgansBySiteId(siteId,true);
    }
}
