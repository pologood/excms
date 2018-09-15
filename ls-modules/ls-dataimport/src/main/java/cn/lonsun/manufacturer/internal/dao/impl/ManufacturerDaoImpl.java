package cn.lonsun.manufacturer.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.manufacturer.internal.dao.IManufacturerDao;
import cn.lonsun.manufacturer.internal.entity.ManufacturerEO;
import cn.lonsun.manufacturer.internal.vo.ManufacturerQueryVO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author caohaitao
 * @Title: ManufacturerDaoImpl
 * @Package cn.lonsun.manufacturer.internal.dao.impl
 * @Description: 厂商管理Dao
 * @date 2018/2/1 16:21
 */
@Repository
public class ManufacturerDaoImpl extends MockDao<ManufacturerEO> implements IManufacturerDao {
    @Override
    public Pagination getPage(ManufacturerQueryVO queryVO) {
        StringBuffer hql = new StringBuffer();
        List<Object> params = new ArrayList<Object>();
        hql.append("select m.id as id,m.name as name,m.productName as productName,m.uniqueCode as uniqueCode from ManufacturerEO m where m.recordStatus = ?");
        params.add(AMockEntity.RecordStatus.Normal.toString());
        if(!AppUtil.isEmpty(queryVO.getName())){
            hql.append(" and m.name like ?");
            params.add("%".concat(queryVO.getName()).concat("%"));
        }
        if(!AppUtil.isEmpty(queryVO.getProductName())){
            hql.append(" and m.productName like ?");
            params.add("%".concat(queryVO.getProductName()).concat("%"));
        }
        if(!AppUtil.isEmpty(queryVO.getUniqueCode())){
            hql.append(" and m.name like ?");
            params.add("%".concat(queryVO.getUniqueCode()).concat("%"));
        }
        hql.append(" order by m.createDate desc");
        Pagination pagination = getPagination(queryVO.getPageIndex(), queryVO.getPageSize(), hql.toString(), params.toArray(), ManufacturerEO.class);
        return pagination;
    }
}
