package cn.lonsun.wechatmgr.internal.wechatapiutil;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.core.task.TaskExecutor;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.wechatmgr.internal.entity.WeChatUserEO;
import cn.lonsun.wechatmgr.internal.service.IWeChatUserService;

public class WeChatUser implements Runnable {

	public static Logger log = Logger.getLogger(WeChatUser.class);
	@Resource
	private TaskExecutor taskExecutor;

	private static IWeChatUserService weChatUserService=SpringContextHolder.getBean("weChatUserService");
	private String fromUserName;

	private Long siteId;

	@Override
	public void run() {
		log.info("WeChat taskExecutor start >>>");
		WeChatUserEO user = ApiUtil.getUserInfo(fromUserName,siteId);
		if(user != null){
			user.setSiteId(siteId);
			user.setRecordStatus("Normal");
			user.setCreateDate(new Date());
			user.setUpdateDate(new Date());
			log.info("WeChat taskExecutor end>>>");
			weChatUserService.saveEntity(user);
		}
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}
}
