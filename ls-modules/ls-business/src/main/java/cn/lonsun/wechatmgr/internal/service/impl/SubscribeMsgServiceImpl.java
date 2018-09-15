package cn.lonsun.wechatmgr.internal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.wechatmgr.internal.dao.ISubscribeMsgDao;
import cn.lonsun.wechatmgr.internal.entity.SubscribeMsgEO;
import cn.lonsun.wechatmgr.internal.service.ISubscribeMsgService;

import java.util.HashMap;
import java.util.Map;

@Service("subscribeMsgService")
public class SubscribeMsgServiceImpl extends MockService<SubscribeMsgEO> implements
		ISubscribeMsgService {

	private static Map<Long,String> msgMap = new HashMap<Long,String>();

	@Autowired
	private ISubscribeMsgDao subscribeMsgDao;

	@Override
	public SubscribeMsgEO getMsgBySite(Long siteId) {
		return subscribeMsgDao.getMsgBySite(siteId);
	}

	@Override
	public String getMsgCache(Long siteId) {
		if(msgMap.containsKey(siteId)){
			return msgMap.get(siteId);
		}else{
			SubscribeMsgEO eo= subscribeMsgDao.getMsgBySite(siteId);
			if(eo != null){
				String msg = eo.getContent();
				msgMap.put(siteId,msg);
				return msg;
			}
		}
		return null;
	}

	@Override
	public void save(SubscribeMsgEO subMsg) {
		saveOrUpdateEntity(subMsg);
		//放入map中  微信调用
		msgMap.put(subMsg.getSiteId(),subMsg.getContent());
	}
}
