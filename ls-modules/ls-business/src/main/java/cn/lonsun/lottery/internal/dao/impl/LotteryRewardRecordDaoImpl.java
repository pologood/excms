package cn.lonsun.lottery.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.lottery.internal.dao.ILotteryRewardRecordDao;
import cn.lonsun.lottery.internal.entity.LotteryRewardEO;
import cn.lonsun.lottery.internal.entity.LotteryRewardRecordEO;
import cn.lonsun.lottery.internal.vo.QuestionsQueryVO;
import cn.lonsun.util.DateUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by lonsun on 2017-1-11.
 */
@Repository
public class LotteryRewardRecordDaoImpl extends MockDao<LotteryRewardRecordEO> implements ILotteryRewardRecordDao {
    @Override
    public Object getCountRecordByReward(LotteryRewardEO lotteryRewardEO) {
        StringBuffer hql = new StringBuffer();
        hql.append("select count(1) from LotteryRewardRecordEO t where   t.recordStatus =? and t.rewardId=?");
        List<Object> param =new ArrayList<Object>();
        param.add(AMockEntity.RecordStatus.Normal.toString());
        param.add(lotteryRewardEO.getRewardId());
        return getObject(hql.toString(),param.toArray());

    }

    @Override
    public Object checkTodayUser(String name, String phone) {
        StringBuffer hql = new StringBuffer();
        hql.append("select count(1) from  LotteryRewardRecordEO t where t.recordStatus =? and t.phone =? ");
        List<Object> param =new ArrayList<Object>();
        param.add(AMockEntity.RecordStatus.Normal.toString());
        param.add(phone);
        hql.append(" and t.createDate >=? and t.createDate < ?");
        param.add(DateUtil.getToday());
        param.add(new Date(DateUtil.getToday().getTime()+24*3600*1000));

        return getObject(hql.toString(), param.toArray());
    }

    @Override
    public Pagination getRewardRecord(QuestionsQueryVO vo) throws ParseException {
        StringBuffer hql = new StringBuffer();
        List<Object> param =new ArrayList<Object>();
        hql.append(" from LotteryRewardRecordEO t where   t.recordStatus =?");
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

        return getPagination(vo.getPageIndex(),vo.getPageSize(),hql.toString(),param.toArray());
    }

    @Override
    public Object getSumToday() {
        StringBuffer hql = new StringBuffer();
        hql.append("select sum(t.rewardPrice) from  LotteryRewardRecordEO t where t.recordStatus =? and t.rewardId is not  null ");
        List<Object> param =new ArrayList<Object>();
        param.add(AMockEntity.RecordStatus.Normal.toString());
        hql.append(" and t.createDate >=? and t.createDate < ?");
        param.add(DateUtil.getToday());
        param.add(new Date(DateUtil.getToday().getTime()+24*3600*1000));

        return getObject(hql.toString(),param.toArray());
    }
}
