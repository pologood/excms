package cn.lonsun.msg.submit.hn.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.msg.submit.hn.entity.CmsMsgToColumnHnEO;
import cn.lonsun.site.template.internal.entity.ParamDto;

/**
 * @author gu.fei
 * @version 2015-11-18 13:44
 */
public interface IMsgToColumnHnDao extends IMockDao<CmsMsgToColumnHnEO> {

    /**
     * 根据消息ID删除
     * @param msgId
     */
    void deleteByMsgId(Long msgId);

    /**
     * 获取已发布的栏目列表
     * @param msgId
     * @return
     */
    Pagination getColumnPageList(Long msgId, ParamDto dto);
}
