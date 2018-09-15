package cn.lonsun.msg.submit.hn.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.msg.submit.hn.dao.IMsgToUserHnDao;
import cn.lonsun.msg.submit.hn.entity.CmsMsgToUserHnEO;
import cn.lonsun.msg.submit.hn.service.IMsgToUserHnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gu.fei
 * @version 2015-11-18 13:46
 */
@Service
public class MsgToUserHnService extends MockService<CmsMsgToUserHnEO> implements IMsgToUserHnService {

    @Autowired
    private IMsgToUserHnDao msgToUserHnDao;

    @Override
    public void deleteByMsgId(Long msgId) {
        msgToUserHnDao.deleteByMsgId(msgId);
    }
}
