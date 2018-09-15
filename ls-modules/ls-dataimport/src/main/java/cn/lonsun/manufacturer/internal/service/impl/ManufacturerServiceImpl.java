package cn.lonsun.manufacturer.internal.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.manufacturer.internal.dao.IManufacturerDao;
import cn.lonsun.manufacturer.internal.entity.ManufacturerEO;
import cn.lonsun.manufacturer.internal.service.IManufacturerService;
import cn.lonsun.manufacturer.internal.vo.ManufacturerQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author caohaitao
 * @Title: ManufacturerServiceImpl
 * @Package cn.lonsun.manufacturer.internal.service.impl
 * @Description: 厂商管理Service
 * @date 2018/2/1 16:24
 */
@Service
public class ManufacturerServiceImpl extends MockService<ManufacturerEO> implements IManufacturerService {
    @Autowired
    private IManufacturerDao manufacturerDao;

    @Override
    public Pagination getPage(ManufacturerQueryVO queryVO) {
        return manufacturerDao.getPage(queryVO);
    }
}
