package cn.lonsun.content.statistics.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.statistics.internal.dao.IClickCountStatDao;
import cn.lonsun.content.statistics.internal.entity.ClickCountStatEO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.statistics.WordListVO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 点击数统计数据持久层实现
 * Created by zhushouyong on 2016-8-12.
 */
@Repository
public class ClickCountStatDaoImpl extends BaseDao<ClickCountStatEO> implements IClickCountStatDao {

    /**
     * 根据日期获取内容IDs
     *
     * @param siteId
     * @param topCount
     * @param date
     * @param sort
     * @return
     */
    @Override
    public List<Long> getContentIdsByDate(Long siteId, Integer topCount, Date date, Integer sort) {
        List<Object> param = new ArrayList<Object>();
        String hql = "select t.contentId from ";
        hql += "(select t1.content_id as contentId,max(t1.count) as totalCount from CMS_CLICK_COUNT_STAT t1,CMS_BASE_CONTENT t2  where t1.content_id = t2.id ";
        if(date!=null){
            hql += "and t1.stat_day >= ? ";
            param.add(date);
        }
        hql += "and t1.site_id = ? and t2.record_status = ? group by t1.content_id) t ";
        param.add(siteId);
        param.add(AMockEntity.RecordStatus.Normal.toString());

        if(Integer.valueOf(0).equals(sort)){
            hql += "order by t.totalCount desc ";
        }else if(Integer.valueOf(1).equals(sort)){
            hql += "order by t.totalCount ";
        }
        List<Object> list = (List<Object>)getObjectsBySql(hql,param.toArray(),topCount);
        List<Long> result = null;
        if(null != list && list.size() > 0){
            result = new ArrayList<Long>();
            for(Object obj : list){
                result.add(AppUtil.getLong(obj));
            }
        }
        return result;
    }

    /**
     * 根据日期获取栏目IDs
     *
     * @param siteId
     * @param topCount
     * @param date
     * @param sort
     * @return
     */
    @Override
    public List<Object> getColumnIdsByDate(Long siteId, Integer topCount, Date date, Integer sort) {
        List<Object> param = new ArrayList<Object>();
        String hql = "select t.columnId,t.count,r.name,r.url_path as urlPath from ";
        hql += "(" +
                "SELECT t1.COLUMN_ID as columnId,sum(t1.COUNT) as count FROM CMS_CLICK_COUNT_STAT t1,CMS_BASE_CONTENT t2 WHERE t1.content_id = t2.id " ;
        if(date!=null){
            hql += "AND  t1.STAT_DAY>= ? ";
            param.add(date);
        }
        hql +=  " AND t1.site_id = ? AND t2.RECORD_STATUS = ?  GROUP BY t1.COLUMN_ID ) t left join cms_column_mgr r on t.columnId = r.indicator_id ";
        param.add(siteId);
        param.add(AMockEntity.RecordStatus.Normal.toString());
        if(Integer.valueOf(0).equals(sort)){
            hql += "order by t.count desc ";
        }else if(Integer.valueOf(1).equals(sort)){
            hql += "order by t.count ";
        }
        List<Object> list = (List<Object>)getObjectsBySql(hql,param.toArray(),topCount);
//        List<Long> result = null;
//        if(null != list && list.size() > 0){
//            result = new ArrayList<Long>();
//            for(Object obj : list){
//                result.add(AppUtil.getLong(obj));
//            }
//        }
        return list;
    }
}
