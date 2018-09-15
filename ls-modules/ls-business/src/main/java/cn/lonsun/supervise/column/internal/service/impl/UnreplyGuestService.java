package cn.lonsun.supervise.column.internal.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.supervise.column.internal.dao.IUnreplyGuestDao;
import cn.lonsun.supervise.column.internal.service.IUnreplyGuestService;
import cn.lonsun.supervise.columnupdate.internal.entity.UnreplyGuestEO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gu.fei
 * @version 2016-4-5 10:46
 */
@Service
public class UnreplyGuestService extends MockService<UnreplyGuestEO> implements IUnreplyGuestService {

    @Autowired
    private IUnreplyGuestDao unreplyGuestDao;

    @Override
    public Pagination getPageEOs(ParamDto dto) {
        return unreplyGuestDao.getPageEOs(dto);
    }

    @Override
    public void delete(Long columnId) {
        unreplyGuestDao.delete(columnId);
    }
}
