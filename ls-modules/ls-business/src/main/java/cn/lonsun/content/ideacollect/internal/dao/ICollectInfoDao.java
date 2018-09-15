package cn.lonsun.content.ideacollect.internal.dao;

import cn.lonsun.content.ideacollect.internal.entity.CollectInfoEO;
import cn.lonsun.content.ideacollect.vo.CollectInfoVO;
import cn.lonsun.content.vo.QueryVO;
import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;

import java.util.List;

public interface ICollectInfoDao extends IBaseDao<CollectInfoEO>{

	Pagination getPage(QueryVO query);

	CollectInfoEO getCollectInfoByContentId(Long contentId);

	List<CollectInfoVO> getCollectInfoVOS(String code);
}
