package cn.lonsun.supervise.column.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.supervise.columnupdate.internal.entity.ColumnUpdateEO;
import cn.lonsun.supervise.columnupdate.internal.entity.CronConfEO;
import cn.lonsun.supervise.vo.SupervisePageVO;

/**
 * @author gu.fei
 * @version 2016-4-5 10:47
 */
public interface IColumnUpdateService extends IMockService<ColumnUpdateEO> {

    /**
     * 分页查询
     * @param vo
     * @return
     */
    public Pagination getPageEOs(SupervisePageVO vo);

    /**
     * 保存任务
     * @param updateEO
     * @param cronEO
     */
    public void saveTask(ColumnUpdateEO updateEO,CronConfEO cronEO);

    /**
     * 更改任务
     * @param updateEO
     * @param cronEO
     */
    public void updateTask(ColumnUpdateEO updateEO,CronConfEO cronEO);

    /**
     * 执行任务
     * @param schedId
     */
    public void execTask(Long schedId);

    /**
     * 停止任务
     * @param taskId
     */
    public void pauseTask(Long taskId);

    /**
     * 恢复任务
     * @param taskId
     */
    public void resumeTask(Long taskId);

    /**
     * 物理删除任务
     * @param ids
     */
    public void physDelEOs(Long[] ids);

}
