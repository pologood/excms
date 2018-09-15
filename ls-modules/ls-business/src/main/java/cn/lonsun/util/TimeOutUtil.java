package cn.lonsun.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeOutUtil {

	
	public static Integer getTimeOut(Date startTime,Date endTime){
		SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date nowTime = null;
		Integer isTimeOut = null;
		try {
			nowTime = simple.parse(simple.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(nowTime.getTime() < startTime.getTime()){
			isTimeOut = 1;//未开始
		}else if(nowTime.getTime() >= startTime.getTime()){
			if(nowTime.getTime() > endTime.getTime()){
				isTimeOut = 3;//已过期
			}else{
				isTimeOut = 2;//进行中
			}
		}
		return isTimeOut;
	}
}
