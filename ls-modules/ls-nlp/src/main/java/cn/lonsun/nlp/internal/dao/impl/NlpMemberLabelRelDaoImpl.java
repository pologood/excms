package cn.lonsun.nlp.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.nlp.internal.dao.INlpMemberLabelRelDao;
import cn.lonsun.nlp.internal.entity.NlpMemberLabelRelEO;
import cn.lonsun.nlp.internal.vo.NlpKeyWordsVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 会员关注会员标签关系
 * @author: liuk
 * @version: v1.0
 * @date:2018/5/18 10:21
 */
@Repository
public class NlpMemberLabelRelDaoImpl extends MockDao<NlpMemberLabelRelEO> implements INlpMemberLabelRelDao {
    /**
     * 删除会员标签
     * @param memberId
     * @param keyWordId
     */
    @Override
    public void delMemberLabel(Long memberId,Long keyWordId){
        String hql = "update NlpMemberLabelRelEO set recordStatus = ? where memberId = ? and labelId = ? ";
        executeUpdateByHql(hql,new Object[]{AMockEntity.RecordStatus.Removed.toString(),memberId,keyWordId});
    }

    @Override
    public List<NlpKeyWordsVO> getLabelByMemberId(Long memberId) {
        StringBuffer hql = new StringBuffer();
        hql.append("select m.labelId as keyWordId, t.name as keyWordName from NlpMemberLabelRelEO m, NlpKeyWordsEO t where m.labelId = t.id ");
        hql.append(" and m.memberId = ? ");
        hql.append(" and m.recordStatus = ? ");
        hql.append(" order by m.createDate ");
        return (List<NlpKeyWordsVO>)getBeansByHql(hql.toString(),new Object[]{memberId,AMockEntity.RecordStatus.Normal.toString()}, NlpKeyWordsVO.class);
    }
}
