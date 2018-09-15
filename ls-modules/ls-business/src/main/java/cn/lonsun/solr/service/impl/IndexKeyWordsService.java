package cn.lonsun.solr.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.solr.dao.IIndexKeyWordsDao;
import cn.lonsun.solr.entity.IndexKeyWordsEO;
import cn.lonsun.solr.service.IIndexKeyWordsService;
import cn.lonsun.solr.vo.KeyWordsCountVO;
import cn.lonsun.util.LoginPersonUtil;

/**
 * @author gu.fei
 * @version 2016-2-1 10:13
 */
@Service
public class IndexKeyWordsService extends MockService<IndexKeyWordsEO> implements IIndexKeyWordsService {

    @Autowired
    private IIndexKeyWordsDao indexKeyWordsDao;

	@Override
	public List<KeyWordsCountVO> getKeyWordsCount(Long siteId, Date now,
			Integer num) {
		
		if(null==siteId){
			siteId=LoginPersonUtil.getSiteId();
		}
		if(now==null){
			String nt=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			try {
				now=new SimpleDateFormat("yyyy-MM-dd").parse(nt);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if(num==null||num==0){
			num=6;
		}
		return indexKeyWordsDao.getKeyWordsCount(siteId, now, num);
	}
}
