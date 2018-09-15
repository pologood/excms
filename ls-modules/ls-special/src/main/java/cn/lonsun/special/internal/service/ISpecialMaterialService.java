package cn.lonsun.special.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.special.internal.entity.SpecialMaterialEO;
import cn.lonsun.special.internal.vo.SpecialMaterialQueryVO;

/**
 * Created by doocal on 2016-10-15.
 */
public interface ISpecialMaterialService extends IMockService<SpecialMaterialEO> {


    /**
     * 保存专题素材
     * @param specialMaterial
     */
    void saveSpecialMaterial(SpecialMaterialEO specialMaterial);


    /**
     *  获取分页数据
     * @param queryVO
     * @return
     */
    Pagination getPagination(SpecialMaterialQueryVO queryVO);
}
