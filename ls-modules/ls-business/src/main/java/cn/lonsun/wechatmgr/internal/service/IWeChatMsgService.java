package cn.lonsun.wechatmgr.internal.service;


import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.wechatmgr.internal.entity.WechatMsgEO;
import cn.lonsun.wechatmgr.vo.MessageVO;
import cn.lonsun.wechatmgr.vo.UnitTopStatisVO;
import cn.lonsun.wechatmgr.vo.WeChatUserVO;

import java.util.List;

/**
 * @author gu.fei
 * @version 2016-09-29 14:33
 */
public interface IWeChatMsgService extends IMockService<WechatMsgEO> {
    Pagination getUserResponse(WeChatUserVO pageQueryVO);

    Pagination getUserTurn(WeChatUserVO pageQueryVO);

    List<String> getWeekCount(List<String> days,Integer isRep,Long siteId);

    List<UnitTopStatisVO> getUnitsCount(Long siteId);

    List<WechatMsgEO> getTodoJudge(MessageVO msg, Long siteId);
}
