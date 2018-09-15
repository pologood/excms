package cn.lonsun.special.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.special.internal.entity.SpecialThemeEO;
import cn.lonsun.special.internal.vo.SpecialThemeQueryVO;

/**
 * Created by doocal on 2016-10-15.
 */
public interface ISpecialThemeDao extends IMockDao<SpecialThemeEO> {


    /**
     * 获取分页数据
     * @param queryVO
     * @return
     */
    Pagination getPagination(SpecialThemeQueryVO queryVO);

}
