package cn.lonsun.delegatemgr.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.delegatemgr.entity.AdviceEO;
import cn.lonsun.delegatemgr.vo.DelegateQueryVO;

import java.util.List;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2017-6-6<br/>
 */
public interface IAdviceDao extends IMockDao<AdviceEO> {
    public Pagination getPage(DelegateQueryVO queryVO);

    public List<AdviceEO> getBySession(String session, Long siteId);
}
