package cn.lonsun.system.globalconfig.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.system.globalconfig.internal.entity.GlobConfigCateEO;

/**
 * 
 * @ClassName: IGlobConfigCateDao
 * @Description: 全局配置类型数据访问层
 * @author Hewbing
 * @date 2015年8月25日 上午10:39:43
 *
 */
public interface IGlobConfigCateDao extends IBaseDao<GlobConfigCateEO> {
	
	/**
	 * 
	 * @Description 根据code编码获取配置
	 * @param code
	 * @return
	 */
	public GlobConfigCateEO getGlobConfigCateByCode(String code);
	
	/**
	 * 
	 * @Description 根据编码code修改key值
	 * @param code
	 * @param key
	 */
	public void updateKeyByCode(String code,String key);
	
	/**
	 * 
	 * @Description 根据key获取配置
	 * @author Hewbing
	 * @date 2015年9月1日 下午3:33:52
	 * @param key
	 * @return
	 */
	public GlobConfigCateEO getGlobConfigCateByKey(String key);
}
