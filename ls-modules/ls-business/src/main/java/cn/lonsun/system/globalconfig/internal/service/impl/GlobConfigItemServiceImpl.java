package cn.lonsun.system.globalconfig.internal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.system.globalconfig.internal.dao.IGlobConfigCateDao;
import cn.lonsun.system.globalconfig.internal.dao.IGlobConfigItemDao;
import cn.lonsun.system.globalconfig.internal.entity.GlobConfigItemEO;
import cn.lonsun.system.globalconfig.internal.service.IGlobConfigItemService;

@Service("globConfigItemService")
public class GlobConfigItemServiceImpl extends BaseService<GlobConfigItemEO> implements
		IGlobConfigItemService {

	@Autowired
	private IGlobConfigItemDao globConfigItemDao;
	@Autowired
	private IGlobConfigCateDao globConfigCateDao;	
	
	@Override
	public List<GlobConfigItemEO> getListByCateId(Long cateId) {
		return globConfigItemDao.getListByCateId(cateId);
	}

	@Override
	public List<GlobConfigItemEO> getListByCateKey(String cateKey) {
		return globConfigItemDao.getListByCateKey(cateKey);
	}

	@Override
	public GlobConfigItemEO getEOByKey(String key) {
		return globConfigItemDao.getEOByKey(key);
	}

	@Override
	public List<GlobConfigItemEO> getEOByKey2(String key, String cateCode) {
		return globConfigItemDao.getEOByKey2(key, cateCode);
	}

	@Override
	public void saveUserAuthConfig(String type,String[] config) {
		if("local_auth".equals(type)){
			globConfigCateDao.updateKeyByCode("user_auth", type);
		}else if("sso_auth".equals(type)){
			globConfigCateDao.updateKeyByCode("user_auth", type);
			for(int i=0;i<config.length;i++){
				String[] str=new String[2];
				str=config[i].split("\\|");
				GlobConfigItemEO item = globConfigItemDao.getEOByKey(str[0]);
				if(!AppUtil.isEmpty(item)){
					item.setValue(str[1]);
					saveEntity(item);
				}
			}
		}
	}

}
