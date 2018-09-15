package cn.lonsun.wechatmgr.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.wechatmgr.internal.entity.WechatMsgEO;
import cn.lonsun.wechatmgr.vo.MessageVO;
import cn.lonsun.wechatmgr.vo.WeChatUserVO;

import java.util.List;

/**
 * @author gu.fei
 * @version 2016-09-29 14:34
 */
public interface IWeChatMsgDao extends IMockDao<WechatMsgEO>  {
    Pagination getUserResponse(WeChatUserVO pageQueryVO);

    Pagination getUserTurn(WeChatUserVO pageQueryVO);

    List<Object> getWeekCount(List<String> days,Integer isRep,Long siteId);

    List<Object> getUnitsCount(Long siteId);

    List<WechatMsgEO> getTodoJudge(MessageVO msg, Long siteId);
}
