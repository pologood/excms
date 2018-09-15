package cn.lonsun.sms.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.sms.internal.entity.SendSmsEO;
import cn.lonsun.sms.vo.SmsQueryVO;

public interface ISendSmsDao extends IBaseDao<SendSmsEO>{

	SendSmsEO getMaxTimeSms(String phone);

	Pagination getPage(SmsQueryVO query);
	
}
