package cn.lonsun.rbac.controller;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.common.util.RegexUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.ThreadUtil;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.rbac.internal.entity.AccessPolicyEO;
import cn.lonsun.rbac.internal.service.IAccessPolicyService;

/**
 * IP安全策略Controller
 * 
 * @Description:
 * @author xujh
 * @date 2014年9月23日 下午10:01:08
 * @version V1.0
 */
@Controller
@RequestMapping(value= "accessPolicy", produces = { "application/json;charset=UTF-8" })
public class AccessPolicyController extends BaseController {
	@Autowired
	private IAccessPolicyService accessPolicyService;

	/**
	 * IP安全策略主页
	 * @return
	 */
	@RequestMapping("policyPage")
	public String policyPage(){
		return "/app/mgr/setting_safe";
	}
	
	/**
	 * IP安全策略主页
	 * @return
	 */
	@RequestMapping("editPage")
	public String editPage(){
		return "/app/mgr/setting_safe_edit";
	}
	
	/**
	 * 获取
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getAccessPolicy")
	public Object getAccessPolicy(Long accessPolicyId) {
		AccessPolicyEO ap = null;
		if (accessPolicyId == null || accessPolicyId <= 0) {
			ap = new AccessPolicyEO();
		} else {
			ap = accessPolicyService.getEntity(AccessPolicyEO.class,
					accessPolicyId);
		}
		return getObject(ap);
	}

	/**
	 * 删除IP访问策略
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("delete")
	public Object delete(Long accessPolicyId) {
		accessPolicyService.delete(AccessPolicyEO.class, accessPolicyId);
		return getObject();
	}

	/**
	 * 保存
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("save")
	public Object save(AccessPolicyEO policy) {
		// 1.ip段验证
		String startIp = policy.getStartIp();
		String endIp = policy.getEndIp();
		if (StringUtils.isEmpty(startIp) || StringUtils.isEmpty(endIp)) {
			throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入IP");
		} else {
			if (!RegexUtil.isIp(startIp) || !RegexUtil.isIp(endIp)) {
				throw new BaseRunTimeException(TipsMode.Message.toString(),"请输入正确的IP");
			}
			String[] arr1 = startIp.split("\\.");
			String[] arr2 = endIp.split("\\.");
			//同一个段上的ip验证
			if(!arr1[0].equals(arr2[0])||!arr1[1].equals(arr2[1])||!arr1[2].equals(arr2[2])){
				throw new BaseRunTimeException(TipsMode.Message.toString(),"请输入同一网段的IP");
			}
			if(Integer.valueOf(arr1[3])>Integer.valueOf(arr2[3])){
				throw new BaseRunTimeException(TipsMode.Message.toString(),"开始IP与结束IP顺序错误");
			}
		}
		// 2.时间段验证
		Date startDate = policy.getStartDate();
		Date endDate = policy.getEndDate();
		Date now = new Date();
		if(startDate!=null){
			if (now.getTime() > startDate.getTime()) {
				throw new BaseRunTimeException(TipsMode.Message.toString(),"开始时间不能晚于当前时间");
			}
		}
		if(endDate!=null){
			if (now.getTime() > endDate.getTime()) {
				throw new BaseRunTimeException(TipsMode.Message.toString(),"结束时间不能晚于当前时间");
			}
			if (startDate!=null&&startDate.getTime() >= endDate.getTime()) {
				throw new BaseRunTimeException(TipsMode.Message.toString(),"结束时间不能晚于开始时间");
			}
		}
		policy.setCreatePersonName(ThreadUtil.getString(ThreadUtil.LocalParamsKey.PersonName));
		accessPolicyService.saveEntity(policy);
		return getObject();
	}

	/**
	 * 更新
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("update")
	public Object update(AccessPolicyEO policy) {
		// 1.ip段验证
		String startIp = policy.getStartIp();
		String endIp = policy.getEndIp();
		if (StringUtils.isEmpty(startIp) || StringUtils.isEmpty(endIp)) {
			throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入IP");
		} else {
			if (!RegexUtil.isIp(startIp) || !RegexUtil.isIp(endIp)) {
				throw new BaseRunTimeException(TipsMode.Message.toString(),
						"请输入正确的IP");
			}
		}
		// 2.时间段验证
		Date startDate = policy.getStartDate();
		Date endDate = policy.getEndDate();
		Date now = new Date();
		if(startDate!=null){
			if (now.getTime() > startDate.getTime()) {
				throw new BaseRunTimeException(TipsMode.Message.toString(),"开始时间不能晚于当前时间");
			}
		}
		if(endDate!=null){
			if (now.getTime() > endDate.getTime()) {
				throw new BaseRunTimeException(TipsMode.Message.toString(),"结束时间不能晚于当前时间");
			}
			if (startDate!=null&&startDate.getTime() >= endDate.getTime()) {
				throw new BaseRunTimeException(TipsMode.Message.toString(),"结束时间不能晚于开始时间");
			}
		}
		accessPolicyService.updateEntity(policy);
		return getObject();
	}

	@ResponseBody
	@RequestMapping("updateIsEnable")
	public Object updateIsEnable(
			@RequestParam("accessPolicyIds") Long[] accessPolicyIds,
			boolean isEnable) {
		List<AccessPolicyEO> policies = accessPolicyService.getEntities(
				AccessPolicyEO.class, accessPolicyIds);
		if (policies != null && policies.size() > 0) {
			for (AccessPolicyEO policy : policies) {
				if (policy.getIsEnable() != isEnable) {
					policy.setIsEnable(isEnable);
					accessPolicyService.updateEntity(policy);
				}
			}
		}
		return getObject();
	}

	/**
	 * 获取分页
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getPage")
	public Object getPage(Long index, Integer size) {
		if (index == null || index < 0) {
			index = 0L;
		}
		if (size == null || size <= 0) {
			size = 15;
		}
		return getObject(accessPolicyService.getPage(index, size));
	}

}
