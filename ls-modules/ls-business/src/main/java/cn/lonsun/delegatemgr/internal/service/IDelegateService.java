package cn.lonsun.delegatemgr.internal.service;

import cn.lonsun.core.base.service.IMockService;
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
public interface IDelegateService extends IMockService<DelegateEO> {
    public Pagination getPage(DelegateQueryVO queryVO);

    public List<DelegateEO> orderByDelegation(DelegateQueryVO siteId);
}
