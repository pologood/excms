/*
 * Powered By zhongjun
 * createtime 2017-11-16 17:55:00
 */
package cn.lonsun.search.internal.dao;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.search.internal.entity.SearchResultEO;


public interface ISearchResultDao extends IBaseDao<SearchResultEO> {

	public Pagination getPage(PageQueryVO pageinfo);

}
