package cn.lonsun.wechatmgr.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.wechatmgr.internal.entity.KeyWordsMsgEO;
import cn.lonsun.wechatmgr.vo.KeyWordsVO;

public interface IKeyWordsMsgDao extends IMockDao<KeyWordsMsgEO> {

	/**
	 * 
	 * @Title: getListBySite
	 * @Description: 根据站点获取关键词集合
	 * @param siteId
	 * @return   Parameter
	 * @return  List<KeyWordsMsgEO>   return type
	 * @throws
	 */
	List<KeyWordsMsgEO> getListBySite(Long siteId);
	
	/**
	 * 
	 * @Title: getPage
	 * @Description: 条件分页
	 * @param vo
	 * @return   Parameter
	 * @return  Pagination   return type
	 * @throws
	 */
	Pagination getPage(KeyWordsVO vo);

	List<KeyWordsMsgEO> getgetKeyWordsMsg(Long siteId, String eventKey);
}
