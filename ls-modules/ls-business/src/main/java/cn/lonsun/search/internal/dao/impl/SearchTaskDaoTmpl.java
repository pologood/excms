/*
 * Powered By zhongjun
 * createtime 2017-11-16 17:55:00
 */

package cn.lonsun.search.internal.dao.impl;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.search.internal.dao.ISearchTaskDao;
import cn.lonsun.search.internal.entity.SearchTaskEO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongjun
 * @createtime 2017-11-16 17:55:00
 */
@Repository("searchTaskEODao")
public class SearchTaskDaoTmpl extends BaseDao<SearchTaskEO> implements ISearchTaskDao {
	
	@Override
    public Pagination getPage(PageQueryVO pageinfo) {
        StringBuilder sql = new StringBuilder();
        sql.append("from SearchTaskEOEO");
		List<Object> values = new ArrayList<Object>();
        // set param like this
		//if(param.containsKey("state") && param.get("state") != null && !AppUtil.isEmpty(param.get("state"))){
        //    sql.append(" and state = ? ");
        //    values.add(Long.valueOf(param.get("state").toString()));
        //}
        return super.getPagination(pageinfo.getPageIndex(),pageinfo.getPageSize(), sql.toString(), values.toArray());
    }

}
