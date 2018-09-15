package cn.lonsun.security.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.security.internal.dao.IMateriaDao;
import cn.lonsun.security.internal.entity.SecurityMateria;
import cn.lonsun.security.internal.vo.MateriaQueryVO;
import cn.lonsun.security.internal.vo.MateriaVO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by lonsun on 2016-12-12.
 */
@Repository
public class MateriaDaoImpl extends MockDao<SecurityMateria> implements IMateriaDao {
    @Override
    public Pagination getgetmMateriaList(MateriaQueryVO materiaQueryVO) {
        StringBuilder hql =new StringBuilder();
        List<Object> param =new ArrayList<Object>();
        hql.append("select s.materiaId as id,c.id as baseContentId,s.materiaName as materiaName,s.year as year,s.periodical as periodical,c.isPublish as isPublish from BaseContentEO c,SecurityMateria s where c.id=s.baseContentId and c.recordStatus=? and s.siteId=? ");
        param.add(AMockEntity.RecordStatus.Normal.toString());
        param.add(materiaQueryVO.getSiteId());

        if(!AppUtil.isEmpty(materiaQueryVO.getKeyWord())){
            hql.append(" and s.materiaName like ?");
            param.add( "%"+SqlUtil.prepareParam4Query(materiaQueryVO.getKeyWord())+"%");

        }
        if(!AppUtil.isEmpty(materiaQueryVO.getCreate())){
            hql.append(" and s.year = ?");
            param.add(materiaQueryVO.getCreate());

        }
        hql.append("order by s.createDate desc");

        return getPagination(materiaQueryVO.getPageIndex(),materiaQueryVO.getPageSize(),hql.toString(),param.toArray(), MateriaVO.class);
    }

    @Override
    public List<SecurityMateria> checkMateria(SecurityMateria securityMateria) {
        StringBuilder hql =new StringBuilder();
        List<Object> param =new ArrayList<Object>();
        hql.append(" from SecurityMateria s where   s.recordStatus=? and s.siteId=? ");
        hql.append("  and s.year =? and s.periodical=?");
        param.add(AMockEntity.RecordStatus.Normal.toString());
        param.add(securityMateria.getSiteId());
        param.add(securityMateria.getYear());
        param.add(securityMateria.getPeriodical());

        if(!AppUtil.isEmpty(securityMateria.getMateriaId())){
          hql.append("  and s.materiaId!=?");
            param.add(securityMateria.getMateriaId());
        }
        return  getEntitiesByHql(hql.toString(),param.toArray());
    }
}
