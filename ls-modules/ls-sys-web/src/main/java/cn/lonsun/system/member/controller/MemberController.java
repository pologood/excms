package cn.lonsun.system.member.controller;


import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.RequestUtil;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.govbbs.internal.entity.BbsMemberRoleEO;
import cn.lonsun.govbbs.internal.service.IBbsMemberRoleService;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.internal.service.IMemberService;
import cn.lonsun.system.member.vo.MemberQueryVO;
import cn.lonsun.util.FileUploadUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 会员管理控制器
 * @author zhangchao
 *
 */
@Controller
@RequestMapping(value = "/member", produces = { "application/json;charset=UTF-8" })
public class MemberController extends BaseController {


	@Autowired
	private IMemberService memberService;
	@Autowired
	private IBbsMemberRoleService bbsMemberRoleService;


	@RequestMapping("list")
	public String list() {
		return "/system/member/list";
	}


	@RequestMapping("edit")
	public String edit(Long id,Model m) {
		List<BbsMemberRoleEO> list = bbsMemberRoleService.getBbsMemberRoleMap(LoginPersonUtil.getSiteId());
		m.addAttribute("memberId", id);
		m.addAttribute("roleList", list);
		return "/system/member/edit";
	}

	@RequestMapping("getPage")
	@ResponseBody
	public Object getPage(MemberQueryVO query){

		// 页码与查询最多查询数据量纠正
		if (query.getPageIndex()==null||query.getPageIndex() < 0) {
			query.setPageIndex(0L);
		}
		Integer size = query.getPageSize();
		if (size==null||size <= 0 || size > Pagination.MAX_SIZE) {
			query.setPageSize(15);
		}
		Pagination page = memberService.getPage(query);
		return getObject(page);
	}

	/**
	 * 获取所有网站会员
	 * @param type
	 * @return
	 */
	@RequestMapping("getMembers")
	@ResponseBody
	public Object getMember(Integer type){
		Long siteId = LoginPersonUtil.getSiteId();
		if (siteId == null) {
			throw new BaseRunTimeException(TipsMode.Message.toString(), "站点id不能为空");
		}
		return getObject(memberService.getMembers(type,siteId));
	}

	@RequestMapping("save")
	@ResponseBody
	public Object save(MemberEO member, HttpServletRequest request){
		//		member.setIp(RequestUtil.getIpAddr(request));
		check(member);
		String srcImg = "";
		if(member.getId() != null){
			String npw = member.getPassword();
			MemberEO srcMember = memberService.getEntity(MemberEO.class,member.getId());
			srcImg = srcMember.getImg();
			srcMember.setName(member.getName());
			srcMember.setStatus(member.getStatus());
			srcMember.setMemberType(member.getMemberType());
			srcMember.setUnitId(member.getUnitId());
			srcMember.setUnitName(member.getUnitName());
			srcMember.setMemberRoleId(member.getMemberRoleId());
			srcMember.setMemberPoints(member.getMemberPoints());
			srcMember.setImg(member.getImg());
			srcMember.setSex(member.getSex());
			srcMember.setEmail(member.getEmail());
			srcMember.setPhone(member.getPhone());
			srcMember.setIdCard(member.getIdCard());
			srcMember.setAddress(member.getAddress());
			srcMember.setQuestion(member.getQuestion());
			srcMember.setAnswer(member.getAnswer());
			if(!StringUtils.isEmpty(member.getPlainpw())){
				srcMember.setPassword(DigestUtils.md5Hex(member.getPlainpw()));
				srcMember.setPlatCode(null);
				srcMember.setRandKey(null);
			}
			memberService.updateEntity(srcMember);
		}else{
			if(!StringUtils.isEmpty(member.getPlainpw())){
				member.setPassword(DigestUtils.md5Hex(member.getPlainpw()));
			}
			memberService.saveEntity(member);
		}
		//图片更新时，才更新状态
		if(!StringUtils.isEmpty(member.getImg()) && !srcImg.equals(member.getImg())){
			FileUploadUtil.saveFileCenterEO(member.getImg());
		}
		return getObject();
	}

	@RequestMapping("updateImg")
	@ResponseBody
	public Object updateImg(Long memberId,String img){
		//		member.setIp(RequestUtil.getIpAddr(request));
		if(memberId != null){
			MemberEO member = memberService.getEntity(MemberEO.class,memberId);
			if(member != null){
				member.setImg(img);
				memberService.updateEntity(member);
			}
		}
		return getObject();
	}

	private void check(MemberEO member) {
		Boolean hasUid = memberService.isExistUid(member.getUid(),member.getSiteId(),member.getId());
		if(hasUid){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "账号已经存在，请重新输入~");
		}
		Boolean hasPhone = memberService.isExistPhone(member.getPhone(),member.getSiteId(),member.getId());
		if(hasPhone){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "手机号码已经存在，请重新输入~");
		}
	}


	@RequestMapping("getMember")
	@ResponseBody
	public Object getMember(Long id){
		MemberEO member = null;
		if(id == null){
			member = new MemberEO();
		}else{
			member = memberService.getEntity(MemberEO.class,id);
		}
		return getObject(member);
	}

	@RequestMapping("updateStatus")
	@ResponseBody
	public Object updateStatus(@RequestParam("ids") Long[] ids,
							   Integer status) {

		return getObject(memberService.updateStatus(ids, status));
	}

	@RequestMapping("delete")
	@ResponseBody
	public Object delete(@RequestParam("ids") Long[] ids) {
		try{
			memberService.delete(MemberEO.class, ids);
		}catch(Exception e){
			throw new BaseRunTimeException();
		}
		return getObject();
	}


	/**
	 * 登录
	 *
	 * @param uid
	 * @param password
	 * @return
	 * @throws Exception
	 */
	/**
	 * @param request
	 * @param uid
	 * @param password
	 * @param code
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "memberLogin", method = RequestMethod.POST)
	@ResponseBody
	public Object memberLogin(HttpServletRequest request, String uid,String password, String code) throws Exception {
		String ip = RequestUtil.getIpAddr(request);
		try {
			// 帐号为空
			if (StringUtils.isEmpty(uid)) {
				throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入帐号");
			}
			// 密码为空
			if (StringUtils.isEmpty(password)) {
				throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入密码");
			}
			//			if (!StringUtils.isEmpty(code)) {
			//				code = code.toLowerCase();
			//				Object obj = request.getSession().getAttribute("code");
			//				if(obj==null){
			//					throw new BaseRunTimeException(TipsMode.Message.toString(),"验证码已失效");
			//				}
			//				String targetCode = obj.toString().toLowerCase();
			//				if (StringUtils.isEmpty(targetCode) || !code.equals(targetCode)) {
			//					throw new BaseRunTimeException(TipsMode.Message.toString(),"请输入正确的验证码");
			//				}
			//			} else {
			//				throw new BaseRunTimeException(TipsMode.Message.toString(),"请输入验证码");
			//			}
			HttpSession session = request.getSession(true);
			//MD5加密
			String md5Pwd = DigestUtils.md5Hex(password);
			//验证账号是否被禁用
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("uid", uid);
			params.put("password", md5Pwd);
			params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
			MemberEO member = memberService.getEntity(MemberEO.class, params);
			if(member==null){
				throw new BaseRunTimeException(TipsMode.Message.toString(),"用户或密码错误");
			}else{
				Integer status = member.getStatus();
				if(status == null || MemberEO.Status.Unable.getStatus().equals(status)){
					throw new BaseRunTimeException(TipsMode.Message.toString(),"您的账号已被锁定，请联系管理员");
				}
				//无系统管理角色，无权限访问
				session.setAttribute("memberId", member.getId());
				session.setAttribute("memberName", member.getName());
				session.setAttribute("memberPhone", member.getPhone());
				session.setAttribute("memberEmail", member.getEmail());
				session.setAttribute("memberSex", member.getSex());

				//设置最后登录ip 和 最后登录时间
				member.setIp(ip);
				member.setLastLoginDate(new Date());
				memberService.updateEntity(member);
			}

		} catch (Exception e) {
			String message = null;
			if (e instanceof BaseRunTimeException) {
				BaseRunTimeException exception = (BaseRunTimeException) e;
				message = exception.getTipsMessage();
			}else{
				message = "系统异常";
			}
			throw e;
		}finally{
		}

		return getObject();
	}


}
