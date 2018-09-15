package cn.lonsun.sms.internal.service.impl;


import java.util.Date;

import cn.lonsun.core.util.Pagination;
import cn.lonsun.sms.vo.SmsQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.sms.internal.dao.ISendSmsDao;
import cn.lonsun.sms.internal.entity.SendSmsEO;
import cn.lonsun.sms.internal.service.ISendSmsService;
import cn.lonsun.sms.util.SmsPropertiesUtil;

@Service("sendSmsService")
public class SendSmsServiceImpl extends BaseService<SendSmsEO> implements ISendSmsService{

	@Autowired
	private ISendSmsDao sendSmsDao;

	/**
	 * 1  通过  | 2 验证码失效 | 3 验证码错误
	 */
	@Override
	public Integer isTimeOut(String phone,String smsCode) {
		Integer status = 3;
		SendSmsEO smsEO = sendSmsDao.getMaxTimeSms(phone);
		if(smsEO == null){
			return status;
		}
		//超时默认时间
		int timeOut = 5;
		try{
			timeOut = Integer.parseInt(SmsPropertiesUtil.time);
		}catch(ParseException e){
			e.printStackTrace();
		}
		//处理是否超时
		int min = (int)((new Date().getTime() - smsEO.getCreateDate().getTime())/(1000*60));
		if(min <= timeOut){
			if(smsCode.equals(smsEO.getCode())){
				status = 1;
				smsEO.setStatus(SendSmsEO.Status.Used.toString());
				updateEntity(smsEO);
			}else{
				status = 3;
			}
		}else{
			status = 2;
			smsEO.setStatus(SendSmsEO.Status.Timeout.toString());
			updateEntity(smsEO);
		}
		return status;
	}

	@Override
	public Pagination getPage(SmsQueryVO query) {
		return sendSmsDao.getPage(query);
	}
}
