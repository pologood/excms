package cn.lonsun.msg.submit.hn.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.msg.submit.hn.dao.IMsgToUserHnDao;
import cn.lonsun.msg.submit.hn.entity.CmsMsgToUserHnEO;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2015-11-18 13:46
 */
@Repository
public class MsgToUserHnDao extends MockDao<CmsMsgToUserHnEO> implements IMsgToUserHnDao {

    @Override
    public void deleteByMsgId(Long msgId) {
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("msgId",msgId);
        this.executeUpdateByJpql("delete from CmsMsgToUserHnEO where msgId = :msgId",param);
    }
}
