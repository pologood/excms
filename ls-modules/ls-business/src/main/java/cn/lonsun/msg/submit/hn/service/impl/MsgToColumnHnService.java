package cn.lonsun.msg.submit.hn.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.msg.submit.hn.dao.IMsgToColumnHnDao;
import cn.lonsun.msg.submit.hn.entity.CmsMsgToColumnHnEO;
import cn.lonsun.msg.submit.hn.service.IMsgToColumnHnService;
import cn.lonsun.site.template.internal.entity.ParamDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gu.fei
 * @version 2015-11-18 13:46
 */
@Service
public class MsgToColumnHnService extends MockService<CmsMsgToColumnHnEO> implements IMsgToColumnHnService {

    @Autowired
    private IMsgToColumnHnDao msgToColumnHnDao;

    @Override
    public void deleteByMsgId(Long msgId) {
        msgToColumnHnDao.deleteByMsgId(msgId);
    }

    @Override
    public Pagination getColumnPageList(Long msgId, ParamDto dto) {
        return msgToColumnHnDao.getColumnPageList(msgId,dto);
    }
}
