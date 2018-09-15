package cn.lonsun.security.internal.dao;

/**
 * Created by lonsun on 2016-12-12.
 */

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.security.internal.entity.SecurityMateria;
import cn.lonsun.security.internal.vo.MateriaQueryVO;

import java.util.List;


public interface IMateriaDao extends IMockDao<SecurityMateria> {

    Pagination getgetmMateriaList(MateriaQueryVO materiaQueryVO);

    List<SecurityMateria> checkMateria(SecurityMateria securityMateria);
}
