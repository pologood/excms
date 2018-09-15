package cn.lonsun.rbac.internal.service;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.PlatformEO;

/**
 * 平台管理服务接口
 *
 * @author xujh
 * @version 1.0
 * 2015年4月24日
 *
 */
public interface IPlatformService extends IBaseService<PlatformEO> {
	
	/**
	 * 获取本平台的平台编码
	 *
	 * @return
	 */
	public PlatformEO getCurrentPlatform();
	
	/**
	 * 保存
	 *
	 * @param platform
	 */
	public void save(PlatformEO platform);
	
	/**
	 * 更新
	 *
	 * @param platform
	 */
	public void update(PlatformEO platform);
	
	/**
	 * 验证code是否已存在
	 *
	 * @param code
	 * @param exceptId
	 * @return
	 */
	public boolean isCodeExisted(String code,Long exceptId);
	
	/**
	 * 分页查询
	 *
	 * @param query
	 * @return
	 */
	public Pagination getPagination(PageQueryVO query);

}
