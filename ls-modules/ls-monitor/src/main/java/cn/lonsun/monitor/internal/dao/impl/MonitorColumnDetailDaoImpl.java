package cn.lonsun.monitor.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.internal.dao.IMonitorColumnDetailDao;
import cn.lonsun.monitor.internal.entity.MonitorColumnDetailEO;
import cn.lonsun.monitor.internal.vo.MonitorDetailQueryVO;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * 日常监测栏目更新详细Dao实现类<br/>
 */

@Repository("monitorColumnDetailDao")
public class MonitorColumnDetailDaoImpl extends BaseDao<MonitorColumnDetailEO> implements IMonitorColumnDetailDao {
    /**
     * 获取日常监测栏目更新详细分页
     *
     */
    @Override
    public Pagination getPage(MonitorDetailQueryVO queryVO) {
        StringBuffer hql=new StringBuffer("FROM MonitorColumnDetailEO WHERE 1 = 1 ");

        Map<String,Object> map=new HashMap<String,Object>();

        if(null!=queryVO.getMonitorId()){
            hql.append(" and monitorId=:monitorId");
            map.put("monitorId", queryVO.getMonitorId());
        }else if(null!=queryVO.getMonitorIds()&&queryVO.getMonitorIds().size()>0){
            hql.append(" and monitorId in (:monitorIds) ");
            map.put("monitorIds", queryVO.getMonitorIds().toArray());
        }
        if(null!=queryVO.getColumnId()){
            hql.append(" and columnId=:columnId");
            map.put("columnId", queryVO.getColumnId());
        }
        if(null!=queryVO.getColumnType()){
            hql.append(" and columnType in (:columnType) ");
            map.put("columnType", queryVO.getColumnType().toArray());
        }
        if(null!=queryVO.getInfoType()){
            hql.append(" and infoType = :infoType ");
            map.put("infoType", queryVO.getInfoType());
        }
        if(null!=queryVO.getUpdateCount()){
            hql.append(" and updateCount<=:updateCount");
            map.put("updateCount", queryVO.getUpdateCount());
        }
        if(queryVO.getStartDate()!=null){
            hql.append(" and createDate>:startDate");
            map.put("startDate", queryVO.getStartDate());
        }
        if(queryVO.getEndDate()!=null){
            hql.append(" and createDate<:endDate");
            map.put("endDate", queryVO.getEndDate());
        }
        hql.append(" order by createDate desc");
        return getPagination(queryVO.getPageIndex(), queryVO.getPageSize(), hql.toString(), map);
    }

    /**
     * 获取规定时间内已经更新数据的首页栏目
     * @param columnIds
     * @param st
     * @param ed
     * @return
     */
    @Override
    public List<MonitorColumnDetailEO> getIndexUpdatedColumns(String columnIds,Date st,Date ed){
        if(AppUtil.isEmpty(columnIds)){
            return new ArrayList<MonitorColumnDetailEO>();
        }
        String hql = "SELECT new MonitorColumnDetailEO(b.columnId||'' as columnId,sum( " +
                "CASE WHEN b.publishDate >= ? AND b.publishDate < ? THEN 1 ELSE 0 END  " +
                ") as updateCount, max(b.publishDate) as lastPublishDate, " +
                "TRUNC(TRUNC(sysdate) - TRUNC(max(b.publishDate))) AS unPublishDays_ ) "
                + " from BaseContentEO b where b.recordStatus = ? and b.columnId in ("+columnIds+") and b.isPublish = ? and b.publishDate is not null "
                + " group by b.columnId ";
        List<Object> values = new ArrayList<Object>();

        values.add(st);
        values.add(ed);

        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(1);
        return (List<MonitorColumnDetailEO>)getObjects(hql,values.toArray(),null);
    }


    @Override
    public List<MonitorColumnDetailEO> getUpdatedColumns(String columnIds, Integer updateCycle) {
        if(AppUtil.isEmpty(columnIds)){
            return new ArrayList<MonitorColumnDetailEO>();
        }
        String hql = "SELECT new MonitorColumnDetailEO(b.columnId||'' as columnId,sum( " +
                "CASE WHEN b.publishDate >= ? AND b.publishDate <= ? THEN 1 ELSE 0 END  " +
                ") as updateCount, max(b.publishDate) as lastPublishDate, " +
                "TRUNC(TRUNC(sysdate) - TRUNC(max(b.publishDate))) AS unPublishDays_ ) "
                + " from BaseContentEO b where b.recordStatus = ? and b.columnId in ("+columnIds+") and b.isPublish = ? and b.publishDate is not null "
                + " group by b.columnId ";
        List<Object> values = new ArrayList<Object>();

        Date endDate = new Date();//监测结束时间为当前时间
        Date startDate ;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - updateCycle);
        startDate =  calendar.getTime();

        values.add(startDate);
        values.add(endDate);
//        values.add(endDate);

        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(1);
        return (List<MonitorColumnDetailEO>)getObjects(hql,values.toArray(),null);
    }


    /**
     * 获取规定时间内已经更新数据的首页信息公开栏目
     * @param organCatId
     * @param st
     * @param ed
     * @return
     */
    @Override
    public List<MonitorColumnDetailEO> getIndexUpdatedPublics(String[] organCatId,Date st,Date ed){
        String hql = "SELECT new MonitorColumnDetailEO(b.columnId||'_'||p.catId as columnId,sum( " +
                "CASE WHEN b.publishDate >= ? AND b.publishDate < ? THEN 1 ELSE 0 END  " +
                ") as updateCount, max(b.publishDate) as lastPublishDate, " +
                "TRUNC(TRUNC(sysdate) - TRUNC(max(b.publishDate))) AS unPublishDays_ ) " +
                "from BaseContentEO b,PublicContentEO p where b.id = p.contentId and b.recordStatus = ? " +
                "and p.recordStatus = ? and b.isPublish = ? and b.publishDate is not null " ;
        List<Object> values = new ArrayList<Object>();

        values.add(st);
        values.add(ed);

        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(1);

        if(organCatId!=null&&organCatId.length>0){
            hql += "and (  ";
            for(int i=0;i<organCatId.length;i++){
                String[] ids =  organCatId[i].split("_");
                if(i==0){
                    hql += " ( b.columnId = ? and p.catId = ? ) ";
                }else{
                    hql += " or ( b.columnId = ? and p.catId = ? ) ";
                }
                values.add(Long.parseLong(ids[0]));
                values.add(Long.parseLong(ids[1]));
            }
            hql += " ) ";
        }

        hql += " group by b.columnId,p.catId ";
        return (List<MonitorColumnDetailEO>)getObjects(hql,values.toArray(),null);
    }

    @Override
    public List<MonitorColumnDetailEO> getUpdatedPublics(String[] organCatId, Integer updateCycle) {
        String hql = "SELECT new MonitorColumnDetailEO(b.columnId||'_'||p.catId as columnId,sum( " +
                "CASE WHEN b.publishDate >= ? AND b.publishDate <= ? THEN 1 ELSE 0 END  " +
                ") as updateCount, max(b.publishDate) as lastPublishDate, " +
                "TRUNC(TRUNC(sysdate) - TRUNC(max(b.publishDate))) AS unPublishDays_ ) " +
                "from BaseContentEO b,PublicContentEO p where b.id = p.contentId and b.recordStatus = ? " +
                "and p.recordStatus = ? and b.isPublish = ? and b.publishDate is not null " ;
        List<Object> values = new ArrayList<Object>();

        Date endDate = new Date();//监测结束时间为当前时间
        Date startDate ;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - updateCycle);
        startDate =  calendar.getTime();

        values.add(startDate);
        values.add(endDate);

        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(1);


        if(organCatId!=null&&organCatId.length>0){
            hql += "and (  ";
            for(int i=0;i<organCatId.length;i++){
                String[] ids =  organCatId[i].split("_");
                if(i==0){
                    hql += " ( b.columnId = ? and p.catId = ? ) ";
                }else{
                    hql += " or ( b.columnId = ? and p.catId = ? ) ";
                }
                values.add(Long.parseLong(ids[0]));
                values.add(Long.parseLong(ids[1]));
            }
            hql += " ) ";
        }

        hql += " group by b.columnId,p.catId ";
        return (List<MonitorColumnDetailEO>)getObjects(hql,values.toArray(),null);
    }

    @Override
    public List<MonitorColumnDetailEO> getList(MonitorDetailQueryVO queryVO) {
        StringBuffer hql=new StringBuffer("FROM MonitorColumnDetailEO WHERE 1 = 1 ");

        Map<String,Object> map=new HashMap<String,Object>();

        if(null!=queryVO.getMonitorId()){
            hql.append(" and monitorId=:monitorId");
            map.put("monitorId", queryVO.getMonitorId());
        }else if(null!=queryVO.getMonitorIds()&&queryVO.getMonitorIds().size()>0){
            hql.append(" and monitorId in (:monitorIds) ");
            map.put("monitorIds", queryVO.getMonitorIds().toArray());
        }
        if(null!=queryVO.getColumnId()){
            hql.append(" and columnId=:columnId");
            map.put("columnId", queryVO.getColumnId());
        }
        if(null!=queryVO.getColumnType()){
            hql.append(" and columnType in (:columnType) ");
            map.put("columnType", queryVO.getColumnType().toArray());
        }
        if(null!=queryVO.getInfoType()){
            hql.append(" and infoType = :infoType ");
            map.put("infoType", queryVO.getInfoType());
        }
        if(null!=queryVO.getUpdateCount()){
            hql.append(" and updateCount<=:updateCount");
            map.put("updateCount", queryVO.getUpdateCount());
        }
        if(queryVO.getStartDate()!=null){
            hql.append(" and createDate>:startDate");
            map.put("startDate", queryVO.getStartDate());
        }
        if(queryVO.getEndDate()!=null){
            hql.append(" and createDate<:endDate");
            map.put("endDate", queryVO.getEndDate());
        }
        hql.append(" order by createDate desc");
        return getEntities(hql.toString(), map,null);
    }

    @Override
    public List<MonitorColumnDetailEO> getList(Long monitorId,String[] columnType,String infoType,Long updateCount) {
        StringBuffer hql=new StringBuffer("FROM MonitorColumnDetailEO WHERE 1 = 1 ");

        Map<String,Object> map=new HashMap<String,Object>();

        if(null!=monitorId){
            hql.append(" and monitorId=:monitorId");
            map.put("monitorId", monitorId);
        }
        if(null!=columnType&&columnType.length>0){
            hql.append(" and columnType in (:columnType) ");
            map.put("columnType", columnType);
        }
        if(!AppUtil.isEmpty(infoType)){
            hql.append(" and infoType = :infoType ");
            map.put("infoType", infoType);
        }
        if(null!=updateCount){
            hql.append(" and updateCount<=:updateCount");
            map.put("updateCount", updateCount);
        }
        hql.append(" order by createDate desc");
        return getEntities(hql.toString(), map,null);
    }

    @Override
    public Long getCount(Long monitorId, String[] columnType,String infoType, Long updateCount) {
        StringBuffer hql=new StringBuffer("FROM MonitorColumnDetailEO WHERE 1 = 1 ");

        Map<String,Object> map=new HashMap<String,Object>();

        if(null!=monitorId){
            hql.append(" and monitorId=:monitorId");
            map.put("monitorId", monitorId);
        }
        if(null!=columnType&&columnType.length>0){
            hql.append(" and columnType in (:columnType) ");
            map.put("columnType", columnType);
        }
        if(!AppUtil.isEmpty(infoType)){
            hql.append(" and infoType = :infoType ");
            map.put("infoType", infoType);
        }
        if(null!=updateCount){
            hql.append(" and updateCount<=:updateCount");
            map.put("updateCount", updateCount);
        }
        hql.append(" order by createDate desc");
        return getCount(hql.toString(), map);
    }

    @Override
    public Long getCounts(MonitorDetailQueryVO queryVO) {
        StringBuffer hql=new StringBuffer("FROM MonitorColumnDetailEO WHERE 1 = 1 ");

        Map<String,Object> map=new HashMap<String,Object>();

        if(null!=queryVO.getMonitorId()){
            hql.append(" and monitorId=:monitorId");
            map.put("monitorId", queryVO.getMonitorId());
        }
        if(null!=queryVO.getColumnId()){
            hql.append(" and columnId=:columnId");
            map.put("columnId", queryVO.getColumnId());
        }
        if(null!=queryVO.getColumnType()){
            hql.append(" and columnType in (:columnType) ");
            map.put("columnType", queryVO.getColumnType().toArray());
        }
        if(null!=queryVO.getUpdateCount()){
            hql.append(" and updateCount<=:updateCount");
            map.put("updateCount", queryVO.getUpdateCount());
        }
        if(queryVO.getStartDate()!=null){
            hql.append(" and createDate>:startDate");
            map.put("startDate", queryVO.getStartDate());
        }
        if(queryVO.getEndDate()!=null){
            hql.append(" and createDate<:endDate");
            map.put("endDate", queryVO.getEndDate());
        }
        hql.append(" order by createDate desc");
        return getCount(hql.toString(), map);
    }


    @Override
    public List<Long> getUnEmptyColumnList(Long siteId) {
        StringBuffer sql = new StringBuffer();

        sql.append("select t.columnId from BaseContentEO t where t.recordStatus = ? and t.siteId= ? ");

        sql.append(" and t.typeCode in ('articleNews','pictureNews')  ");

        sql.append(" and t.isPublish = ? ");

        sql.append(" group by t.columnId ");

        return (List<Long>) getObjects(sql.toString(),new Object[]{AMockEntity.RecordStatus.Normal.toString(),siteId,1});
    }

    @Override
    public List<MonitorColumnDetailEO> getUnEmptyColumnInfoList(Long siteId) {
        String hql = "SELECT new MonitorColumnDetailEO(b.columnId||'' as columnId,count(b.columnId) as updateCount,  " +
                " max(b.publishDate) as lastPublishDate, " +
                "TRUNC(TRUNC(sysdate) - TRUNC(max(b.publishDate))) AS unPublishDays_ ) "
                + " from BaseContentEO b where b.recordStatus = ? and b.isPublish = ? and b.siteId= ? and b.publishDate is not null "
                + " and b.typeCode in ('articleNews','pictureNews') "
                + " group by b.columnId ";
        List<Object> values = new ArrayList<Object>();

        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(1);
        values.add(siteId);
        return (List<MonitorColumnDetailEO>)getObjects(hql,values.toArray(),null);
    }

}
