
package cn.lonsun.staticcenter.generate.tag.impl.special;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.site.template.internal.entity.TemplateConfEO;
import cn.lonsun.site.template.internal.service.ITplConfService;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.util.AssertUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 模板自定义样式 <br/>
 *
 * @author liuk <br/>
 * @version v1.0 <br/>
 * @date 2017年7月06日 <br/>
 */
@Component
public class SpecialModuleSytleBeanService extends AbstractBeanService {

    @Autowired
    private ITplConfService tplConfService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Long tplId = paramObj.getLong("tplId");
        AssertUtil.isEmpty(tplId, "模板ID不能为空！");
        TemplateConfEO eo = tplConfService.getEntity(TemplateConfEO.class, tplId);
        return eo;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String objToStr(String content, Object resultObj, JSONObject paramObj) {
        TemplateConfEO templateConfEO = (TemplateConfEO) resultObj;
        String moduleSytle = "";
        if(!AppUtil.isEmpty(templateConfEO.getSpecialModuleSytle())){
            moduleSytle = templateConfEO.getSpecialModuleSytle();
        }
        return moduleSytle;
    }

}