/*
 * Powered By zhongjun
 * createtime 2017-11-16 17:55:00
 */

package cn.lonsun.search.internal.service;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.search.internal.entity.SearchTaskEO;

import java.util.Map;

public interface ISearchTaskService extends IBaseService<SearchTaskEO> {
	
	public Pagination getPage(PageQueryVO pageinfo);
	
    public void saveOrUpdate(SearchTaskEO eo);
	
    public SearchTaskEO getByID(long id);
	
	public void delete(long id);

    /**
     * 保存查询任务
     * @param searchKey
     * @return
     */
    public void save(String searchKey);

    /**
     * 批量删除
     * @param id
     */
    public void delete(String[] id);

    /**
     * 标记已完成
     * @param id
     */
    public void setFinished(Long id, String msg);


    public Pagination queryFromSolr(String searchKey, Map<String, Object> otherParam, long pageIndex, int pageSize);
}
