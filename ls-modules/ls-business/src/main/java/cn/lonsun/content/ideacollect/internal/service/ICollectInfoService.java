package cn.lonsun.content.ideacollect.internal.service;

import cn.lonsun.content.ideacollect.internal.entity.CollectInfoEO;
import cn.lonsun.content.ideacollect.vo.CollectInfoVO;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.vo.QueryVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;

import java.util.List;

public interface ICollectInfoService extends IBaseService<CollectInfoEO>{

	Pagination getPage(QueryVO query);

	void delete(Long[] ids,Long[] contentIds);

	CollectInfoVO getCollectInfoVO(Long infoId);

    CollectInfoEO getCollectInfoByContentId(Long id);
	/**
	 * 彻底删除接口
	 * @param contents
	 */
	void deleteByContentIds(BaseContentEO content);

	List<CollectInfoVO> getCollectInfoVOS(String code);

	BaseContentEO save(CollectInfoVO collectInfoVO);
}
