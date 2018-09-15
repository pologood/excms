package cn.lonsun.solr.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.solr.dao.IIndexKeyWordsDao;
import cn.lonsun.solr.entity.IndexKeyWordsEO;
import cn.lonsun.solr.vo.KeyWordsCountVO;

/**
 * @author gu.fei
 * @version 2016-2-1 10:12
 */
@Repository
public class IndexKeyWordsDao extends MockDao<IndexKeyWordsEO> implements IIndexKeyWordsDao {

	@Override
	public List<KeyWordsCountVO> getKeyWordsCount(Long siteId, Date now,Integer num) {
		String hql="select t.keyWords as keyWord, count(t.id) as counts from IndexKeyWordsEO t where t.recordStatus='Normal' "
				+ " and t.siteId=? group by t.keyWords order by counts desc";
		return (List<KeyWordsCountVO>) getEntities(hql.toString(), new Object[]{siteId}, KeyWordsCountVO.class, num);
	}
	
}
