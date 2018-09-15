package cn.lonsun.wechatmgr.internal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.SysLog;
import cn.lonsun.wechatmgr.internal.dao.IUserGroupDao;
import cn.lonsun.wechatmgr.internal.entity.UserGroupEO;
import cn.lonsun.wechatmgr.internal.service.IUserGroupService;

@Service("userGroupService")
public class UserGroupServiceImpl extends MockService<UserGroupEO> implements
		IUserGroupService {

	@Autowired
	private IUserGroupDao userGroupDao;
	@Override
	public List<UserGroupEO> getListBySite(Long siteId) {
		return userGroupDao.getListBySite(siteId);
	}
	@Override
	public Pagination getpage(Long siteId, Long pageIndex, Integer pageSize) {
		return userGroupDao.getpage(siteId, pageIndex, pageSize);
	}
	@Override
	public void deleteBySite(Long siteId) {
		userGroupDao.deleteBySite(siteId);
		SysLog.log("删除站点下全部微信用户分组 ", "UserGroupEO", CmsLogEO.Operation.Delete.toString());
	}
	
}
