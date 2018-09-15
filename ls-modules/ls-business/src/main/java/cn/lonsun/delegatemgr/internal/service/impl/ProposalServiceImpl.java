package cn.lonsun.delegatemgr.internal.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.delegatemgr.entity.AdviceEO;
import cn.lonsun.delegatemgr.entity.ProposalEO;
import cn.lonsun.delegatemgr.internal.dao.IAdviceDao;
import cn.lonsun.delegatemgr.internal.dao.IProposalDao;
import cn.lonsun.delegatemgr.internal.service.IAdviceService;
import cn.lonsun.delegatemgr.internal.service.IProposalService;
import cn.lonsun.delegatemgr.vo.DelegateQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 建议议案Service实现层<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2017-6-28<br/>
 */
@Service("proposalService")
public class ProposalServiceImpl extends MockService<ProposalEO> implements IProposalService {
    @Autowired
    private IProposalDao proposalDao;

    /**
     * 根据搜索条件获取议案分页列表
     * @param queryVO
     * @return
     */
    @Override
    public Pagination getPage(DelegateQueryVO queryVO) {
        return proposalDao.getPage(queryVO);
    }

    /**
     * 根据届次获取议案列表
     * @param session
     * @param siteId
     * @return
     */
    @Override
    public List<ProposalEO> getBySession(String session, Long siteId) {
        return proposalDao.getBySession(session,siteId);
    }
}
