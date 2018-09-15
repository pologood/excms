package cn.lonsun.datacollect.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datacollect.entity.DBCollectDataEO;
import cn.lonsun.datacollect.vo.CollectPageVO;

/**
 * @author gu.fei
 * @version 2016-1-21 14:27
 */
public interface IDBCollectDataDao extends IMockDao<DBCollectDataEO> {

    /**
     * 分页查询采集任务数据
     * @param vo
     * @return
     */
    public Pagination getPageEOs(CollectPageVO vo);

    /**
     * @param taskId
     */
    public void deleteByTaskId(Long taskId);

    /**
     * @param id
     */
    public void deleteById(Long id);
}
