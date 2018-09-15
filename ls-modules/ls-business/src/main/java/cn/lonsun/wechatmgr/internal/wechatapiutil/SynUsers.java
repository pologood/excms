package cn.lonsun.wechatmgr.internal.wechatapiutil;

import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.util.ConcurrentUtil;
import cn.lonsun.wechatmgr.internal.entity.WeChatUserEO;
import cn.lonsun.wechatmgr.internal.service.IWeChatUserService;

public class SynUsers implements Runnable {

	private static IWeChatUserService weChatUserService=SpringContextHolder.getBean("weChatUserService");
	
	private Long siteId;
	
	private Long userId;
	@Override
	public void run() {
        SessionFactory sessionFactory = SpringContextHolder.getBean(SessionFactory.class);
        boolean participate = ConcurrentUtil.bindHibernateSessionToThread(sessionFactory);
		try{
		weChatUserService.removeBySite(siteId);
		String nextOpenId="";
		synUser(siteId,nextOpenId);
        MessageSystemEO eo = new MessageSystemEO();
        eo.setTitle("微信关注用户同步成功");
        eo.setLink("");
        eo.setModeCode("weChatUserSyn");
        eo.setMessageType(1L);
        eo.setRecUserIds(String.valueOf(userId));
        eo.setMessageStatus(MessageSystemEO.MessageStatus.success.toString());
        MessageSender.sendMessage(eo);
		}catch (Exception e){
	        MessageSystemEO eo = new MessageSystemEO();
	        eo.setTitle("微信关注用户同步失败，请重新同步");
	        eo.setLink("");
	        eo.setModeCode("weChatUserSyn");
	        eo.setMessageType(1L);
	        eo.setRecUserIds(String.valueOf(userId));
	        eo.setMessageStatus(MessageSystemEO.MessageStatus.error.toString());
	        MessageSender.sendMessage(eo);
			e.printStackTrace();
		}finally{
			ConcurrentUtil.closeHibernateSessionFromThread(participate, sessionFactory);
		}
	}
	public Long getSiteId() {
		return siteId;
	}
	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	//递归获取微信关注用户信息
	private void synUser(Long siteId,String nextOpenId){
		List<String> uList=ApiUtil.getUserList(siteId,nextOpenId);
		for(String li:uList){
			WeChatUserEO uInfo = ApiUtil.getUserInfo(li, siteId);
			uInfo.setSiteId(siteId);
			uInfo.setCreateDate(new Date(uInfo.getSubscribe_time()*1000));
			weChatUserService.synUser(uInfo);
		}
		if(uList.size()>=1000){
			nextOpenId=uList.get(uList.size()-1);
			synUser(siteId,nextOpenId);
		}else{
			return;
		}
	}

}
