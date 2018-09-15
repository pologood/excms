package cn.lonsun.delegatemgr.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.delegatemgr.entity.AdviceEO;
import cn.lonsun.delegatemgr.entity.ProposalEO;
import cn.lonsun.delegatemgr.vo.DelegateQueryVO;

import java.util.List;

/**
 * 建议议案service<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2017-6-28<br/>
 */
public interface IProposalService extends IMockService<ProposalEO> {
    /**
     * 根据搜索条件获取议案分页列表
     * @param queryVO
     * @return
     */
    public Pagination getPage(DelegateQueryVO queryVO);

    /**
     * 根据届次获取议案列表
     * @param session
     * @param siteId
     * @return
     */
    public List<ProposalEO> getBySession(String session, Long siteId);
}
