package cn.lonsun.ldap.internal.service.impl;

import java.util.List;

import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.RecordsException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.ldap.internal.dao.IConfigDao;
import cn.lonsun.ldap.internal.entity.ConfigEO;
import cn.lonsun.ldap.internal.exception.LDAPDisconnectionException;
import cn.lonsun.ldap.internal.service.IConfigService;
import cn.lonsun.ldap.internal.util.LDAPUtil;

@Service("configService")
public class ConfigServiceImpl extends MockService<ConfigEO> implements IConfigService {

	@Autowired
	private IConfigDao configDao;
	
	@Override
	public ConfigEO getEffectiveConfig() {
		ConfigEO effectiveConfig = null;
		//如果当前配置不可用，那么到数据库读取其他配置
		List<ConfigEO> configs =  getConfigs();
		if(configs==null||configs.size()<=0){
			throw new RecordsException();
		}
		//用来记录链接失败次数
		int connectionFailureTimes = 0;
		for(ConfigEO config:configs){
			try {
				LDAPUtil.getDirContext(config);
				//获取上下成功，说明配置有效
				effectiveConfig = config;
				//已经获取有效的配置，那么结束循环
				break;
			} catch (NamingException e) {
				connectionFailureTimes++;
				e.printStackTrace();
			}
		}
		//如果服务器链接失败次数和配置条数相同，那么说明所有的LDAP服务器都链接
		if(connectionFailureTimes==configs.size()){
			throw new LDAPDisconnectionException();
		}
		return effectiveConfig;
	}
	
	@Override
	public boolean isUrlExistedExceptSelf(ConfigEO config){
		return configDao.isUrlExistedExceptSelf(config);
	}

	@Override
	public List<ConfigEO> getConfigs() {
		return configDao.getConfigs();
	}
	
	@Override
	public Pagination getPagination(PageQueryVO query){
		return configDao.getPagination(query);
	}

	@Override
	public Integer getMaxSortNum() {
		return configDao.getMaxSortNum();
	}

	@Override
	public String getPassword(Long configId) {
		return configDao.getPassword(configId);
	}

	
}
