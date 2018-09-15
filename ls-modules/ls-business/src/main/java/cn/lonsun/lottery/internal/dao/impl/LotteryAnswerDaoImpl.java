package cn.lonsun.lottery.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.lottery.internal.dao.ILotteryAnswerDao;
import cn.lonsun.lottery.internal.entity.LotteryAnswerEO;
import cn.lonsun.lottery.internal.vo.QuestionsQueryVO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lonsun on 2017-1-11.
 */
@Repository
public class LotteryAnswerDaoImpl extends MockDao<LotteryAnswerEO> implements ILotteryAnswerDao {
    @Override
    public Pagination getAnswers(QuestionsQueryVO vo) {
        StringBuffer hql = new StringBuffer();
        hql.append("from  LotteryAnswerEO t where t.siteId=? and t.recordStatus =? ");
        List<Object> param =new ArrayList<Object>();
        param.add(LoginPersonUtil.getSiteId());
        param.add(AMockEntity.RecordStatus.Normal.toString());
        if(!AppUtil.isEmpty(vo.getSearchKey())){
            hql.append("  and t.lotteryTitle like ?");
            param.add("%"+ SqlUtil.prepareParam4Query(vo.getSearchKey())+"%");
        }
        if(!AppUtil.isEmpty(vo.getAnswerKey())){
            hql.append("  and t.answer like ?");
            param.add("%"+ SqlUtil.prepareParam4Query(vo.getAnswerKey())+"%");
        }


        hql.append("order by t.updateDate desc");

        return getPagination(vo.getPageIndex(), vo.getPageSize(), hql.toString(), param.toArray());
    }

    @Override
    public List<LotteryAnswerEO> getAnswersByQuestionId(LotteryAnswerEO lotteryAnswerEO) {
        StringBuffer hql = new StringBuffer();
        List<Object> param =new ArrayList<Object>();
        hql.append("from  LotteryAnswerEO t where t.siteId=? and t.recordStatus =? and t.lotteryId=?");
        param.add(LoginPersonUtil.getSiteId());
        param.add(AMockEntity.RecordStatus.Normal.toString());
        param.add(lotteryAnswerEO.getLotteryId());
        if(!AppUtil.isEmpty(lotteryAnswerEO.getAnswerId())){
            hql.append(" and t.answerId!=?");
            param.add(lotteryAnswerEO.getAnswerId());


        }
       
        return getEntitiesByHql(hql.toString(),param.toArray());
    }

    @Override
    public List<LotteryAnswerEO> checkOption(LotteryAnswerEO lotteryAnswerEO) {
        StringBuffer hql = new StringBuffer();
        List<Object> param =new ArrayList<Object>();
        hql.append("from  LotteryAnswerEO t where t.siteId=? and t.recordStatus =? and t.lotteryId=? and t.answerOption=?");

        param.add(LoginPersonUtil.getSiteId());
        param.add(AMockEntity.RecordStatus.Normal.toString());
        param.add(lotteryAnswerEO.getLotteryId());
        param.add(lotteryAnswerEO.getAnswerOption());
        if(!AppUtil.isEmpty(lotteryAnswerEO.getAnswerId())){
            hql.append(" and t.answerId !=?");
            param.add(lotteryAnswerEO.getAnswerId());
        }

        return getEntitiesByHql(hql.toString(),param.toArray());


    }

    @Override
    public List<LotteryAnswerEO> getAnsersByQuestion(Long lotteryId) {
        StringBuffer hql = new StringBuffer();
        List<Object> param =new ArrayList<Object>();
        hql.append("from  LotteryAnswerEO t where  t.recordStatus =? and t.lotteryId=? ");


        param.add(AMockEntity.RecordStatus.Normal.toString());
        param.add(lotteryId);
        hql.append(" order by t.answerOption asc ");

        return getEntitiesByHql(hql.toString(),param.toArray());

    }
}
