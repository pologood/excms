package cn.lonsun.net.service.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.entity.CmsWorkGuideEO;
import cn.lonsun.site.template.internal.entity.ParamDto;

import java.util.List;

/**
 * @author gu.fei
 * @version 2015-11-18 13:45
 */
public interface IWorkGuideService extends IBaseService<CmsWorkGuideEO> {

    public List<CmsWorkGuideEO> getEOs();

    public CmsWorkGuideEO getByContentId(Long contentId);

    public List<CmsWorkGuideEO> getEOsByCIds(ParamDto dto,List<Long> cIds);

    public Pagination getPageEOsByCIds(ParamDto dto,List<Long> cIds);

    /*
    * 根据参数分页查询
    * */
    public Pagination getPageEOs(ParamDto dto);

    public Pagination getPageEOs(ParamDto dto,List<Long> cIds,String condition);

    public Pagination getPageEOs(ParamDto dto,Long organId,String name,String condition,String typeCode);

    public Pagination getPageSEOs(ParamDto dto,String organIds,String name,String condition,String typeCode);

    public String saveEO(CmsWorkGuideEO eo,String cIds);

    public void saveClassify(Long pid,String cIds);

    public String updateEO(CmsWorkGuideEO eo,String cIds);

    public String deleteEO(Long[] ids);

    public String publish(Long[] ids, Long publish);

}
