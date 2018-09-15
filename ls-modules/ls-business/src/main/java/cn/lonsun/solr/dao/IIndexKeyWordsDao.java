package cn.lonsun.solr.dao;

import java.util.Date;
import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.solr.entity.IndexKeyWordsEO;
import cn.lonsun.solr.vo.KeyWordsCountVO;

/**
 * @author gu.fei
 * @version 2016-2-1 10:11
 */
public interface IIndexKeyWordsDao extends IMockDao<IndexKeyWordsEO> {
	
	List<KeyWordsCountVO> getKeyWordsCount(Long siteId,Date now,Integer num);
}
