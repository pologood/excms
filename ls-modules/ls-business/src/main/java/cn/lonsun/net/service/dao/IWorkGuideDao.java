package cn.lonsun.net.service.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.entity.CmsWorkGuideEO;
import cn.lonsun.site.template.internal.entity.ParamDto;

import java.util.List;

/**
 * @author gu.fei
 * @version 2015-11-18 13:44
 */
public interface IWorkGuideDao extends IBaseDao<CmsWorkGuideEO> {

    public List<CmsWorkGuideEO> getEOs();

    public CmsWorkGuideEO getByContentId(Long contentId);

    public List<CmsWorkGuideEO> getEOsByCIds(ParamDto dto,List<Long> cIds);

    public Pagination getPageEOsByCIds(ParamDto dto, List<Long> cIds);

    public Pagination getPageEOs(ParamDto vo);

    public Pagination getPageEOs(ParamDto dto,List<Long> cIds,String condition);

    public Pagination getPageEOs(ParamDto dto, Long organId,String name, String condition,String typeCode);

    public Pagination getPageSEOs(ParamDto dto, String organIds,String name, String condition,String typeCode);

    public Object publish(Long[] ids, Long publish);
}
