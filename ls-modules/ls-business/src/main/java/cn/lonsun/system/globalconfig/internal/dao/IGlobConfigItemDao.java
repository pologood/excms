package cn.lonsun.system.globalconfig.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.system.globalconfig.internal.entity.GlobConfigItemEO;

/**
 * 
 * @ClassName: IGlobConfigItemDao
 * @Description:  配置项数据访问层
 * @author Hewbing
 * @date 2015年8月24日 上午10:40:27
 *
 */
public interface IGlobConfigItemDao extends IBaseDao<GlobConfigItemEO> {
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
	 * @param cateKey
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
	 * @Description 根据key和cateKey获取集合
	 * @param key
	 * @param cateCode
	 * @return
	 */
	public List<GlobConfigItemEO> getEOByKey2(String key,String cateKey);
}
