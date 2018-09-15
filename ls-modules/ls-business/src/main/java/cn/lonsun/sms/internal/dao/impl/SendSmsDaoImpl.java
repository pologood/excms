package cn.lonsun.sms.internal.dao.impl;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.sms.vo.SmsQueryVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.sms.internal.dao.ISendSmsDao;
import cn.lonsun.sms.internal.entity.SendSmsEO;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SendSmsDaoImpl extends BaseDao<SendSmsEO> implements ISendSmsDao{

	@Override
	public SendSmsEO getMaxTimeSms(String phone) {
		String hql = "from SendSmsEO t where t.status = ? and t.phone = ? and t.smsStatus = 1 and "
				+ "not exists(select 1 from SendSmsEO t1 where t1.createDate > t.createDate)";
		return getEntityByHql(hql, new Object[]{SendSmsEO.Status.Unused.toString(),phone});
	}

	@Override
	public Pagination getPage(SmsQueryVO query) {
		// 取参数
		StringBuffer hql = new StringBuffer();
		List<Object> values = new ArrayList<Object>();
		hql.append("from SendSmsEO where 1=1");
		if(!StringUtils.isEmpty(query.getPhone())){
			hql.append(" and phone like ?");
			values.add("%".concat(query.getPhone()).concat("%"));
		}
		hql.append(" order by smsId desc");
		return getPagination(query.getPageIndex(), query.getPageSize(), hql.toString(), values.toArray()) ;
	}
}
