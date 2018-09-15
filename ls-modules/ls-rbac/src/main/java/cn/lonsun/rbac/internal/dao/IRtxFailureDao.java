package cn.lonsun.rbac.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.RtxFailureEO;
import cn.lonsun.rbac.internal.vo.RtxQueryVO;

/**
 * RTX同步错误记录ORM接口
 *
 * @author xujh
 * @version 1.0
 * 2015年4月22日
 *
 */
public interface IRtxFailureDao extends IBaseDao<RtxFailureEO> {
	
	/**
	 * 分页查询RTX同步失败信息
	 *
	 * @param query
	 * @return
	 */
	public Pagination getPage(RtxQueryVO rqvo);

}
