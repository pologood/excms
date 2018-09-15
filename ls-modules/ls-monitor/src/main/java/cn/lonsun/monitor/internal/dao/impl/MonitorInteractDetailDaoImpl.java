package cn.lonsun.monitor.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardForwardEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardEditVO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.internal.dao.IMonitorInteractDetailDao;
import cn.lonsun.monitor.internal.entity.MonitorInteractDetailEO;
import cn.lonsun.monitor.internal.vo.MonitorDetailQueryVO;
import cn.lonsun.system.role.internal.util.RoleAuthUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * 日常监测互动更新详细Dao实现类<br/>
 */

@Repository("monitorInteractDetailDao")
public class MonitorInteractDetailDaoImpl extends BaseDao<MonitorInteractDetailEO> implements IMonitorInteractDetailDao {
    /**
     * 获取日常监测互动更新详细分页
     */
    @Override
    public Pagination getPage(MonitorDetailQueryVO queryVO) {
        StringBuffer hql=new StringBuffer("FROM MonitorInteractDetailEO WHERE 1 = 1 ");

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

        if(null!=queryVO.getUnreplyCount()){
            hql.append(" and unreplyCount>:unreplyCount");
            map.put("unreplyCount", queryVO.getUnreplyCount());
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

    @Override
    public Pagination getUnReplyPage(Long pageIndex, Integer pageSize, String contentIds) {
        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.createDate as createDate, c.isPublish as isPublish")
                .append(",c.columnId as columnId,c.siteId as siteId,c.id as baseContentId,m.id as id,m.messageBoardContent as messageBoardContent,m.addDate as addDate")
                .append(",m.resourceType as resourceType,m.openId as openId,m.personPhone as personPhone,m.personIp as personIp,m.personName as personName")
                .append(",m.attachName as attachName,m.attachId as attachId,m.docNum as docNum,m.dealStatus as dealStatus")
                .append(",m.classCode as classCode,m.randomCode as randomCode,m.isPublic as isPublic,m.isPublicInfo as isPublicInfo")
                .append(" from BaseContentEO c,MessageBoardEO m where m.baseContentId=c.id and c.recordStatus=? and m.recordStatus=? ");

        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());

        if(!AppUtil.isEmpty(contentIds)){
            Long[] contentId = AppUtil.getLongs(contentIds,",");
            if(contentId.length<=1000){
                hql.append(" and c.id in ("+contentIds+") ");
            }else{//in最多只能查询1000个，超过1000个需要做处理
                List<Long> list = Arrays.asList(contentId);
                int size =  (int)Math.ceil(contentId.length/1000.0);//向上取整
                hql.append(" and ( ");
                for(int i=0;i<size;i++){
                    if(i>0){
                        hql.append(" or ");
                    }
                    int stIndex = i*1000;
                    int edIndex = (i+1)*1000;
                    if(edIndex>contentId.length){
                        edIndex = contentId.length;
                    }
                    hql.append(" c.id in ("+StringUtils.join(list.subList(stIndex,edIndex))+") ");
                }
                hql.append(" ) ");
            }

        }else{
            hql.append(" and 1<>1 ");
        }


        hql.append(" order by m.createDate desc");

        Pagination page = getPagination(pageIndex, pageSize, hql.toString(), values.toArray(), MessageBoardEditVO.class);

        return page;
    }

    /**
     * 获取日常监测互动更新详细 不分页
     */
    @Override
    public List<MonitorInteractDetailEO> getList(MonitorDetailQueryVO queryVO) {
        StringBuffer hql=new StringBuffer("FROM MonitorInteractDetailEO WHERE 1 = 1 ");

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

        if(null!=queryVO.getUnreplyCount()){
            hql.append(" and unreplyCount>:unreplyCount");
            map.put("unreplyCount", queryVO.getUnreplyCount());
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


    /**
     * 获取规定时间内政务咨询类(留言)更新数和未回复数
     */
    @Override
    public List<MonitorInteractDetailEO> getZWZXInfo(String columnIds, Integer updateCycle, Integer unreplyCycle) {
        if(unreplyCycle==null||unreplyCycle==0){
            unreplyCycle = 90; //默认查询三个月未回复的留言数
        }

        if(updateCycle==null||updateCycle==0){
            updateCycle = 365;//默认查询前一年时间内的数据
        }

        List<Object> values = new ArrayList<Object>();
        Date endDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - updateCycle);
        Date startDate =  calendar.getTime();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT d.COLUMN_ID AS columnId,count(d.COLUMN_ID) AS updateCount, " +
                "sum(CASE WHEN e.update_days > ?  THEN 1 ELSE 0 END) AS  unreplyCount," +
                "wm_concat(CASE WHEN e.update_days > ?  THEN to_char(d.ID)  ELSE NULL END) AS unreplyIds " +
                "FROM CMS_BASE_CONTENT d ," +
                "(" +
                "SELECT a.BASE_CONTENT_ID,a.ADD_DATE,nvl(b.reply_date,sysdate) reply_date,TRUNC(TRUNC(nvl(b.reply_date,sysdate))-TRUNC(a.ADD_DATE)) AS update_days " +
                "FROM CMS_MESSAGE_BOARD a LEFT JOIN (" +
                "SELECT c.MESSAGE_BOARD_ID,min(c.CREATE_DATE) AS reply_date,count(c.MESSAGE_BOARD_ID) AS reply_count " +
                "FROM  CMS_MESSAGE_BOARD_REPLY c GROUP BY c.MESSAGE_BOARD_ID ) b " +
                "on a.ID = b.MESSAGE_BOARD_ID" +
                ") e WHERE d.id = e.BASE_CONTENT_ID AND d.record_status= ? AND d.COLUMN_ID IS NOT NULL ");
        if(!AppUtil.isEmpty(columnIds)){
            sql.append("  and d.COLUMN_ID in (" + columnIds + ") ");
        }else{
            sql.append("  and 1 <> 1 ");
        }


        values.add(unreplyCycle);
        values.add(unreplyCycle);
        values.add(AMockEntity.RecordStatus.Normal.toString());

        sql.append("  and d.IS_PUBLISH = ? ");
        values.add(1);

        sql.append("  and d.CREATE_DATE>= ? and d.CREATE_DATE<= ? ");
        values.add(startDate);
        values.add(endDate);

        sql.append("GROUP BY d.COLUMN_ID");

        List<String> fields = new ArrayList<String>();
        fields.add("columnId");
        fields.add("updateCount");
        fields.add("unreplyCount");
        fields.add("unreplyIds");
        String[] arr = new String[fields.size()];
        return (List<MonitorInteractDetailEO>) getBeansBySql(sql.toString(), values.toArray(), MonitorInteractDetailEO.class, fields.toArray(arr));
    }

    @Override
    public List<MonitorInteractDetailEO> getUpdatedColumns(String columnIds, Integer updateCycle) {
        String hql = "SELECT new MonitorInteractDetailEO(b.columnId as columnId,count(b.columnId) as updateCount) "
                + " from BaseContentEO b where b.recordStatus = ? and b.columnId in ("+columnIds+") and b.isPublish = ? and b.publishDate is not null "
                + " and b.publishDate >= ? and b.publishDate <= ? group by b.columnId having count(b.columnId) > 0 ";
        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(1);

        Date endDate = new Date();//监测结束时间为当前时间
        Date startDate ;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - updateCycle);
        startDate =  calendar.getTime();


        values.add(startDate);
        values.add(endDate);
        return (List<MonitorInteractDetailEO>)getObjects(hql,values.toArray(),null);
    }

    /**
     * 获取符合条件的记录数目
     * @param queryVO
     * @return
     */
    @Override
    public Long getCounts(MonitorDetailQueryVO queryVO) {
        StringBuffer hql=new StringBuffer("FROM MonitorInteractDetailEO WHERE 1 = 1 ");

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
        if(null!=queryVO.getUnreplyCount()){
            hql.append(" and unreplyCount>=:unreplyCount");
            map.put("unreplyCount", queryVO.getUnreplyCount());
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
    public List<MonitorInteractDetailEO> getList(Long monitorId,String[] columnType,Long updateCount) {
        StringBuffer hql=new StringBuffer("FROM MonitorInteractDetailEO WHERE 1 = 1 ");

        Map<String,Object> map=new HashMap<String,Object>();

        if(null!=monitorId){
            hql.append(" and monitorId=:monitorId");
            map.put("monitorId", monitorId);
        }
        if(null!=columnType&&columnType.length>0){
            hql.append(" and columnType in (:columnType) ");
            map.put("columnType", columnType);
        }
        if(null!=updateCount){
            hql.append(" and updateCount<=:updateCount");
            map.put("updateCount", updateCount);
        }
        hql.append(" order by createDate desc");
        return getEntities(hql.toString(), map,null);
    }

    @Override
    public Long getCount(Long monitorId, String[] columnType, Long updateCount) {
        StringBuffer hql=new StringBuffer("FROM MonitorInteractDetailEO WHERE 1 = 1 ");

        Map<String,Object> map=new HashMap<String,Object>();

        if(null!=monitorId){
            hql.append(" and monitorId=:monitorId");
            map.put("monitorId", monitorId);
        }
        if(null!=columnType&&columnType.length>0){
            hql.append(" and columnType in (:columnType) ");
            map.put("columnType", columnType);
        }
        if(null!=updateCount){
            hql.append(" and updateCount<=:updateCount");
            map.put("updateCount", updateCount);
        }
        hql.append(" order by createDate desc");
        return getCount(hql.toString(), map);
    }

}
