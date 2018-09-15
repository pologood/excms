package cn.lonsun.content.onlineDeclaration.internal.service;

import cn.lonsun.content.onlineDeclaration.internal.entity.OnlineDeclarationEO;
import cn.lonsun.content.onlineDeclaration.vo.DeclaQueryVO;
import cn.lonsun.content.onlineDeclaration.vo.OnlineDeclarationVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;

import java.util.Date;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-6-12<br/>
 */

public interface IOnlineDeclarationService extends IMockService<OnlineDeclarationEO> {
    public Pagination getPage(DeclaQueryVO pageVO);

    public Pagination getFrontPage(DeclaQueryVO pageVO);

    public OnlineDeclarationEO saveVO(OnlineDeclarationVO vo);

    public OnlineDeclarationVO getVO(Long id);

    public void deleteVOs(String ids);

    public void transfer(Long declarationId, Long recUnitId, String recUnitName, String remark);

    public OnlineDeclarationVO searchByCode(String randomCode,String docNum,Long siteId);

    public Long countList(Long siteId, String columnIds, Date startDate, Integer isPublish, Integer isResponse) ;

    }
