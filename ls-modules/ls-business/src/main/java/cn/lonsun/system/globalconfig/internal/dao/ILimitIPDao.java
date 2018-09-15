package cn.lonsun.system.globalconfig.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.globalconfig.internal.entity.LimitIPEO;
import cn.lonsun.system.globalconfig.vo.LimitIPPageVO;

/**
 * @author Doocal
 * @ClassName: ILimitIPDao
 * @Description: 限制IP数据访问层
 */
public interface ILimitIPDao extends IMockDao<LimitIPEO> {

    public Pagination getPage(LimitIPPageVO vo);

    public Object saveLimitIP(LimitIPEO eo);

    public Object deleteLimitIP(Long id);

    public Object updateLimitIP(LimitIPEO eo);

    public List<LimitIPEO> checkIP(LimitIPEO eo);

    public LimitIPEO getOneIP(Long id);

}
