package cn.lonsun.site.template.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.site.template.internal.entity.TemplateConfEO;

import java.util.List;

/**
 * Created by zhushouyong on 2017-9-25.
 */
public interface ITplConfSpecialService extends IBaseService<TemplateConfEO> {


    public List<TemplateConfEO> getSpecialById(Long siteId, Long specialId, String tempType);

    public List<TemplateConfEO> saveEO(TemplateConfEO eo);

    public Object delEO(Long id);

}
