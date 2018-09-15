package cn.lonsun.govbbs.util;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.govbbs.internal.entity.BbsPostEO;
import cn.lonsun.govbbs.internal.entity.BbsSettingEO;
import cn.lonsun.govbbs.internal.service.IBbsSettingService;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PostTimeUtil {

	private static IBbsSettingService bbsSettingService = SpringContextHolder.getBean("bbsSettingService");

	public static List<BbsPostEO> setTimes(List<BbsPostEO> posts) throws ParseException{
		BbsSettingEO bbsSetting = getBbsSetting();
		SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
		String nowTimeStr = simple.format(new Date());
		Date endTime =  simple.parse(nowTimeStr);
		if(posts !=null && posts.size()>0){
			for(BbsPostEO post:posts){
				if(post.getAcceptUnitId() != null){
					String startTimeStr = simple.format(post.getCreateDate());
					Date startTime =  simple.parse(startTimeStr);
					long intervalMilli =endTime.getTime() - startTime.getTime();
					int day = (int) (intervalMilli / (24 * 60 * 60 * 1000)) + 1;
					if(bbsSetting != null){
						// 处理日期池
						if(!StringUtils.isEmpty(bbsSetting.getTimes())){
							String[] timesArray = bbsSetting.getTimes().split("#");
							if(timesArray != null && timesArray.length>0){
								for(String t:timesArray){
									try{
										Date times = simple.parse(t);
										Long timeI = times.getTime();
										if(timeI>=startTime.getTime() && timeI<=endTime.getTime()){
											day--;
										}
									}catch(Exception e){}
								}
							}
						}
						if(bbsSetting.getYellowDay() !=null && day >= bbsSetting.getYellowDay()){
							post.setYellowCard(1);
						}
						if(bbsSetting.getRedDay() !=null && day >= bbsSetting.getRedDay()){
							post.setRedCard(1);
						}
						if(bbsSetting.getReplyDay() !=null && day >= bbsSetting.getReplyDay()){
							post.setIsTimeOut(1);
						}
					}
				}
			}
		}
		return posts;
	}

	private static BbsSettingEO getBbsSetting() {
		BbsSettingEO bbsSetting = null;
		try{
			Long siteId = LoginPersonUtil.getSiteId();
			bbsSetting = CacheHandler.getEntity(BbsSettingEO.class,siteId);
		}catch(Exception e){}
		return bbsSetting;
	}

}
