package cn.lonsun.supervise.errhref.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.supervise.errhref.internal.entity.ErrHrefEO;
import cn.lonsun.supervise.vo.SupervisePageVO;

/**
 * @author gu.fei
 * @version 2016-4-5 10:45
 */
public interface IErrHrefDao extends IMockDao<ErrHrefEO> {

    /**
     * 分页查询
     * @param vo
     * @return
     */
    public Pagination getPageEOs(SupervisePageVO vo);

    /**
     * 物理删除任务
     * @param ids
     */
    public void physDelEOs(Long[] ids);
}
