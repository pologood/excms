package cn.lonsun.delegatemgr.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.delegatemgr.entity.DelegateEO;
import cn.lonsun.delegatemgr.vo.DelegateQueryVO;

import java.util.List;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2017-6-6<br/>
 */
public interface IDelegateDao extends IMockDao<DelegateEO> {

    /**
     * 获取分页信息
     * @param queryVO
     * @return
     */
    public Pagination getPage(DelegateQueryVO queryVO);

    public List<DelegateEO> orderByDelegation(DelegateQueryVO queryVO);
}
