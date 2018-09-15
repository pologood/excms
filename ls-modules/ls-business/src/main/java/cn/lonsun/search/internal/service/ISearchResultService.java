/*
 * Powered By zhongjun
 * createtime 2017-11-16 17:55:00
 */

package cn.lonsun.search.internal.service;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.search.internal.entity.SearchResultEO;

public interface ISearchResultService extends IBaseService<SearchResultEO> {
	
	public Pagination getPage(PageQueryVO pageinfo);
	
    public void saveOrUpdate(SearchResultEO eo);
	
    public SearchResultEO getByID(long id);
	
	public void delete(long id);


    /**
     * 批量删除
     * @param id
     */
    public void delete(String[] id);
}
