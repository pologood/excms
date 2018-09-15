package cn.lonsun.system.globalconfig.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.system.globalconfig.internal.entity.GlobConfigCateEO;

/**
 * 
 * @ClassName: IGlobConfigCateService
 * @Description: 全局配置分类业务逻辑层
 * @author Hewbing
 * @date 2015年8月25日 上午10:42:27
 *
 */
public interface IGlobConfigCateService extends IBaseService<GlobConfigCateEO> {
	/**
	 * 
	 * @Description 根据code编码获取配置
	 * @param code
	 * @return
	 */
	public GlobConfigCateEO getGlobConfigCateByCode(String code);
	
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
