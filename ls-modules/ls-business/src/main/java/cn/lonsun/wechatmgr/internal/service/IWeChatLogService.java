package cn.lonsun.wechatmgr.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.wechatmgr.internal.entity.WeChatLogEO;
import cn.lonsun.wechatmgr.vo.WeChatUserVO;

/**
 * Created by zhangchao on 2016/10/9.
 */
public interface IWeChatLogService extends IMockService<WeChatLogEO> {
    Pagination getPage(WeChatUserVO userVO);
}
