package cn.lonsun.lottery.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.lottery.internal.dao.ILotteryRecordDao;
import cn.lonsun.lottery.internal.entity.LotteryRecordEO;
import cn.lonsun.lottery.internal.vo.QuestionsQueryVO;
import cn.lonsun.util.DateUtil;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by lizhi on 2017-1-15.
 */
@Repository
public class LotteryRecordDaoImpl extends MockDao<LotteryRecordEO> implements ILotteryRecordDao {
    @Override
    public Pagination getRecords(QuestionsQueryVO vo) throws ParseException {

        StringBuffer hql = new StringBuffer();
        hql.append("from  LotteryRecordEO t where t.recordStatus =? ");
        List<Object> param =new ArrayList<Object>();
        param.add(AMockEntity.RecordStatus.Normal.toString());
        if(!AppUtil.isEmpty(vo.getName())){
            hql.append("  and t.name like ?");
            param.add("%"+ SqlUtil.prepareParam4Query(vo.getName())+"%");
        }
        if(!AppUtil.isEmpty(vo.getPhone())){
            hql.append("  and t.phone like ?");
            param.add("%"+ SqlUtil.prepareParam4Query(vo.getPhone())+"%");
        }

        if(!AppUtil.isEmpty(vo.getDate())){
            SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy-MM-dd");

            Calendar now = Calendar.getInstance();
            now.setTime(dateFormat.parse(vo.getDate()));
            now.set(Calendar.DATE, now.get(Calendar.DATE) + 1);


            hql.append("  and t.createDate >= ?  and  t.createDate < ? ");
            param.add(dateFormat.parse(vo.getDate()));
            param.add(now.getTime());
        }


        hql.append("order by t.updateDate desc");

        return getPagination(vo.getPageIndex(), vo.getPageSize(), hql.toString(), param.toArray());
    }

    @Override
    public Object checkTodayUser(String name, String phone,Integer status) {
        StringBuffer hql = new StringBuffer();
        hql.append("select count(1) from  LotteryRecordEO t where t.recordStatus =? and t.phone =? ");
        List<Object> param =new ArrayList<Object>();
        param.add(AMockEntity.RecordStatus.Normal.toString());
        param.add(phone);
        hql.append(" and t.createDate >=? and t.createDate < ?");
        param.add(DateUtil.getToday());
        param.add(new Date(DateUtil.getToday().getTime()+24*3600*1000));
        if(!AppUtil.isEmpty(status)){
            hql.append(" and t.results=? ");
            param.add(status);

        }
        return getObject(hql.toString(),param.toArray());
    }

}
