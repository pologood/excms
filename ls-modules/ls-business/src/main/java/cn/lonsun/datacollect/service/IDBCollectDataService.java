package cn.lonsun.datacollect.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datacollect.entity.DBCollectDataEO;
import cn.lonsun.datacollect.vo.CollectPageVO;

/**
 * @author gu.fei
 * @version 2016-1-21 14:31
 */
public interface IDBCollectDataService extends IMockService<DBCollectDataEO> {

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
    public void saveEO(DBCollectDataEO eo);

    /**
     * @param taskId
     */
    public void deleteByTaskId(Long taskId);

    /**
     * 引用数据到栏目中
     * @param columnId
     * @param cSiteId
     * @param ids
     */
    public void quoteData(Long columnId,Long cSiteId,Long[] ids);
}
