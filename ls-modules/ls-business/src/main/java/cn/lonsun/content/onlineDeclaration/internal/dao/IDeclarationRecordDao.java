package cn.lonsun.content.onlineDeclaration.internal.dao;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.content.onlineDeclaration.internal.entity.DeclarationRecordEO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-6-13<br/>
 */

public interface IDeclarationRecordDao extends IMockDao<DeclarationRecordEO> {
    public Pagination getPage(PageQueryVO pageVO, Long declarationId);
}
