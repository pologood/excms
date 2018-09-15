package cn.lonsun.site.template.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.site.template.internal.entity.TemplateConfEO;

import java.util.List;

/**
 * Created by zhushouyong on 2017-9-26.
 */
public interface ITplConfSpecialDao extends IBaseDao<TemplateConfEO> {

    public List<TemplateConfEO> getSpecialById(Long siteId, Long specialId, String tempType);

    public Object delEO(Long id);
}
