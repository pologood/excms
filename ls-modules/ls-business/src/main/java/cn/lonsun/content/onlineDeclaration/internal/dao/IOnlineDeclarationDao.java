package cn.lonsun.content.onlineDeclaration.internal.dao;

import cn.lonsun.content.onlineDeclaration.internal.entity.OnlineDeclarationEO;
import cn.lonsun.content.onlineDeclaration.vo.DeclaQueryVO;
import cn.lonsun.content.onlineDeclaration.vo.OnlineDeclarationVO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;

import java.util.Date;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-6-12<br/>
 */

public interface IOnlineDeclarationDao extends IMockDao<OnlineDeclarationEO> {
    public Pagination getPage(DeclaQueryVO pageVO);

    public Pagination getFrontPage(DeclaQueryVO pageVO);

    public OnlineDeclarationVO getVO(Long id);

    public Long countList(Long siteId, String columnIds, Date startDate, Integer isPublish, Integer isResponse);

    public OnlineDeclarationVO searchByCode(String randomCode, String docNum, Long siteId);
}
