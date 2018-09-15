package cn.lonsun.content.optrecord.dao;

import cn.lonsun.content.optrecord.entity.ContentOptRecordEO;
import cn.lonsun.content.optrecord.vo.OptRecordQueryVO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;

/**
 * @author gu.fei
 * @version 2018-01-18 9:37
 */
public interface IContentOptRecordDao extends IMockDao<ContentOptRecordEO> {

    /**
     * 分页查询
     * @param vo
     * @return
     */
    Pagination getPage(OptRecordQueryVO vo);
}
