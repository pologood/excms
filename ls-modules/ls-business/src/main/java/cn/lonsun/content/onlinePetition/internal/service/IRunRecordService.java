package cn.lonsun.content.onlinePetition.internal.service;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.content.onlinePetition.internal.entity.RunRecordEO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-27<br/>
 */

public interface IRunRecordService extends IMockService<RunRecordEO>{
    public Pagination getPage(PageQueryVO pageVO,Long petitionId);
}
