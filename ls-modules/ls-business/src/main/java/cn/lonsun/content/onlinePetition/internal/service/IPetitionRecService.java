package cn.lonsun.content.onlinePetition.internal.service;

import cn.lonsun.content.onlinePetition.internal.entity.PetitionRecEO;
import cn.lonsun.core.base.service.IMockService;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-27<br/>
 */

public interface IPetitionRecService extends IMockService<PetitionRecEO>{

    public PetitionRecEO saveReply(PetitionRecEO recVO);

}
