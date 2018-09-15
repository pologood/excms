package cn.lonsun.supervise.errhref.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.supervise.errhref.internal.entity.HrefResultEO;
import cn.lonsun.supervise.vo.SupervisePageVO;

import java.util.List;

/**
 * @author gu.fei
 * @version 2016-4-5 10:45
 */
public interface IHrefResultDao extends IMockDao<HrefResultEO> {

    /**
     * 分页查询
     * @param vo
     * @return
     */
    public Pagination getPageEOs(SupervisePageVO vo);

    /**
     * 根据任务ID查询
     * @param taskId
     * @return
     */
    public List<HrefResultEO> getByTaskId(Long taskId);

    /**
     * 物理删除任务
     * @param taskId
     */
    public void physDelEOs(Long taskId);

    /**
     * 物理删除
     * @param resultId
     */
    public void physDelEO(Long resultId);

    /**
     * @param taskId
     * @return
     */
    public Long getCountByTaskId(Long taskId);
}
