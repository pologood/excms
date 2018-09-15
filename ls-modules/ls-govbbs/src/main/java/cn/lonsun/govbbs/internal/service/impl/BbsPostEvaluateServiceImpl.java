package cn.lonsun.govbbs.internal.service.impl;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.govbbs.internal.dao.IBbsPostEvaluateDao;
import cn.lonsun.govbbs.internal.entity.BbsPostEvaluateEO;
import cn.lonsun.govbbs.internal.service.IBbsPostEvaluateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BbsPostEvaluateServiceImpl extends BaseService<BbsPostEvaluateEO> implements IBbsPostEvaluateService {
	
	@Autowired
	private IBbsPostEvaluateDao bbsPostEvaluateDao;

}
