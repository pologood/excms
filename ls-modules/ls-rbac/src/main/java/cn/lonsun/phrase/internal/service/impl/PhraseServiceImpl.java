package cn.lonsun.phrase.internal.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.phrase.internal.dao.IPhraseDao;
import cn.lonsun.phrase.internal.entity.PhraseEO;
import cn.lonsun.phrase.internal.service.IPhraseService;

/**
 * 常用语接口服务实现类
 *
 * @author xujh
 * @version 1.0
 * 2014年12月3日
 *
 */
@Service("phraseService")
public class PhraseServiceImpl extends BaseService<PhraseEO> implements IPhraseService {
	
	Logger logger = LoggerFactory.getLogger(PhraseServiceImpl.class);
	
	@Autowired
	private IPhraseDao phraseDao;
	
	@Override
	public Long savePhrase(PhraseEO phrase) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("createUserId", phrase.getCreateUserId());
		params.put("createOrganId", phrase.getCreateOrganId());
		params.put("type", phrase.getType());
		params.put("text", phrase.getText());
		PhraseEO p = getEntity(PhraseEO.class, params);
		if(p!=null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "常用语已存在.");
		}
		return saveEntity(phrase);
	}
	
	@Override
	public void updatePhrase(PhraseEO phrase) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("createUserId", phrase.getCreateUserId());
		params.put("createOrganId", phrase.getCreateOrganId());
		params.put("text", phrase.getText());
		PhraseEO p = getEntity(PhraseEO.class, params);
		if(p!=null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "常用语已存在.");
		}
		updateEntity(phrase);
	}

	@Override
	public List<PhraseEO> getPhrases(Long organId,Long userId,String type) {
		return phraseDao.getPhrases(organId,userId,type);
	}

	@Override
	public Pagination getPage(Long index, Integer size,Long organId, Long userId, String text) {
		return phraseDao.getPage(index, size, organId,userId, text);
	}


}
