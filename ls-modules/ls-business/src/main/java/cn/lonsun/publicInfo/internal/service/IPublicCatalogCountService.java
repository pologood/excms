package cn.lonsun.publicInfo.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogCountEO;

import java.util.List;

/**
 * Created by fth on 2017/5/31.
 */
public interface IPublicCatalogCountService extends IBaseService<PublicCatalogCountEO> {

    List<PublicCatalogCountEO> getListByOrganId(Long organId);

    void updateOrganCatIdCountByStatus(Long organId, Long catId, Long increment, Integer status, boolean cascade);
}
