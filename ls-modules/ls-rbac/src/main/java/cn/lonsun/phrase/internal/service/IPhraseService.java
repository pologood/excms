package cn.lonsun.phrase.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.phrase.internal.entity.PhraseEO;

/**
 * 通用语服务接口
 *
 * @author xujh
 * @version 1.0
 * 2014年12月3日
 *
 */
public interface IPhraseService extends IBaseService<PhraseEO> {
	
	/**
	 * 保存常用语
	 *
	 * @param phrase
	 * @return
	 */
	public Long savePhrase(PhraseEO phrase);
	
	/**
	 * 更新常用语
	 *
	 * @param phrase
	 */
	public void updatePhrase(PhraseEO phrase);
	
	/**
	 * 获取用户的常用语
	 * @param organId
	 * @param userId
	 * @return
	 */
	public List<PhraseEO> getPhrases(Long organId,Long userId,String type);
	
	/**
	 * 获取分页
	 *
	 * @param index
	 * @param size
	 * @param organId
	 * @param userId
	 * @param text
	 * @return
	 */
	public Pagination getPage(Long index,Integer size,Long organId,Long userId,String text);

}
