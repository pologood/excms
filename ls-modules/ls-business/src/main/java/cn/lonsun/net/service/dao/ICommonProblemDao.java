package cn.lonsun.net.service.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.entity.CmsCommonProblemEO;
import cn.lonsun.site.template.internal.entity.ParamDto;

/**
 * @author gu.fei
 * @version 2015-11-18 13:44
 */
public interface ICommonProblemDao extends IMockDao<CmsCommonProblemEO> {

    public Pagination getPageEntities(ParamDto dto);

    public Object publish(Long[] ids, Long publish);
}
