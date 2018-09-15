package cn.lonsun.net.service.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.entity.CmsTableResourcesEO;
import cn.lonsun.site.template.internal.entity.ParamDto;

/**
 * @author gu.fei
 * @version 2015-11-18 13:44
 */
public interface ITableResourcesDao extends IBaseDao<CmsTableResourcesEO> {

    public List<CmsTableResourcesEO> getEOs();

    public Pagination getPageEOs(ParamDto vo);
}
