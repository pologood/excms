package cn.lonsun.manufacturer.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.manufacturer.internal.entity.ManufacturerEO;
import cn.lonsun.manufacturer.internal.vo.ManufacturerQueryVO;

/**
 * @author caohaitao
 * @Title: IManufacturerService
 * @Package cn.lonsun.manufacturer.internal.service
 * @Description: 厂商管理Service
 * @date 2018/2/1 16:20
 */
public interface IManufacturerService extends IMockService<ManufacturerEO>{
    Pagination getPage(ManufacturerQueryVO queryVO);
}
