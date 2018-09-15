package cn.lonsun.datacollect.service;

import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datacollect.entity.DBCollectColumnEO;
import cn.lonsun.datacollect.vo.CollectPageVO;

/**
 * @author gu.fei
 * @version 2016-1-21 14:31
 */
public interface IDBCollectColumnService extends IMockService<DBCollectColumnEO> {

    /**
     * 分页查询采集任务数据
     * @param vo
     * @return
     */
    public Pagination getPageEOs(CollectPageVO vo);

    /**
     * 根据任务ID查询
     * @param taskId
     * @return
     */
    public List<DBCollectColumnEO> getEOsByTaskId(Long taskId);

    /**
     * 保存采集任务
     * @param eo
     */
    public void saveEO(DBCollectColumnEO eo);

    /**
     * 更新采集任务
     * @param eo
     */
    public void updateEO(DBCollectColumnEO eo);

    /**
     * 删除采集任务
     * @param ids
     */
    public void deleteEOs(Long[] ids);
}
