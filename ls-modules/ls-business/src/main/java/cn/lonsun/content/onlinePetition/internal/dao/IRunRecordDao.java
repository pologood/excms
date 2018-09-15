package cn.lonsun.content.onlinePetition.internal.dao;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.content.onlinePetition.internal.entity.RunRecordEO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-27<br/>
 */

public interface IRunRecordDao extends IMockDao<RunRecordEO> {
    public Pagination getPage(PageQueryVO pageVO,Long petitionId);
}
