package cn.lonsun.supervise.errhref.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.supervise.columnupdate.internal.entity.CronConfEO;
import cn.lonsun.supervise.errhref.internal.entity.ErrHrefEO;
import cn.lonsun.supervise.vo.SupervisePageVO;

/**
 * @author gu.fei
 * @version 2016-4-5 10:47
 */
public interface IErrHrefService extends IMockService<ErrHrefEO> {

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
    public void saveTask(ErrHrefEO updateEO, CronConfEO cronEO);

    /**
     * 更改任务
     * @param updateEO
     * @param cronEO
     */
    public void updateTask(ErrHrefEO updateEO, CronConfEO cronEO);

    /**
     * 执行任务
     * @param taskId
     */
    public void execTask(Long taskId);

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

    /**
     * 重新检测
     * @param resultId
     * @return
     */
    public int recheck(Long resultId);
}
