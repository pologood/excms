package cn.lonsun.solr.service;

import java.util.Date;
import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.solr.entity.IndexKeyWordsEO;
import cn.lonsun.solr.vo.KeyWordsCountVO;

/**
 * @author gu.fei
 * @version 2016-2-1 10:12
 */
public interface IIndexKeyWordsService extends IMockService<IndexKeyWordsEO> {
	
	List<KeyWordsCountVO> getKeyWordsCount(Long siteId,Date now,Integer num);
}
