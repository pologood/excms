package cn.lonsun.staticcenter.generate.tag.impl.onlinework;

import cn.lonsun.net.service.entity.CmsRelatedRuleEO;
import cn.lonsun.net.service.service.IRelatedRuleService;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.RegexUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2016-5-28 9:10
 */
@Component
public class RelatedRuleDetailBeanService extends AbstractBeanService {

    @Autowired
    private IRelatedRuleService relatedRuleService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("contentId",context.getContentId());
        CmsRelatedRuleEO eo = relatedRuleService.getEntity(CmsRelatedRuleEO.class, params);
        return eo;
    }

    @Override
    public String objToStr(String content, Object resultObj, JSONObject paramObj) throws GenerateException {
        CmsRelatedRuleEO eo = (CmsRelatedRuleEO)resultObj;
        return RegexUtil.parseProperty(content, eo);
    }
}