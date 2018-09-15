package cn.lonsun.wechatmgr.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.wechatmgr.internal.dao.IWeChatLogDao;
import cn.lonsun.wechatmgr.internal.entity.WeChatLogEO;
import cn.lonsun.wechatmgr.vo.WeChatUserVO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangchao on 2016/10/9.
 */
@Repository
public class WeChatLogDaoImpl extends MockDao<WeChatLogEO> implements IWeChatLogDao {

    @Override
    public Pagination getPage(WeChatUserVO userVO) {
        List<Object> values = new ArrayList<Object>();
        StringBuffer hql=new StringBuffer("from WeChatLogEO where recordStatus='Normal'");
        if(!AppUtil.isEmpty(userVO.getSiteId())){
            hql.append(" and siteId = ?");
            values.add(userVO.getSiteId());
        }
        if(!AppUtil.isEmpty(userVO.getNickname())){
            hql.append(" and nickname like ? escape'\\'");
            values.add("%".concat(userVO.getNickname()).concat("%"));
        }
        hql.append(" order by createDate desc");
        return getPagination(userVO.getPageIndex(), userVO.getPageSize(), hql.toString(), values.toArray());
    }
}
