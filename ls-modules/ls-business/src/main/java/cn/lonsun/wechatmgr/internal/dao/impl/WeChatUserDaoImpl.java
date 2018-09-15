package cn.lonsun.wechatmgr.internal.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity.RecordStatus;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.wechatmgr.internal.dao.IWeChatUserDao;
import cn.lonsun.wechatmgr.internal.entity.WeChatUserEO;
import cn.lonsun.wechatmgr.vo.WeChatUserVO;

@Repository("weChatUserDao")
public class WeChatUserDaoImpl extends MockDao<WeChatUserEO> implements IWeChatUserDao {

	@Override
	public void deleteUserByOpenId(String openid) {
			String hql="update WeChatUserEO set recordStatus=? where openid=?";
			executeUpdateByHql(hql, new Object[]{RecordStatus.Removed.toString(),openid});
	}

	@Override
	public WeChatUserEO getUserByOpenId(String openid) {
		String hql="from WeChatUserEO where openid=?";
		return getEntityByHql(hql, new Object[]{openid});
	}

	@Override
	public Pagination getUserPage(WeChatUserVO uservo) {
		StringBuffer hql=new StringBuffer("from WeChatUserEO where recordStatus='Normal'");
		Map<String,Object> map=new HashMap<String,Object>();
		if(!AppUtil.isEmpty(uservo.getSiteId())){
			hql.append(" and siteId=:siteId");
			map.put("siteId", uservo.getSiteId());
		}
		if(!AppUtil.isEmpty(uservo.getNickname())){
			hql.append(" and nickname like :nickname escape'\\'");
			map.put("nickname","%".concat(uservo.getNickname()).concat("%"));
		}
		if(!AppUtil.isEmpty(uservo.getGroupid())){
			hql.append(" and groupid=:groupid");
			map.put("groupid", uservo.getGroupid());
		}
		hql.append(" order by createDate desc");
		return getPagination(uservo.getPageIndex(), uservo.getPageSize(), hql.toString(), map);
	}

	@Override
	public void deleteBySite(Long siteId) {
		String hql="delete WeChatUserEO where siteId=?";
		executeUpdateByHql(hql, new Object[]{siteId});
	}

	@Override
	public void updateGroupByOpenid(String[] openid, Long groupid) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("groupid", groupid);
		map.put("openid", openid);
		String hql="update WeChatUserEO set groupid=:groupid where openid in (:openid)";
		executeUpdateByJpql(hql, map);		
	}

	@Override
	public WeChatUserEO getUserByName(String originUserName) {
		StringBuffer hql=new StringBuffer("from WeChatUserEO where recordStatus= ? and openid =? and siteId =? ");
        List<Object> param =new ArrayList<Object>();
		param.add(AMockEntity.RecordStatus.Normal.toString());
		param.add(originUserName);
		param.add(LoginPersonUtil.getSiteId());
		return getEntityByHql(hql.toString(),param.toArray());
	}

}
