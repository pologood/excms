package cn.lonsun.msg.submit.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.msg.submit.dao.IMsgToColumnDao;
import cn.lonsun.msg.submit.entity.CmsMsgToColumnEO;

/**
 * @author gu.fei
 * @version 2015-11-18 13:46
 */
@Repository
public class MsgToColumnDao extends BaseDao<CmsMsgToColumnEO> implements IMsgToColumnDao {

    @Override
    public List<CmsMsgToColumnEO> getEOs() {
        return this.getEntitiesByHql("from CmsMsgToColumnEO", new Object[]{});
    }

    @Override
    public List<CmsMsgToColumnEO> getEOsByMsgId(Long msgId) {
        return this.getEntitiesByHql("from CmsMsgToColumnEO where msgId = ? order by createDate desc", new Object[]{msgId});
    }
}
