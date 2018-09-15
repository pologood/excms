package cn.lonsun.supervise.column.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.supervise.columnupdate.internal.entity.ColumnUpdateWarnEO;
import cn.lonsun.supervise.vo.SupervisePageVO;

/**
 * @author gu.fei
 * @version 2016-4-5 10:47
 */
public interface IColumnUpdateWarnService extends IMockService<ColumnUpdateWarnEO> {

    /**
     * 分页查询
     * @param vo
     * @return
     */
    public Pagination getPageTasks(SupervisePageVO vo);

    /**
     * 保存或者修改
     * @param eo
     */
    public void saveOrUpdateColumnUpdateWarnEntity(ColumnUpdateWarnEO eo);
}
