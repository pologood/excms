package cn.lonsun.phrase.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.phrase.internal.entity.PhraseEO;

/**
 * 常用语Dao接口
 *
 * @author xujh
 * @version 1.0
 * 2014年12月3日
 *
 */
public interface IPhraseDao extends IBaseDao<PhraseEO> {
	
	
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
