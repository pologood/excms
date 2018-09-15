package cn.lonsun.content.internal.service;


import cn.lonsun.content.internal.entity.GuestBookForwardRecordEO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;

public interface IForwardRecordService extends IMockService<GuestBookForwardRecordEO>{
	
	public void saveRecord(GuestBookForwardRecordEO eo);
	
	//public Object queryForwardRecord(Long guestBookId);
	
	//取留言记录数据
	public Pagination getRecord(Long pageIndex, Integer pageSize,Long id);

}
