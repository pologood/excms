package cn.lonsun.datacollect.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datacollect.entity.HtmlCollectContentEO;
import cn.lonsun.datacollect.vo.CollectPageVO;
import cn.lonsun.datacollect.vo.ColumnVO;

/**
 * @author gu.fei
 * @version 2016-1-21 14:27
 */
public interface IHtmlCollectContentDao extends IMockDao<HtmlCollectContentEO> {

    /**
     * 分页查询采集任务数据
     * @param vo
     * @return
     */
    public Pagination getPageEOs(CollectPageVO vo);

    /**
     * @param taskId
     * @return
     */
    public List<HtmlCollectContentEO> getByTaskId(Long taskId);

    /**
     * 删除采集任务
     * @param ids
     */
    public void deleteEOs(Long[] ids);

    /**
     * 获取字段
     * @param tableName
     * @return
     */
    public List<ColumnVO> getColumns(String tableName);
}
