package cn.lonsun.site.site.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;

import java.util.Date;


/**
 * 栏目归档配置Dao层<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-8-24 <br/>
 */
public interface IColumnGuiDangConfigDao extends IMockDao<ColumnConfigEO> {
    public void updateColumnConfigEObyPid(Long indicatorId, Integer istofile, Date tofiledate, String tofileid);
    public void updateBaseContentEObyPid(Long indicatorId, Integer istofile, Date tofiledate, String tofileid);
    public void updateColumnConfigEObyOracle(Long indicatorId, Integer istofile, Date tofiledate, String tofileid);
    public void updateBaseContentEObyOracle(Long indicatorId, Integer istofile, Date tofiledate, String tofileid);
    public void updateColumnConfigEObyProcedure(Long indicatorId, Integer istofile, Date tofiledate, String tofileid);
    public void updateBaseContentEObyProcedure(Long indicatorId, Integer istofile, Date tofiledate, String tofileid);
}
