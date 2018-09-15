package cn.lonsun.staticcenter.generate.tag.impl.member;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.tag.impl.member.util.HtmlMemberEnum;
import cn.lonsun.staticcenter.generate.tag.impl.member.util.MemberCenterVO;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.internal.service.IMemberService;
import cn.lonsun.system.member.vo.MemberSessionVO;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

@Component
public class MemberInfoBeanService extends AbstractBeanService {

	@Autowired
	private IMemberService memberService;

	@Override
	public Object getObject(JSONObject paramObj) {
		Map<String, Object> mapParams = new HashMap<String, Object>();
		Context context = ContextHolder.getContext();
		//站点ie
		Long siteId = context.getSiteId();
		//是否登录 0 未登录  //用户中心类型
		Integer isLogin = 0,param = 0;
		String goId = context.getParamMap().get("goId");
		String goType = context.getParamMap().get("goType");
		String action = context.getParamMap().get("actionType");
		MemberSessionVO sessionMember = (MemberSessionVO) ContextHolderUtils.getSession().getAttribute("member");
		isLogin = (sessionMember != null?1:0);
		action = (isLogin == 1? HtmlMemberEnum.center.getValue():action);
		//忘记密码
		if (HtmlMemberEnum.setpw.getValue().equals(action)) {
			String uid = context.getParamMap().get("uid");
			String getQuestion = context.getParamMap().get("getQuestion");
			if(uid !=null && "1".equals(getQuestion)){
				MemberEO member = memberService.getMemberByUid(uid, siteId);
				if (null == member) {
					mapParams.put("memberIsExist",false);
				}else{
					mapParams.put("memberIsExist",true);
					mapParams.put("question",member.getQuestion());
					mapParams.put("uid",uid);
					mapParams.put("memberId",member.getId());
				}
			}
		}
		else if (HtmlMemberEnum.register.getValue().equals(action)) {}
		else if(isLogin == 1 && HtmlMemberEnum.center.getValue().equals(action)) {
			String paramStr = context.getParamMap().get("param");
			try {
				param = (StringUtils.isEmpty(paramStr) ? 0 : Integer.parseInt(paramStr));
			}catch (Exception e){}
			//个人中心各个应用需要处理的逻辑
			switch (param){
				case 1://基本信息
					sessionMember = setMemberVO(sessionMember);
					break;
				case 2://密码修改
					break;
				case 3://安全修改
					sessionMember = setMemberVO(sessionMember);
					break;
				case 4://需要可自行添加
					sessionMember = setMemberVO(sessionMember);
					break;
				default: break;
			}
		}
		//设置站点id
		mapParams.put("siteId",siteId);
		//访问类型
		mapParams.put("action",action);
		//是否登录
		mapParams.put("isLogin",isLogin);
		//设置会员信息
		mapParams.put("param",param);
		//设置会员信息
		mapParams.put("member",sessionMember);
		//跳转id
		mapParams.put("goId",StringUtils.isEmpty(goId)?"":goId);
		//跳转参数
		mapParams.put("goType",StringUtils.isEmpty(goType)?"":goType);
		return mapParams;
	}

	//设置最新会员信息
	private MemberSessionVO setMemberVO(MemberSessionVO sessionMember) {
		MemberEO member = memberService.getEntity(MemberEO.class, sessionMember.getId());
		if(member != null){
			BeanUtils.copyProperties(member, sessionMember);
		}
		return sessionMember;
	}

}
