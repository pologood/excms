package cn.lonsun.datacollect.dao.impl;

import cn.lonsun.datacollect.vo.ColumnVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author gu.fei
 * @version 2016-1-21 14:27
 */
@Repository("htmlCollectContentMySqlDao")
public class HtmlCollectContentMySqlDao extends HtmlCollectContentDao {

    @Value("${hibernate.default_schema}")
    private String schema;

    @Override
    public List<ColumnVO> getColumns(String tableName) {
        String sql = "SELECT COLUMN_NAME AS \"columnName\",COLUMN_TYPE AS \"dataType\",COLUMN_COMMENT AS \"comments\" FROM INFORMATION_SCHEMA.COLUMNS " +
                " WHERE TABLE_NAME = '" + tableName + "' AND TABLE_SCHEMA = '" + schema + "'";
        return (List<ColumnVO>) this.getBeansBySql(sql, new Object[]{}, ColumnVO.class, new String[]{"columnName", "dataType", "comments"});
    }
}
