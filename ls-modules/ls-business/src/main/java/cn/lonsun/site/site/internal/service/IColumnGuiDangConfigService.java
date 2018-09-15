package cn.lonsun.site.site.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;

import java.util.Date;

/**
 * 栏目归档配置service层 <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-8-24 <br/>
 */

public interface IColumnGuiDangConfigService extends IMockService<ColumnConfigEO> {

    public void updateColumnConfigEObyPid(Long indicatorId, Integer istofile, Date tofiledate, String tofileid);

    public void updateBaseContentEObyPid(Long indicatorId, Integer istofile, Date tofiledate, String tofileid);

    public void updateColumnConfigEObyOracle(Long indicatorId, Integer istofile, Date tofiledate, String tofileid);

    public void updateBaseContentEObyOracle(Long indicatorId, Integer istofile, Date tofiledate, String tofileid);

    public void updateColumnConfigEObyProcedure(Long indicatorId, Integer istofile, Date tofiledate, String tofileid);

    public void updateBaseContentEObyProcedure(Long indicatorId, Integer istofile, Date tofiledate, String tofileid);
}
