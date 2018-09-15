package cn.lonsun.wechatmgr.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.wechatmgr.internal.dao.IWeChatTurnDao;
import cn.lonsun.wechatmgr.internal.entity.WeChatTurnEO;
import cn.lonsun.wechatmgr.vo.WeChatProcessVO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lonsun on 2016-10-10.
 */
@Repository
public class WeChatTurnDaoImpl extends MockDao<WeChatTurnEO> implements IWeChatTurnDao {
    @Override
    public Pagination getInforByMsgId(WeChatProcessVO weChatProcessVO) {
        StringBuffer hql =new StringBuffer();
        List<Object> list =new ArrayList<Object>();
        hql.append("from WeChatTurnEO t where  t.siteId = ? and  t.recordStatus = ? and t.msgId = ? ");
        hql.append(" order by t.createDate desc");
        list.add(LoginPersonUtil.getSiteId());
        list.add(AMockEntity.RecordStatus.Normal.toString());
        list.add(weChatProcessVO.getId());
        return getPagination(weChatProcessVO.getPageIndex(),weChatProcessVO.getPageSize(),hql.toString(),list.toArray());
    }

    @Override
    public List<WeChatTurnEO> getProcessListNew(WeChatProcessVO weChatProcessVO) {
        StringBuffer hql =new StringBuffer();
        List<Object> list =new ArrayList<Object>();
        hql.append("from WeChatTurnEO t where  t.siteId = ? and  t.recordStatus = ? and t.msgId = ? ");
        hql.append(" order by t.createDate desc");
        list.add(LoginPersonUtil.getSiteId());
        list.add(AMockEntity.RecordStatus.Normal.toString());
        list.add(weChatProcessVO.getId());
        return getEntitiesByHql(hql.toString(),list.toArray());
    }
}
