package cn.lonsun.manufacturer.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.manufacturer.internal.entity.ManufacturerEO;
import cn.lonsun.manufacturer.internal.vo.ManufacturerQueryVO;

/**
 * @author caohaitao
 * @Title: IManufacturerDao
 * @Package cn.lonsun.manufacturer.internal.dao
 * @Description: 厂商管理Dao
 * @date 2018/2/1 16:20
 */
public interface IManufacturerDao extends IMockDao<ManufacturerEO> {
    Pagination getPage(ManufacturerQueryVO queryVO);
}
