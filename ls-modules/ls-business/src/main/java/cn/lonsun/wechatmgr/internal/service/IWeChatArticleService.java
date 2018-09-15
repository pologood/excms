package cn.lonsun.wechatmgr.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.wechatmgr.internal.entity.WeChatArticleEO;
import cn.lonsun.wechatmgr.vo.WeChatArticleVO;

import java.util.List;

public interface IWeChatArticleService extends IMockService<WeChatArticleEO> {

	/**
	 * 
	 * @Title: getPage
	 * @Description: 新闻分页
	 * @param vo
	 * @return   Parameter
	 * @return  Pagination   return type
	 * @throws
	 */
	Pagination getPage(WeChatArticleVO vo);
	
	/**
	 * 
	 * @Title: saveArticle
	 * @Description: 保存新闻
	 * @param artEO   Parameter
	 * @return  void   return type
	 * @throws
	 */
	void saveArticle(WeChatArticleEO artEO);

	/**
	 * 查询关键词绑定的所有素材
	 * @param id
	 * @return
     */
	List<WeChatArticleEO> getByKeyWordsMsgId(Long id);
}
