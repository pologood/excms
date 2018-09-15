
package cn.lonsun.staticcenter.generate.tag.impl.special;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.special.internal.entity.SpecialEO;
import cn.lonsun.special.internal.service.ISpecialService;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.AssertUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 专题自定义背景图片 <br/>
 *
 * @author liuk <br/>
 * @version v1.0 <br/>
 * @date 2017年7月05日 <br/>
 */
@Component
public class SpecialPageBackgroundBeanService extends AbstractBeanService {

    @Autowired
    private ISpecialService specialService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Long specialId = paramObj.getLong("specialId");
        AssertUtil.isEmpty(specialId, "专题ID不能为空！");
        SpecialEO specialEO = specialService.getById(specialId);

        return specialEO;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String objToStr(String content, Object resultObj, JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        SpecialEO specialEO = (SpecialEO) resultObj;
        //IndicatorEO siteEO = CacheHandler.getEntity(IndicatorEO.class, context.getSiteId());
        String pageBackgroud = "";
        if (!AppUtil.isEmpty(specialEO.getPageBackground())) {
            pageBackgroud = specialEO.getPageBackground();
        }
        return pageBackgroud;
    }

}