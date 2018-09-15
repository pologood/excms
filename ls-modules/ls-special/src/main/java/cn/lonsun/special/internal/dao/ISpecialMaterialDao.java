package cn.lonsun.special.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.special.internal.entity.SpecialMaterialEO;
import cn.lonsun.special.internal.vo.SpecialMaterialQueryVO;

/**
 * Created by doocal on 2016-10-15.
 */
public interface ISpecialMaterialDao extends IMockDao<SpecialMaterialEO> {


    /**
     * 获取分页数据
     * @param queryVO
     * @return
     */
    Pagination getPagination(SpecialMaterialQueryVO queryVO);

}
