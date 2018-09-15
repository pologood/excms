package cn.lonsun.system.systemlog.internal.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.systemlog.internal.dao.ICmsLoginHistoryDao;
import cn.lonsun.system.systemlog.internal.entity.CmsLoginHistoryEO;
import cn.lonsun.system.systemlog.internal.service.ICmsLoginHistoryService;

@Service("cmsLoginHistoryService")
public class CmsLoginHistoryServiceImpl extends MockService<CmsLoginHistoryEO>
		implements ICmsLoginHistoryService {
	@Autowired
	private ICmsLoginHistoryDao cmsLoginHistoryDao;
	
	@Override
	public Pagination getPage(Long pageIndex, Integer pageSize, Date startDate,
			Date endDate, String type, String key,Long siteId) {
		return (Pagination) cmsLoginHistoryDao.getPage(pageIndex, pageSize,
				startDate, endDate, type, key,siteId);
	}

	@Override
	public List<CmsLoginHistoryEO> getAllLogs(Date startDate, Date endDate,
			String type, String key,Long siteId) {
		return cmsLoginHistoryDao.getAllLogs(startDate, endDate, type, key,siteId);
	}
}
