package cn.lonsun.monitor.internal.dao.impl;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.monitor.internal.entity.MonitorColumnDetailEO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日常监测栏目更新详细Dao实现类<br/>
 */

@Repository("monitorColumnDetailMySqlDao")
public class MonitorColumnDetailMySqlDaoImpl extends MonitorColumnDetailDaoImpl {


    /**
     * 获取规定时间内已经更新数据的首页栏目
     * @param columnIds
     * @param st
     * @param ed
     * @return
     */
    @Override
    public List<MonitorColumnDetailEO> getIndexUpdatedColumns(String columnIds,Date st,Date ed){
        String hql = "SELECT new MonitorColumnDetailEO(CONCAT(b.columnId,'') as columnId,sum( " +
                "CASE WHEN b.publishDate >= ? AND b.publishDate < ? THEN 1 ELSE 0 END  " +
                ") as updateCount, max(b.publishDate) as lastPublishDate, " +
                "datediff(now(),max(b.publishDate)) AS unPublishDays_ ) "
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
        String hql = "SELECT new MonitorColumnDetailEO(CONCAT(b.columnId,'') as columnId,sum( " +
                "CASE WHEN b.publishDate >= ? AND b.publishDate <= ? THEN 1 ELSE 0 END  " +
                ") as updateCount, max(b.publishDate) as lastPublishDate, " +
                "datediff(now(),max(b.publishDate)) AS unPublishDays_ ) "
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
        String hql = "SELECT new MonitorColumnDetailEO(concat(b.columnId,'_',p.catId) as columnId,sum( " +
                "CASE WHEN b.publishDate >= ? AND b.publishDate < ? THEN 1 ELSE 0 END  " +
                ") as updateCount, max(b.publishDate) as lastPublishDate, " +
                "datediff(now(),max(b.publishDate)) AS unPublishDays_ ) " +
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
        String hql = "SELECT new MonitorColumnDetailEO(concat(b.columnId,'_',p.catId) as columnId,sum( " +
                "CASE WHEN b.publishDate >= ? AND b.publishDate <= ? THEN 1 ELSE 0 END  " +
                ") as updateCount, max(b.publishDate) as lastPublishDate, " +
                "datediff(now(),max(b.publishDate)) AS unPublishDays_ ) " +
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
    public List<MonitorColumnDetailEO> getUnEmptyColumnInfoList(Long siteId) {
        String hql = "SELECT new MonitorColumnDetailEO(concat(b.columnId,'') as columnId,count(b.columnId) as updateCount,  " +
                " max(b.publishDate) as lastPublishDate, " +
                "datediff(now(),max(b.publishDate)) AS unPublishDays_ ) "
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
