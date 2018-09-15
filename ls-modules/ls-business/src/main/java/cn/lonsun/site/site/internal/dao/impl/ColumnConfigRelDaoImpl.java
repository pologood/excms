package cn.lonsun.site.site.internal.dao.impl;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.site.site.internal.dao.IColumnConfigRelDao;
import cn.lonsun.site.site.internal.entity.ColumnConfigRelEO;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-4-7<br/>
 */
@Repository("columnConfigRelDao")
public class ColumnConfigRelDaoImpl extends MockDao<ColumnConfigRelEO> implements IColumnConfigRelDao {
}
