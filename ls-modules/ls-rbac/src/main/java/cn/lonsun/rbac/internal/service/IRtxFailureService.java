package cn.lonsun.rbac.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.RtxFailureEO;
import cn.lonsun.rbac.internal.vo.RtxQueryVO;

/**
 * RTX同步失败记录服务接口
 *
 * @author xujh
 * @version 1.0
 * 2015年4月22日
 *
 */
public interface IRtxFailureService extends IBaseService<RtxFailureEO> {
	
	/**
	 * 分页查询RTX同步失败信息
	 *
	 * @param query
	 * @return
	 */
	public Pagination getPage(RtxQueryVO rfvo);

	public void synchronous();

}
