package cn.lonsun.delegatemgr.internal.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.delegatemgr.entity.DelegateEO;
import cn.lonsun.delegatemgr.internal.dao.IDelegateDao;
import cn.lonsun.delegatemgr.internal.service.IDelegateService;
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
@Service("delegateService")
public class DelegateServiceImpl extends MockService<DelegateEO> implements IDelegateService {

    @Autowired
    private IDelegateDao delegateDao;

    @Override
    public Pagination getPage(DelegateQueryVO queryVO) {
        return delegateDao.getPage(queryVO);
    }

    @Override
    public List<DelegateEO> orderByDelegation(DelegateQueryVO queryVO) {
        return delegateDao.orderByDelegation(queryVO);
    }

}
