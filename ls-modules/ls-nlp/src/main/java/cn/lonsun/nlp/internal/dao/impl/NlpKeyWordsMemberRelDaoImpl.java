package cn.lonsun.nlp.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.nlp.internal.dao.INlpKeyWordsMemberRelDao;
import cn.lonsun.nlp.internal.entity.NlpKeyWordsMemberRelEO;
import cn.lonsun.nlp.internal.vo.NlpKeyWordsVO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 会员浏览关键词相关文章记录
 * @author: liuk
 * @version: v1.0
 * @date:2018/5/18 10:21
 */
@Repository
public class NlpKeyWordsMemberRelDaoImpl extends BaseDao<NlpKeyWordsMemberRelEO> implements INlpKeyWordsMemberRelDao {

    @Override
    public List<NlpKeyWordsVO> getMemberKeywords(Long memberId,Long siteId,Integer num) {
        StringBuffer hql = new StringBuffer();
        List<Object> values = new ArrayList<Object>();
        hql.append("select m.keyWordId as keyWordId, t.name as keyWordName,count(m.keyWordId) as readCounts ");
        hql.append(" from NlpKeyWordsMemberRelEO m, NlpKeyWordsEO t where m.keyWordId = t.id ");
        hql.append(" and m.siteId = ? ");
        values.add(siteId);
        if(!AppUtil.isEmpty(memberId)){
            hql.append(" and m.memberId = ? ");
            values.add(memberId);
        }
        hql.append(" group by m.keyWordId,t.name ");
        hql.append(" order by count(m.keyWordId) desc, m.keyWordId desc ");
        return (List<NlpKeyWordsVO>)getBeansByHql(hql.toString(),values.toArray(), NlpKeyWordsVO.class,num);
    }
}
