package cn.lonsun.special.internal.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.special.internal.dao.ISpecialMaterialDao;
import cn.lonsun.special.internal.entity.SpecialMaterialEO;
import cn.lonsun.special.internal.service.ISpecialMaterialService;
import cn.lonsun.special.internal.vo.SpecialMaterialQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zhushouyong on 2016-10-15.
 */
@Service
public class SpecialMaterialServiceImpl extends MockService<SpecialMaterialEO> implements ISpecialMaterialService {

    @Autowired
    private ISpecialMaterialDao specialMaterialDao;

    /**
     * 保存专题主题
     * @param specialMaterial
     */
    @Override
    public void saveSpecialMaterial(SpecialMaterialEO specialMaterial) {
        if(null  ==  specialMaterial.getId()){
            saveEntity(specialMaterial);
        }else{
            updateEntity(specialMaterial);
        }
    }

    @Override
    public Pagination getPagination(SpecialMaterialQueryVO queryVO) {
        return specialMaterialDao.getPagination(queryVO);
    }
}
