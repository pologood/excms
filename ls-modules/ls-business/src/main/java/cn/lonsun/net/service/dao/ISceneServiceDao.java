package cn.lonsun.net.service.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.entity.CmsSceneServiceEO;
import cn.lonsun.site.template.internal.entity.ParamDto;

/**
 * @author gu.fei
 * @version 2015-11-18 13:44
 */
public interface ISceneServiceDao extends IBaseDao<CmsSceneServiceEO> {

    public List<CmsSceneServiceEO> getEOs();

    public Pagination getPageEOs(ParamDto vo);

    public Object publish(Long[] ids, Long publish);
}
