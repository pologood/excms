package cn.lonsun.content.onlinePetition.internal.dao.imp;

import org.springframework.stereotype.Repository;

import cn.lonsun.content.onlinePetition.internal.dao.IPetitionRecDao;
import cn.lonsun.content.onlinePetition.internal.entity.PetitionRecEO;
import cn.lonsun.core.base.dao.impl.MockDao;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-27<br/>
 */
@Repository("petitionRecDao")
public class PetitionRecDaoImpl extends MockDao<PetitionRecEO> implements IPetitionRecDao {
}
