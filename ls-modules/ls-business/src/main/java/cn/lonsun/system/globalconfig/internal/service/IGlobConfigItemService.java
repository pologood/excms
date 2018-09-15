package cn.lonsun.system.globalconfig.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.system.globalconfig.internal.entity.GlobConfigItemEO;

/**
 * 
 * @ClassName: IGlobConfigItemService
 * @Description: 配置项业务逻辑层
 * @author Hewbing
 * @date 2015年8月25日 上午10:42:56
 *
 */
public interface IGlobConfigItemService extends IBaseService<GlobConfigItemEO> {
	/**
	 * 
	 * @Description 根据配置类型ID获取配置项
	 * @param cateId
	 * @return
	 */
	public List<GlobConfigItemEO> getListByCateId(Long cateId);
	
	/**
	 * 
	 * @Description 根据编码获取配置
	 * @param cateCode
	 * @return
	 */
	public List<GlobConfigItemEO> getListByCateKey(String cateKey);
	
	/**
	 * 
	 * @Description 根据key值查找
	 * @param key
	 * @return
	 */
	public GlobConfigItemEO getEOByKey(String key);
	
	/**
	 * 
	 * @Description 根据key和cateCode获取集合
	 * @param key
	 * @param cateCode
	 * @return
	 */
	public List<GlobConfigItemEO> getEOByKey2(String key,String cateKey);
	
	/**
	 * 
	 * @Description 保存用户认证设置
	 * @param type
	 * @param config
	 */
	public void saveUserAuthConfig(String type,String[] config);
}
