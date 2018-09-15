package cn.lonsun.wechatmgr.internal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.FileUploadUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.SysLog;
import cn.lonsun.wechatmgr.internal.dao.IWeChatAccountsInfoDao;
import cn.lonsun.wechatmgr.internal.entity.WeChatAccountsInfoEO;
import cn.lonsun.wechatmgr.internal.service.IWeChatAccountsInfoService;

import java.util.HashMap;
import java.util.Map;

@Service("weChatAccountsInfoService")
public class WeChatAccountsInfoServiceImpl extends
		MockService<WeChatAccountsInfoEO> implements IWeChatAccountsInfoService {

	private static Map<String,Long> siteMap = new HashMap<String,Long>();

	@Autowired
	private IWeChatAccountsInfoDao weChatAccountsInfoDao;
	@Override
	public WeChatAccountsInfoEO getInfoByPrimitiveId(String primitiveId) {
		return weChatAccountsInfoDao.getInfoByPrimitiveId(primitiveId);
	}

	/**
	 * 缓存下站点id
	 * @param toUserName
	 * @return
     */
	@Override
	public Long getSiteId(String toUserName) {
		if(siteMap.containsKey(toUserName)){
			return siteMap.get(toUserName);
		}else{
			WeChatAccountsInfoEO eo = weChatAccountsInfoDao.getInfoByPrimitiveId(toUserName);
			if(eo != null){
				Long siteId = eo.getSiteId();
				siteMap.put(toUserName,siteId);
				return siteId;
			}
		}
		return null;
	}

	@Override
	public WeChatAccountsInfoEO getInfoBySite(Long siteId) {
		return weChatAccountsInfoDao.getInfoBySite(siteId);
	}
	@Override
	public void saveConfig(WeChatAccountsInfoEO config) {
		Long siteId=LoginPersonUtil.getSiteId();
		config.setSiteId(siteId);
		if(AppUtil.isEmpty(config.getId())){
			Long id=saveEntity(config);
			SysLog.log("新增站点下微信配置 >> ID："+id, "WeChatAccountsInfoEO", CmsLogEO.Operation.Add.toString());
		}else{
			WeChatAccountsInfoEO oldConfig = getEntity(WeChatAccountsInfoEO.class, config.getId());
			FileUploadUtil.setStatus(new String[]{oldConfig.getQrCode(),oldConfig.getHeadImg()}, 0);
			weChatAccountsInfoDao.merge(config);
			SysLog.log("修改站点下微信配置 >> ID："+oldConfig.getId(), "WeChatAccountsInfoEO", CmsLogEO.Operation.Update.toString());
		}
		FileUploadUtil.setStatus(new String[]{config.getQrCode(),config.getHeadImg()}, 1);
	}
	@Override
	public WeChatAccountsInfoEO getInfoByUid(String primitiveId) {
		return weChatAccountsInfoDao.getInfoByUid(primitiveId);
	}

}
