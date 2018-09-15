package cn.lonsun.wechatmgr.internal.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.wechatmgr.internal.dao.IWeChatLogDao;
import cn.lonsun.wechatmgr.internal.entity.WeChatLogEO;
import cn.lonsun.wechatmgr.internal.service.IWeChatLogService;
import cn.lonsun.wechatmgr.vo.WeChatUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zhangchao on 2016/10/9.
 */
@Service("weChatLogService")
public class WeChatLogServiceImpl extends MockService<WeChatLogEO> implements IWeChatLogService{

    @Autowired
    private IWeChatLogDao weChatLogDao;


    @Override
    public Pagination getPage(WeChatUserVO userVO) {
        return weChatLogDao.getPage(userVO);
    }
}
