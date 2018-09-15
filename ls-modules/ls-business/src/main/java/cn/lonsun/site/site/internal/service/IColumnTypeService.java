package cn.lonsun.site.site.internal.service;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.entity.ColumnTypeEO;

import java.util.Date;
import java.util.List;

/**
 * 栏目归档配置service层 <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-8-24 <br/>
 */

public interface IColumnTypeService extends IMockService<ColumnTypeEO> {
    public Pagination getPage(ContentPageVO pageVO);

    public List<ColumnTypeEO> getColumnTypeEOs(Long siteId);

    public boolean isHave(Long siteId, Long id, String name);
    public List<ColumnTypeEO> getCtsByIds(Long[] ids);
}
