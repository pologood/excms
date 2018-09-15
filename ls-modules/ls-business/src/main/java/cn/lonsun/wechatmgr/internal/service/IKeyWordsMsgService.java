package cn.lonsun.wechatmgr.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.wechatmgr.internal.entity.KeyWordsMsgEO;
import cn.lonsun.wechatmgr.vo.KeyWordsVO;

public interface IKeyWordsMsgService extends IMockService<KeyWordsMsgEO> {

	/**
	 * 
	 * @Title: getListBySite
	 * @Description: 根据站点ID获取
	 * @param siteId
	 * @return   Parameter
	 * @return  List<KeyWordsMsgEO>   return type
	 * @throws
	 */
	List<KeyWordsMsgEO> getListBySite(Long siteId);
	/**
	 * 
	 * @Title: getPage
	 * @Description: 分页查询
	 * @param vo
	 * @return   Parameter
	 * @return  Pagination   return type
	 * @throws
	 */
	Pagination getPage(KeyWordsVO vo);
	/**
	 * 
	 * @Title: getKeywords
	 * @Description: 
	 * @param id
	 * @return   Parameter
	 * @return  KeyWordsMsgEO   return type
	 * @throws
	 */
	KeyWordsMsgEO getKeywords(Long id);
	/**
	 * 
	 * @Title: saveKeywords
	 * @Description: TODO
	 * @param eo
	 * @param articles   Parameter
	 * @return  void   return type
	 * @throws
	 */
	void saveKeywords(KeyWordsMsgEO eo,Long[] articles);

	/**
	 * 根据关键词查询
	 * @param siteId
	 * @param eventKey
     * @return
     */
	KeyWordsMsgEO getKeyWordsMsg(Long siteId, String eventKey);
}
