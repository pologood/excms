package cn.lonsun.process.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.process.entity.ProcessFormEO;
import cn.lonsun.process.vo.ProcessFormQueryVO;

/**
 * Created by zhu124866 on 2015-12-18.
 */
public interface IProcessFormDao extends IBaseDao<ProcessFormEO> {


    /**
     * 获取分页列表
     * @param queryVO
     * @return
     */
    Pagination getPagination(ProcessFormQueryVO queryVO);

    /**
     * 获取表单标题是否重复
     * @param processFormId
     * @param title
     * @return
     */
    boolean getTitleIsExist(Long processFormId, String title);
}
