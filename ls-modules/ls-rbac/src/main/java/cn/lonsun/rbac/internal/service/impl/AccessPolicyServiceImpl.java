package cn.lonsun.rbac.internal.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.dao.IAccessPolicyDao;
import cn.lonsun.rbac.internal.entity.AccessPolicyEO;
import cn.lonsun.rbac.internal.service.IAccessPolicyService;

@Service("accessPolicyService")
public class AccessPolicyServiceImpl extends BaseService<AccessPolicyEO> implements IAccessPolicyService {
	
	@Autowired
	private IAccessPolicyDao accessPolicyDao;
	
	@Override
	public boolean isIpAccessable(String ip){
		boolean isAccessable = false;
		String[] arr = ip.split("\\.");
		String blurryIp = arr[0].concat(".").concat(arr[1]).concat(".").concat(arr[2]);
		int fourthPart = Integer.valueOf(arr[3]);
		List<AccessPolicyEO> policies = accessPolicyDao.getPolicys(blurryIp, null);
		if(policies!=null&&policies.size()>0){
			for(AccessPolicyEO policy:policies){
				//时效性验证
				Date startDate = policy.getStartDate();
				Date endDate = policy.getEndDate();
				Date now = new Date();
				//规则尚未生效，忽略
				if(startDate!=null&&now.getTime()<startDate.getTime()){
					continue;
				}
				//规则已失效，忽略
				if(endDate!=null&&now.getTime()>endDate.getTime()){
					continue;
				}
				String startIp = policy.getStartIp();
				String endIp = policy.getEndIp();
				if(StringUtils.isEmpty(startIp)||StringUtils.isEmpty(endIp)){
					continue;
				}
				int start = Integer.valueOf(startIp.split("\\.")[3]);
				int end = Integer.valueOf(endIp.split("\\.")[3]);
				if(fourthPart>start&&fourthPart<end){
					if(policy.getIsEnable()){
						isAccessable = true;
					}else{
						//只要有一条禁止，那么就禁止访问
						isAccessable = false;
						break;
					}
				}
			}
		}
		return isAccessable;
	}

	@Override
	public List<AccessPolicyEO> getPolicys() {
		return accessPolicyDao.getPolicys();
	}

	@Override
	public Pagination getPage(Long index, Integer size) {
		return accessPolicyDao.getPage(index, size);
	}

	@Override
	public List<AccessPolicyEO> getPolicys(boolean isEnable) {
		return accessPolicyDao.getPolicys(isEnable);
	}

}
