package cn.lonsun.lsrobot.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lsrobot.dao.ILsRobotSourcesDao;
import cn.lonsun.lsrobot.entity.LsRobotSourcesEO;
import cn.lonsun.lsrobot.vo.RobotPageVO;
import cn.lonsun.site.template.util.SqlHelper;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author gu.fei
 * @version 2016-07-07 14:22
 */
@Repository
public class LsRobotSourcesDao extends MockDao<LsRobotSourcesEO> implements ILsRobotSourcesDao {

    @Override
    public Pagination getPageEntities(RobotPageVO vo) {
        Long pageIndex = vo.getPageIndex();
        Integer pageSize = vo.getPageSize();
        Long siteId = vo.getSiteId();
        if(null == siteId) {
            siteId = LoginPersonUtil.getSiteId();
        }
        StringBuilder hql = new StringBuilder("from LsRobotSourcesEO where siteId = ? and recordStatus = ?");

        if (null != vo.getIfActive()) {
            hql.append(" and ifActive='" + vo.getIfActive() + "'");
        }

        if (null != vo.getIfShow()) {
            hql.append(" and ifShow='" + vo.getIfShow() + "'");
        }
        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql.toString(), vo), new Object[]{siteId, AMockEntity.RecordStatus.Normal.toString()});
    }

    @Override
    public List<LsRobotSourcesEO> getEntities(RobotPageVO vo) {
        Long siteId = vo.getSiteId();
        if(null == siteId) {
            siteId = LoginPersonUtil.getSiteId();
        }
        StringBuilder hql = new StringBuilder("from LsRobotSourcesEO where siteId = ? and recordStatus = ?");
        if (null != vo.getIfActive()) {
            hql.append(" and ifActive='" + vo.getIfActive() + "'");
        }

        if (null != vo.getIfShow()) {
            hql.append(" and ifShow='" + vo.getIfShow() + "'");
        }

        return this.getEntitiesByHql(SqlHelper.getSearchAndOrderSql(hql.toString(), vo), new Object[]{siteId, AMockEntity.RecordStatus.Normal.toString()});
    }

    @Override
    public LsRobotSourcesEO getEntity(RobotPageVO vo) {
        Long siteId = vo.getSiteId();
        if(null == siteId) {
            siteId = LoginPersonUtil.getSiteId();
        }
        StringBuilder hql = new StringBuilder("from LsRobotSourcesEO where siteId = ? and recordStatus = ?");
        if (null != vo.getIfActive()) {
            hql.append(" and ifActive='" + vo.getIfActive() + "'");
        }

        if (null != vo.getIfShow()) {
            hql.append(" and ifShow='" + vo.getIfShow() + "'");
        }

        return this.getEntityByHql(SqlHelper.getSearchAndOrderSql(hql.toString(), vo),new Object[]{siteId, AMockEntity.RecordStatus.Normal.toString()});
    }

    @Override
    public Long getMaxSortNum() {
        StringBuilder hql = new StringBuilder("from LsRobotSourcesEO where sortNum = (select max(sortNum) from LsRobotSourcesEO where siteId = ?)");
        LsRobotSourcesEO eo = this.getEntityByHql(hql.toString(),new Object[]{LoginPersonUtil.getSiteId()});
        if(null != eo) {
            return eo.getSortNum();
        }

        return null;
    }
}
