package cn.lonsun.monitor.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.internal.dao.IMonitorInteractDetailDao;
import cn.lonsun.monitor.internal.entity.MonitorInteractDetailEO;
import cn.lonsun.monitor.internal.vo.MonitorDetailQueryVO;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * 日常监测互动更新详细Dao实现类<br/>
 */

@Repository("monitorInteractDetailMySqlDao")
public class MonitorInteractDetailMySqlDaoImpl extends MonitorInteractDetailDaoImpl {

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
                "sum(CASE WHEN e.update_days > ?  THEN 1 ELSE 0 END) AS  unreplyCount, " +
                "group_concat(CASE WHEN e.update_days > ?  THEN CONCAT(d.ID,'')  ELSE NULL END) AS unreplyIds " +
                "FROM CMS_BASE_CONTENT d ," +
                "(" +
                "SELECT a.BASE_CONTENT_ID,a.ADD_DATE,IFNULL(b.reply_date,now()) reply_date,datediff(IFNULL(b.reply_date,now()),a.ADD_DATE) AS update_days " +
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


}
