package cn.lonsun.wechatmgr.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.wechatmgr.internal.entity.WeChatArticleEO;
import cn.lonsun.wechatmgr.vo.WeChatArticleVO;

import java.util.List;

public interface IWeChatArticleDao extends IMockDao<WeChatArticleEO> {

	
	/**
	 * 
	 * @Title: getPage
	 * @Description: 分页列表
	 * @param vo
	 * @return   Parameter
	 * @return  Pagination   return type
	 * @throws
	 */
	Pagination getPage(WeChatArticleVO vo);

	List<WeChatArticleEO> getByKeyWordsMsgId(Long id);
}
