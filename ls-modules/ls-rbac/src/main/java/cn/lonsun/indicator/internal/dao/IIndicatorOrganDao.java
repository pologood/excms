package cn.lonsun.indicator.internal.dao;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorOrganEO;

/**
 * 部分应用只展示固定单位的数据ORM接口
 *
 * @author xujh
 * @version 1.0
 * 2015年3月12日
 *
 */
public interface IIndicatorOrganDao  extends IBaseDao<IndicatorOrganEO> {
	
	/**
	 * 获取最大的排序号
	 *
	 * @return
	 */
	public int getMaxSortNum();
	
	/**
	 * 分页查询
	 *
	 * @param query
	 * @return
	 */
	public Pagination getPagination(PageQueryVO query);

}
