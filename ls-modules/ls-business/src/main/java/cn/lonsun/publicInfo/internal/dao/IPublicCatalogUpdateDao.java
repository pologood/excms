package cn.lonsun.publicInfo.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogUpdateEO;
import cn.lonsun.publicInfo.vo.PublicCatalogUpdateQueryVO;

import java.util.List;

/**
 * Created by fth on 2017/6/9.
 */
public interface IPublicCatalogUpdateDao extends IBaseDao<PublicCatalogUpdateEO> {

    Pagination getPagination(PublicCatalogUpdateQueryVO queryVO);

    int getCountByOrganId(PublicCatalogUpdateQueryVO queryVO);

    List<PublicCatalogUpdateEO> getList(PublicCatalogUpdateQueryVO queryVO);
}
