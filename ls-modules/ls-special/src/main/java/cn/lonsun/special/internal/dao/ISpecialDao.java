package cn.lonsun.special.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.special.internal.entity.SpecialEO;
import cn.lonsun.special.internal.vo.SpecialQueryVO;

/**
 * Created by doocal on 2016-10-15.
 */
public interface ISpecialDao extends IMockDao<SpecialEO> {

    /**
     * 获取分页
     *
     * @param queryVO
     * @return
     */
    Pagination getPagination(SpecialQueryVO queryVO);

    /**
     * 改变专题状态
     */

    Object changeSpecial(Long id, Long specialStatus);

    /**
     * 查找是否存在相关主题
     */
    SpecialEO getThemeById(Long id);

    /**
     * 根据ID查找专题
     */
    SpecialEO getById(Long id);

}
