package cn.lonsun.wechatmgr.internal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.SysLog;
import cn.lonsun.wechatmgr.internal.dao.IWeChatMenuDao;
import cn.lonsun.wechatmgr.internal.entity.WeChatMenuEO;
import cn.lonsun.wechatmgr.internal.service.IWeChatMenuService;

@Service("weChatMenuService")
public class WeChatMenuServiceImpl extends MockService<WeChatMenuEO> implements
		IWeChatMenuService {
	@Autowired
	private IWeChatMenuDao weChatMenuDao;

	@Override
	public List<WeChatMenuEO> get1Leve(Long siteId) {
		return weChatMenuDao.get1Leve(siteId);
	}

	@Override
	public List<WeChatMenuEO> get2Leve(Long pId) {
		return weChatMenuDao.get2Leve(pId);
	}

	@Override
	public List<WeChatMenuEO> getMenuBySite(Long siteId) {
		return weChatMenuDao.getMenuBySite(siteId);
	}

	@Override
	public void deleteMenu(Long id) {
		weChatMenuDao.deleteMenu(id);
		SysLog.log("删除微信菜单 >> ID："+id, "WeChatMenuEO", CmsLogEO.Operation.Delete.toString());
	}
	
}
