package cn.lonsun.site.site.internal.service.impl;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.site.internal.dao.IColumnGuiDangConfigDao;
import cn.lonsun.site.site.internal.dao.IColumnTypeDao;
import cn.lonsun.site.site.internal.entity.ColumnConfigRelEO;
import cn.lonsun.site.site.internal.entity.ColumnTypeEO;
import cn.lonsun.site.site.internal.service.IColumnConfigRelService;
import cn.lonsun.site.site.internal.service.IColumnTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 栏目类型关联Service层<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-4-7<br/>
 */
@Service("columnTypeService")
public class ColumnTypeServiceImpl extends MockService<ColumnTypeEO> implements IColumnTypeService {
    @Autowired
    private IColumnTypeDao typeDao;

    public Pagination getPage(ContentPageVO pageVO) {
        return typeDao.getPage(pageVO);
    }

    public List<ColumnTypeEO> getColumnTypeEOs(Long siteId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("recordStatus", "Normal");
        if (siteId != null)
            params.put("siteId", siteId);
        List<ColumnTypeEO> cs = getEntities(ColumnTypeEO.class, params);
        return cs;
    }

    public boolean isHave(Long siteId, Long id, String name) {
        return typeDao.isHave(siteId, id, name);
    }

    public List<ColumnTypeEO> getCtsByIds(Long[] ids) {
        return typeDao.getCtsByIds(ids);
    }
}
