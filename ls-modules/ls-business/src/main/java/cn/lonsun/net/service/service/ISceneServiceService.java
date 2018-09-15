package cn.lonsun.net.service.service;

import java.util.List;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.entity.CmsSceneServiceEO;
import cn.lonsun.site.template.internal.entity.ParamDto;

/**
 * @author gu.fei
 * @version 2015-11-18 13:45
 */
public interface ISceneServiceService extends IBaseService<CmsSceneServiceEO> {

    public List<CmsSceneServiceEO> getEOs();

    /*
    * 根据参数分页查询
    * */
    public Pagination getPageEOs(ParamDto dto);

    public void saveEO(CmsSceneServiceEO eo);

    public void updateEO(CmsSceneServiceEO eo);

    public void deleteEO(Long id);

    public Object publish(Long[] ids, Long publish);

}
