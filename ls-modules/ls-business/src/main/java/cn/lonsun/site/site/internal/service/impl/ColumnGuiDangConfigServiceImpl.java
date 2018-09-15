package cn.lonsun.site.site.internal.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.site.site.internal.dao.IColumnGuiDangConfigDao;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.service.IColumnGuiDangConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-8-24 <br/>
 */
@Service("columnGuiDangConfigService")
public class ColumnGuiDangConfigServiceImpl extends MockService<ColumnConfigEO> implements IColumnGuiDangConfigService {
    @Autowired
    private IColumnGuiDangConfigDao configDao;

    public void updateColumnConfigEObyPid(Long indicatorId, Integer istofile, Date tofiledate, String tofileid) {
        configDao.updateColumnConfigEObyPid(indicatorId, istofile, tofiledate, tofileid);
    }

    public void updateBaseContentEObyPid(Long indicatorId, Integer istofile, Date tofiledate, String tofileid) {
        configDao.updateBaseContentEObyPid(indicatorId, istofile, tofiledate, tofileid);
    }

    public void updateColumnConfigEObyOracle(Long indicatorId, Integer istofile, Date tofiledate, String tofileid) {
        configDao.updateColumnConfigEObyOracle(indicatorId, istofile, tofiledate, tofileid);
    }

    public void updateBaseContentEObyOracle(Long indicatorId, Integer istofile, Date tofiledate, String tofileid) {
        configDao.updateBaseContentEObyOracle(indicatorId, istofile, tofiledate, tofileid);
    }

    public void updateColumnConfigEObyProcedure(Long indicatorId, Integer istofile, Date tofiledate, String tofileid) {
        configDao.updateColumnConfigEObyProcedure(indicatorId, istofile, tofiledate, tofileid);
    }

    public void updateBaseContentEObyProcedure(Long indicatorId, Integer istofile, Date tofiledate, String tofileid) {
        configDao.updateBaseContentEObyProcedure(indicatorId, istofile, tofiledate, tofileid);
    }
}
