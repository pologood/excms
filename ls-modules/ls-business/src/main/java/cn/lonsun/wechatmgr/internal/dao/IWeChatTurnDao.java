package cn.lonsun.wechatmgr.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.wechatmgr.internal.entity.WeChatTurnEO;
import cn.lonsun.wechatmgr.vo.WeChatProcessVO;

import java.util.List;

/**
 * Created by lonsun on 2016-10-10.
 */
public interface IWeChatTurnDao extends IMockDao<WeChatTurnEO> {
    Pagination getInforByMsgId(WeChatProcessVO weChatProcessVO);

    List<WeChatTurnEO> getProcessListNew(WeChatProcessVO weChatProcessVO);
}
