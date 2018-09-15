package cn.lonsun.process.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.process.entity.FormHandleRecordEO;
import cn.lonsun.process.vo.ProcessFormQueryVO;

/**
 * Created by zhu124866 on 2015-12-23.
 */
public interface IFormHandleRecordDao extends IBaseDao<FormHandleRecordEO> {


    /**
     * 获取分页数据
     * @param queryVO
     * @return
     */
    Pagination getFormHandleRecordPagination(ProcessFormQueryVO queryVO);


}
