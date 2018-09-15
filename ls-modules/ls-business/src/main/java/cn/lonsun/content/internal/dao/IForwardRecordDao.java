package cn.lonsun.content.internal.dao;

import cn.lonsun.content.internal.entity.GuestBookForwardRecordEO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;

public interface IForwardRecordDao extends IMockDao<GuestBookForwardRecordEO>{
	public void saveRecord(GuestBookForwardRecordEO eo);
	
	//public Object queryForwardRecord(Long guestBookId);
	public Pagination getRecord(Long pageIndex, Integer pageSize,Long id);

}
