package cn.lonsun.datacollect.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datacollect.entity.HtmlCollectTaskEO;
import cn.lonsun.datacollect.vo.CollectPageVO;

/**
 * @author gu.fei
 * @version 2016-1-21 14:27
 */
public interface IHtmlCollectTaskDao extends IMockDao<HtmlCollectTaskEO> {

    /**
     * 分页查询采集任务数据
     * @param vo
     * @return
     */
    public Pagination getPageEOs(CollectPageVO vo);

    /**
     * 删除采集任务
     * @param ids
     */
    public void deleteEOs(Long[] ids);
}
