package cn.lonsun.site.site.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.site.internal.dao.ISiteConfigDao;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.site.site.vo.SiteVO;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 站点配置DAO实现类<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-8-24 <br/>
 */
@Repository("siteConfigDao")
public class SiteConfigDaoImpl extends MockDao<SiteConfigEO> implements ISiteConfigDao {

    final String whereStr = " where recordStatus='Normal' and isEnable=1 ";

    @Override
    public Integer getNewSortNum(Long parentId, boolean isSub) {
        //判断当前的最大的排序号
        StringBuilder sb = new StringBuilder("select max(sortNum) from cn.lonsun.indicator.internal.entity.IndicatorEO ");
        sb.append(whereStr);
        sb.append(" and parentId=").append(parentId);
        if (isSub) {
            sb.append(" and type ='" + IndicatorEO.Type.SUB_Site.toString() + "'");
        } else {
            sb.append(" and type ='" + IndicatorEO.Type.CMS_Site.toString() + "'");
        }
        Query query = this.getCurrentSession().createQuery(sb.toString());
        Integer maxSortNum = (Integer) query.uniqueResult();
        //判断当前的排序号是否为空，若为空，则返回2，不为空则返回当前最大排序号加2
        if (isSub) {
            return maxSortNum == null ? 100 : (maxSortNum + 2);
        } else {
            return maxSortNum == null ? 2 : (maxSortNum + 2);
        }
    }

    @Override
    public SiteConfigEO getSiteConfigByIndicatorId(Long indicatorId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("indicatorId", indicatorId);
        List<SiteConfigEO> list = getEntities(SiteConfigEO.class, map);
        if (list == null || list.size() == 0) {
            return null;
        }
        SiteConfigEO eo = list.get(0);
        return eo;
    }

    @Override
    public List<SiteVO> getSiteTree(Long indicatorId) {
        String hql = " select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId,r.isEnable as isEnable "
            + ",r.type as type,r.sortNum as sortNum ,r.open as open,r.createDate as createDate ,r.uri as uri"
            + ",s.siteConfigId as siteConfigId,s.keyWords as keyWords,s.description as description "
            + ",s.videoTransUrl as videoTransUrl,s.isVideoTrans as isVideoTrans "
            + " from IndicatorEO r,SiteConfigEO s where r.indicatorId=s.indicatorId and r.type='CMS_Site' and "
            + " r.recordStatus='Normal' and r.isEnable=1 and  s.recordStatus='Normal' ";
        if (indicatorId != null) {
            hql += " and r.parentId=" + indicatorId;
        }
        hql += " order by r.sortNum asc,r.createDate asc";
        List<SiteVO> list = (List<SiteVO>) getBeansByHql(hql, new Object[]{}, SiteVO.class);
        return list;
    }

    @Override
    public List<SiteMgrEO> getByComColumnId(Long comColumnId) {
        String hql = " select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId "
            + ",r.type as type,r.sortNum as sortNum ,r.createDate as createDate ,r.uri as uri"
            + ",r.siteConfigId as siteConfigId,r.comColumnId as comColumnId "
            + " from SiteMgrEO r where r.type='SUB_Site' and  r.recordStatus='Normal'";
        if (comColumnId != null) {
            hql += " and r.comColumnId=" + comColumnId;
        }
        hql += " order by r.sortNum asc,r.createDate asc";
        List<SiteMgrEO> list = (List<SiteMgrEO>) getBeansByHql(hql, new Object[]{}, SiteMgrEO.class);
        return list;
    }

    @Override
    public SiteMgrEO getById(Long indicatorId) {
        String hql = " select r.indicatorId as indicatorId,r.name as name,r.siteTitle as siteTitle,r.parentId as parentId "
            + ",r.type as type,r.sortNum as sortNum ,r.createDate as createDate ,r.uri as uri,r.isParent as isParent"
            + ",r.siteConfigId as siteConfigId,r.keyWords as keyWords,r.description as description "
            + ",r.videoTransUrl as videoTransUrl,r.isVideoTrans as isVideoTrans,r.recordStatus as recordStatus,r.unitIds as unitIds,r.unitNames as unitNames "
            + ",r.indexTempId as indexTempId,r.commentTempId as commentTempId,r.errorTempId as errorTempId,r.publicTempId as publicTempId"
            + ",r.searchTempId as searchTempId,r.memberId as memberId,r.stationId as stationId,r.stationPwd as stationPwd,r.comColumnId as comColumnId"
            + ",r.siteTempId as siteTempId,r.isWap as isWap,r.wapTempId as wapTempId,r.wapPublicTempId as wapPublicTempId,r.phoneTempId as phoneTempId"
            + " from SiteMgrEO r where r.indicatorId=" + indicatorId;
        List<SiteMgrEO> list = (List<SiteMgrEO>) getBeansByHql(hql, new Object[]{}, SiteMgrEO.class);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public Pagination getSiteInfos(Long pageIndex, Integer pageSize){
        String hql = " from SiteMgrEO r where 1=1 ";
        return getPagination(pageIndex,pageSize,hql,new String[]{});
    }


}
