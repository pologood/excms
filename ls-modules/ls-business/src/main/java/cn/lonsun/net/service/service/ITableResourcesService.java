package cn.lonsun.net.service.service;

import java.util.List;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.entity.CmsTableResourcesEO;
import cn.lonsun.site.template.internal.entity.ParamDto;

/**
 * @author gu.fei
 * @version 2015-11-18 13:45
 */
public interface ITableResourcesService extends IBaseService<CmsTableResourcesEO> {

    public List<CmsTableResourcesEO> getEOs();

    /*
    * 根据参数分页查询
    * */
    public Pagination getPageEOs(ParamDto dto);

    public void saveEO(CmsTableResourcesEO eo, String cIds);

    public void updateEO(CmsTableResourcesEO eo, String cIds);

    public void deleteEO(Long id);

}
