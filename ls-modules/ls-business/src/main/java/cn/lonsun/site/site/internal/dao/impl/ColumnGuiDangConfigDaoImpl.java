package cn.lonsun.site.site.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.site.site.internal.dao.IColumnGuiDangConfigDao;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 栏目配置Dao实现类<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-8-24 <br/>
 */
@Repository("columnGuiDangConfigDao")
public class ColumnGuiDangConfigDaoImpl extends MockDao<ColumnConfigEO> implements IColumnGuiDangConfigDao {
    public void updateColumnConfigEObyPid(Long indicatorId, Integer istofile, Date tofiledate, String tofileid) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = null;
        if (tofiledate != null)
            str = "'" + sdf.format(tofiledate) + "'";
        String hql = "UPDATE CMS_COLUMN_CONFIG set IS_TOFILE=?,TO_FILEDATE=" + str + ",TO_FILEID=? where INDICATOR_ID in ";
        if (istofile == 1)
            hql += "(select t.indicator_id from RBAC_INDICATOR t where t.parent_ids like '%" + indicatorId + "%') " +
                    "and (IS_TOFILE=0 or IS_TOFILE is null) and RECORD_STATUS='Normal'";
        else
            hql += "(select t.indicator_id from RBAC_INDICATOR t where t.parent_ids like '%" + indicatorId + "%') " +
                    "and IS_TOFILE=1 and RECORD_STATUS='Normal'";

        this.executeUpdateBySql(hql, new Object[]{istofile, tofileid});
    }

    public void updateBaseContentEObyPid(Long indicatorId, Integer istofile, Date tofiledate, String tofileid) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = null;
        if (tofiledate != null)
            str = "'" + sdf.format(tofiledate) + "'";
        String hql = "UPDATE CMS_BASE_CONTENT set IS_TOFILE=?,TO_FILEDATE=" + str + ",TO_FILEID=? where COLUMN_ID in ";
        if (istofile == 1)
            hql += "(select t.indicator_id from RBAC_INDICATOR t where t.parent_ids like '%" + indicatorId + "%' or t.indicator_id=?) " +
                    "and (IS_TOFILE=0 or IS_TOFILE is null) and RECORD_STATUS='Normal'";
        else
            hql += "(select t.indicator_id from RBAC_INDICATOR t where t.parent_ids like '%" + indicatorId + "%' or t.indicator_id=?) " +
                    "and IS_TOFILE=1 and RECORD_STATUS='Normal'";

        this.executeUpdateBySql(hql, new Object[]{istofile, tofileid, indicatorId});
    }

    public void updateColumnConfigEObyOracle(Long indicatorId, Integer istofile, Date tofiledate, String tofileid) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = null;
        if (tofiledate != null)
            str = "'" + sdf.format(tofiledate) + "'";
        String hql = "UPDATE CMS_COLUMN_CONFIG set IS_TOFILE=?,TO_FILEDATE=to_timestamp(" + str + ",'yyyy-mm-dd hh24:mi:ss:ff'),TO_FILEID=? where INDICATOR_ID in ";
        if (istofile == 1)
            hql += "(select t.indicator_id from RBAC_INDICATOR t start with t.parent_id = ? connect by prior t.indicator_id = t.parent_id) " +
                    "and (IS_TOFILE=0 or IS_TOFILE is null) and RECORD_STATUS='Normal'";
        else
            hql += "(select t.indicator_id from RBAC_INDICATOR t start with t.parent_id = ? connect by prior t.indicator_id = t.parent_id) " +
                    "and IS_TOFILE=1 and RECORD_STATUS='Normal'";

        this.executeUpdateBySql(hql, new Object[]{istofile, tofileid, indicatorId});
    }

    public void updateBaseContentEObyOracle(Long indicatorId, Integer istofile, Date tofiledate, String tofileid) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = null;
        if (tofiledate != null)
            str = "'" + sdf.format(tofiledate) + "'";
        String hql = "UPDATE CMS_BASE_CONTENT set IS_TOFILE=?,TO_FILEDATE=to_timestamp(" + str + ",'yyyy-mm-dd hh24:mi:ss:ff'),TO_FILEID=? where COLUMN_ID in ";
        if (istofile == 1)
            hql += "(select t.indicator_id from RBAC_INDICATOR t start with t.indicator_id = ? connect by prior t.indicator_id = t.parent_id) " +
                    "and (IS_TOFILE=0 or IS_TOFILE is null) and RECORD_STATUS='Normal'";
        else
            hql += "(select t.indicator_id from RBAC_INDICATOR t start with t.indicator_id = ? connect by prior t.indicator_id = t.parent_id) " +
                    "and IS_TOFILE=1 and RECORD_STATUS='Normal'";

        this.executeUpdateBySql(hql, new Object[]{istofile, tofileid, indicatorId});
    }

    public void updateColumnConfigEObyProcedure(Long indicatorId, Integer istofile, Date tofiledate, String tofileid) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = null;
        if (tofiledate != null)
            str = sdf.format(tofiledate);
        String hql = "call showChildList(?,?,?,?,?)";
        this.executeUpdateBySql(hql, new Object[]{indicatorId, 0, istofile, str, tofileid});
    }

    public void updateBaseContentEObyProcedure(Long indicatorId, Integer istofile, Date tofiledate, String tofileid) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = null;
        if (tofiledate != null)
            str = sdf.format(tofiledate);
        String hql = "call showChildList(?,?,?,?,?)";
        this.executeUpdateBySql(hql, new Object[]{indicatorId, 1, istofile, str, tofileid});
    }
}
