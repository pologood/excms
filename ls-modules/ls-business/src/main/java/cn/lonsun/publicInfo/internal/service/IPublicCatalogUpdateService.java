package cn.lonsun.publicInfo.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogUpdateEO;
import cn.lonsun.publicInfo.vo.PublicCatalogUpdateQueryVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by fth on 2017/6/9.
 */
public interface IPublicCatalogUpdateService extends IBaseService<PublicCatalogUpdateEO> {

    Pagination getPagination(PublicCatalogUpdateQueryVO queryVO);

    int getCountByOrganId(PublicCatalogUpdateQueryVO queryVO);

    List<PublicCatalogEO> getEmptyCatalogByOrganId(Long organId);

    void export(PublicCatalogUpdateQueryVO queryVO, HttpServletResponse response);

    void exportEmptyCatalog(Long organId, HttpServletResponse response);

    void sendMessageByCurrentUser(Long userId);
}
