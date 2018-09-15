package cn.lonsun.wechatmgr.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.wechatmgr.internal.entity.WeChatLogEO;
import cn.lonsun.wechatmgr.vo.WeChatUserVO;

/**
 * Created by zhangchao on 2016/10/9.
 */
public interface IWeChatLogDao extends IMockDao<WeChatLogEO> {
    Pagination getPage(WeChatUserVO userVO);
}
