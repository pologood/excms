package cn.lonsun.datacollect.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datacollect.entity.HtmlCollectTaskEO;
import cn.lonsun.datacollect.vo.CollectPageVO;
import cn.lonsun.supervise.columnupdate.internal.entity.CronConfEO;

/**
 * @author gu.fei
 * @version 2016-1-21 14:31
 */
public interface IHtmlCollectTaskService extends IMockService<HtmlCollectTaskEO> {

    /**
     * 分页查询采集任务数据
     * @param vo
     * @return
     */
    public Pagination getPageEOs(CollectPageVO vo);

    /**
     * 保存采集任务
     * @param eo
     */
    public void saveEO(HtmlCollectTaskEO eo);

    /**
     * 更新采集任务
     * @param eo
     */
    public void updateEO(HtmlCollectTaskEO eo);

    /**
     * 复制采集任务
     * @param eo
     */
    public void copyEO(HtmlCollectTaskEO eo);

    /**
     * 删除采集任务
     * @param ids
     */
    public void deleteEOs(Long[] ids);

    /**
     * 保存定时配置
     * @param cronEO
     * @param taskId
     */
    public void saveTaskCron(CronConfEO cronEO,Long taskId);

    /**
     * 修改定时配置
     * @param cronEO
     * @param taskId
     */
    public void updateTaskCron(CronConfEO cronEO,Long taskId);

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

}
