package cn.lonsun.security.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.security.internal.entity.SecurityMateria;
import cn.lonsun.security.internal.vo.MateriaQueryVO;

/**
 * Created by lonsun on 2016-12-12.
 */
public interface IMateriaService extends IMockService<SecurityMateria> {
    Pagination getmMateriaList(MateriaQueryVO materiaQueryVO);

    SecurityMateria saveMateria(SecurityMateria securityMateria);
}
