package cn.lonsun.content.onlineDeclaration.internal.service;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.content.onlineDeclaration.internal.entity.DeclarationRecordEO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-6-13<br/>
 */

public interface IDeclarationRecordService extends IMockService<DeclarationRecordEO> {
    public Pagination getPage(PageQueryVO pageVO, Long declarationId);
}
