package cn.lonsun.sms.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.sms.internal.entity.SendSmsEO;
import cn.lonsun.sms.vo.SmsQueryVO;

public interface ISendSmsService extends IBaseService<SendSmsEO>{

	/**
	 * 
	 * @param phone
	 * @param smsCode
	 * @return 1  通过  | 2  超时 | 3 验证码错误
	 */
	public Integer isTimeOut(String phone,String smsCode);

	Pagination getPage(SmsQueryVO query);
}
