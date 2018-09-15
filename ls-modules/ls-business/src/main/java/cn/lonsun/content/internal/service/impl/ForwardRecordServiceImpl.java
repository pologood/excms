package cn.lonsun.content.internal.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.content.internal.dao.IForwardRecordDao;
import cn.lonsun.content.internal.entity.GuestBookForwardRecordEO;
import cn.lonsun.content.internal.service.IForwardRecordService;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;

@Service("forwardRecordService")
public class ForwardRecordServiceImpl extends MockService<GuestBookForwardRecordEO> implements IForwardRecordService {

	@Autowired
	private IForwardRecordDao forwardRecordDao;
	
	@Override
	public void saveRecord(GuestBookForwardRecordEO eo) {
		forwardRecordDao.save(eo);
		
	}

	@Override
	public Pagination getRecord(Long pageIndex, Integer pageSize,Long id) {
		return forwardRecordDao.getRecord(pageIndex, pageSize,id);
	}
	
	

	

}
