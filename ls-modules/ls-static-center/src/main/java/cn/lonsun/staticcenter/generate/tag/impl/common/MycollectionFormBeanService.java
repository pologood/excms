package cn.lonsun.staticcenter.generate.tag.impl.common;

import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by hu on 2016/8/16.
 */
@Component
public class MycollectionFormBeanService extends AbstractBeanService {
    @Override
    public Object getObject(JSONObject paramObj) {
        Context context= ContextHolder.getContext();
        return context.getSiteId();
    }
}
