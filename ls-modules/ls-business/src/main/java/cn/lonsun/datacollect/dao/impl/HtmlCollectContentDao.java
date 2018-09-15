package cn.lonsun.datacollect.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datacollect.dao.IHtmlCollectContentDao;
import cn.lonsun.datacollect.entity.HtmlCollectContentEO;
import cn.lonsun.datacollect.vo.CollectPageVO;
import cn.lonsun.datacollect.vo.ColumnVO;
import cn.lonsun.site.template.util.SqlHelper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2016-1-21 14:27
 */
@Repository("htmlCollectContentDao")
public class HtmlCollectContentDao extends MockDao<HtmlCollectContentEO> implements IHtmlCollectContentDao {

    @Override
    public Pagination getPageEOs(CollectPageVO vo) {
        Long pageIndex = vo.getPageIndex();
        Integer pageSize = vo.getPageSize();
        StringBuilder hql = new StringBuilder("from HtmlCollectContentEO where 1=1 and taskId = ?");
        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql.toString(), vo), new Object[]{vo.getTaskId()});
    }

    @Override
    public List<HtmlCollectContentEO> getByTaskId(Long taskId) {
        return this.getEntitiesByHql("from HtmlCollectContentEO where taskId = ?",new Object[] {taskId});
    }

    @Override
    public void deleteEOs(Long[] ids) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("ids", ids);
        this.executeUpdateByJpql("delete from HtmlCollectContentEO where id in (:ids)", param);
    }

    @Override
    public List<ColumnVO> getColumns(String tableName) {
//        String sql = "SELECT COLUMN_NAME AS \"columnName\",COMMENTS AS \"comments\" FROM USER_COL_COMMENTS WHERE TABLE_NAME='" + tableName + "' AND COLUMN_NAME NOT IN('ID','TASK_ID','CREATE_USER_ID','UPDATE_DATE','CREATE_DATE','UPDATE_USER_ID','RECORD_STATUS','CREATE_ORGAN_ID')";
        String sql = "SELECT t1.COLUMN_NAME AS \"columnName\",t1.DATA_TYPE AS \"dataType\",t1.DATA_LENGTH AS \"dataLength\",t2.COMMENTS AS \"comments\"" +
                " FROM user_tab_columns t1,user_col_comments t2 WHERE t1.TABLE_NAME='" + tableName + "' AND t1.TABLE_NAME  =t2.TABLE_NAME AND t1.COLUMN_NAME =t2.COLUMN_NAME AND t1.COLUMN_NAME NOT IN('ID','TASK_ID','CREATE_USER_ID','UPDATE_DATE','CREATE_DATE','UPDATE_USER_ID','RECORD_STATUS','CREATE_ORGAN_ID')";
        return (List<ColumnVO>) this.getBeansBySql(sql,new Object[]{},ColumnVO.class);
    }
}
