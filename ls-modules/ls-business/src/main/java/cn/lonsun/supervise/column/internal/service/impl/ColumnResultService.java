package cn.lonsun.supervise.column.internal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.supervise.column.internal.dao.IColumnResultDao;
import cn.lonsun.supervise.column.internal.service.IColumnResultService;
import cn.lonsun.supervise.columnupdate.internal.entity.ColumnResultEO;
import cn.lonsun.supervise.vo.SupervisePageVO;

/**
 * @author gu.fei
 * @version 2016-4-5 10:48
 */
@Service
public class ColumnResultService extends MockService<ColumnResultEO> implements IColumnResultService {

    @Autowired
    private IColumnResultDao columnResultDao;

    @Override
    public Pagination getPageEOs(SupervisePageVO vo) {
        return columnResultDao.getPageEOs(vo);
    }

    @Override
    public void physDelEOs(Long taskId) {
        columnResultDao.physDelEOs(taskId);
    }
}
