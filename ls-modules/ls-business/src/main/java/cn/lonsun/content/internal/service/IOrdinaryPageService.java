package cn.lonsun.content.internal.service;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.vo.OrdinaryPageVO;
import cn.lonsun.core.base.service.IMockService;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-18<br/>
 */

public interface IOrdinaryPageService extends IMockService<BaseContentEO> {

    public BaseContentEO saveEO(OrdinaryPageVO vo);
}
