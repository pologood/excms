package cn.lonsun.msg.submit.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.msg.submit.entity.CmsMsgToColumnEO;

/**
 * @author gu.fei
 * @version 2015-11-18 13:44
 */
public interface IMsgToColumnDao extends IBaseDao<CmsMsgToColumnEO> {

    public List<CmsMsgToColumnEO> getEOs();

    public List<CmsMsgToColumnEO> getEOsByMsgId(Long msgId);

}
