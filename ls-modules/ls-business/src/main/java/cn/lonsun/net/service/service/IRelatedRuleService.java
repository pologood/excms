package cn.lonsun.net.service.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.entity.CmsRelatedRuleEO;
import cn.lonsun.site.template.internal.entity.ParamDto;

import java.util.List;

/**
 * @author gu.fei
 * @version 2015-11-18 13:45
 */
public interface IRelatedRuleService extends IBaseService<CmsRelatedRuleEO> {

    public List<CmsRelatedRuleEO> getEOs();

    /*
    * 根据参数分页查询
    * */
    public Pagination getPageEOs(ParamDto dto);

    public String saveEO(CmsRelatedRuleEO eo, String cIds);

    public String updateEO(CmsRelatedRuleEO eo, String cIds);

    public String deleteEO(Long[] ids);

    public String publish(Long[] ids,Long publish);

}
