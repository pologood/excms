package cn.lonsun.supervise.column.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.supervise.columnupdate.internal.entity.UnreplyGuestEO;

/**
 * @author gu.fei
 * @version 2016-4-5 10:45
 */
public interface IUnreplyGuestService extends IMockService<UnreplyGuestEO> {

    /**
     * 分页查询
     * @param dto
     * @return
     */
    public Pagination getPageEOs(ParamDto dto);

    /**
     * 物理删除
     * @param columnId
     */
    void delete(Long columnId);
}
