package cn.lonsun.content.onlinePetition.internal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.content.onlinePetition.internal.dao.IRunRecordDao;
import cn.lonsun.content.onlinePetition.internal.entity.RunRecordEO;
import cn.lonsun.content.onlinePetition.internal.service.IRunRecordService;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-27<br/>
 */
@Service("runRecordService")
public class RunRecordServiceImpl extends MockService<RunRecordEO> implements IRunRecordService {
    @Autowired
    private IRunRecordDao recordDao;
    @Override
    public Pagination getPage(PageQueryVO pageVO,Long petitionId) {
        return recordDao.getPage(pageVO,petitionId);
    }
}
