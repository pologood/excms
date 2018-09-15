package cn.lonsun.wechatmgr.internal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.wechatmgr.internal.dao.IWeChatUserDao;
import cn.lonsun.wechatmgr.internal.entity.WeChatUserEO;
import cn.lonsun.wechatmgr.internal.service.IWeChatUserService;
import cn.lonsun.wechatmgr.vo.WeChatUserVO;

@Service("weChatUserService")
public class WeChatUserServiceImpl extends MockService<WeChatUserEO> implements
		IWeChatUserService {

	@Autowired
	private IWeChatUserDao weChatUserDao;
	@Override
	public void deleteUserByOpenId(String openid) {
		weChatUserDao.deleteUserByOpenId(openid);
	}

	@Override
	public WeChatUserEO getUserByOpenId(String openid) {
		return weChatUserDao.getUserByOpenId(openid);
	}
	
	@Override
	public void recordUser(WeChatUserEO user) {
		WeChatUserEO userEO = weChatUserDao.getUserByOpenId(user.getOpenid());
		if(AppUtil.isEmpty(userEO)){
			saveEntity(user);
		}else{
			user.setId(userEO.getId());
			updateEntity(user);
		}
	}

	@Override
	public Pagination getUserPage(WeChatUserVO uservo) {
		return weChatUserDao.getUserPage(uservo);
	}

	@Override
	public void removeBySite(Long siteId) {
		weChatUserDao.deleteBySite(siteId);
	}

	@Override
	public void updateGroupByOpenid(String[] openid, Long groupid) {
		weChatUserDao.updateGroupByOpenid(openid, groupid);
	}

	@Override
	public void synUser(WeChatUserEO user) {
		weChatUserDao.save(user);
	}

	@Override
	public WeChatUserEO getUserByName(String originUserName) {
		return weChatUserDao.getUserByName(originUserName);
	}
}
