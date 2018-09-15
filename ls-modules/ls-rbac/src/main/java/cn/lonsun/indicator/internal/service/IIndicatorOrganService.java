package cn.lonsun.indicator.internal.service;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.IBaseService;
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
public interface IIndicatorOrganService  extends IBaseService<IndicatorOrganEO> {

	/**
	 * 获取最大的排序号
	 *
	 * @return
	 */
	public int getMaxSortNum();
	
	/**
	 * 获取IndicatorOrganEO
	 *
	 * @param shortCutId
	 * @param menuId
	 * @param unitId
	 * @return
	 */
	public IndicatorOrganEO getIndicatorOrgans(Long shortCutId,Long menuId,Long unitId);
	
	/**
	 * 分页查询
	 *
	 * @param query
	 * @return
	 */
	public Pagination getPagination(PageQueryVO query);
}
