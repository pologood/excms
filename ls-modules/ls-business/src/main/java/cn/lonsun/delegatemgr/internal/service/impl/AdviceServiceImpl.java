package cn.lonsun.delegatemgr.internal.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.delegatemgr.entity.AdviceEO;
import cn.lonsun.delegatemgr.internal.dao.IAdviceDao;
import cn.lonsun.delegatemgr.internal.service.IAdviceService;
import cn.lonsun.delegatemgr.vo.DelegateQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2017-6-6<br/>
 */
@Service("adviceService")
public class AdviceServiceImpl extends MockService<AdviceEO> implements IAdviceService{

    @Autowired
    private IAdviceDao adviceDao;

    @Override
    public Pagination getPage(DelegateQueryVO queryVO) {
        return adviceDao.getPage(queryVO);
    }

    @Override
    public List<AdviceEO> getBySession(String session, Long siteId) {
        return adviceDao.getBySession(session,siteId);
    }
}
