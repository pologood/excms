package cn.lonsun.site.site.internal.dao;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.site.internal.entity.ColumnConfigRelEO;
import cn.lonsun.site.site.internal.entity.ColumnTypeEO;

import java.util.List;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-4-7<br/>
 */

public interface IColumnTypeDao extends IMockDao<ColumnTypeEO> {
    public Pagination getPage(ContentPageVO pageVO);
    public boolean isHave(Long siteId,Long id,String name);
    public List<ColumnTypeEO> getCtsByIds(Long[] ids);
}
