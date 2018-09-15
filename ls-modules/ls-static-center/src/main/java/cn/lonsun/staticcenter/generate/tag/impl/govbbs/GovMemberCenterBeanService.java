package cn.lonsun.staticcenter.generate.tag.impl.govbbs;

import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.govbbs.internal.vo.BbsMemberVO;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.tag.impl.member.util.HtmlMemberEnum;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.internal.service.IMemberService;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangchao on 2017/1/13.
 */
@Component
public class GovMemberCenterBeanService extends AbstractBeanService {

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
        String goUrl = context.getParamMap().get("goUrl");
        String action = context.getParamMap().get("actionType");
        HttpSession session = ContextHolderUtils.getSession();
        BbsMemberVO member = (BbsMemberVO) session.getAttribute("bbsUser");
        if(member != null && member.getMemberType() != null && member.getMemberType() != 2){
            isLogin = (member != null?1:0);
        }
        action = (isLogin == 1? HtmlMemberEnum.center.getValue():action);
        if (HtmlMemberEnum.setpw.getValue().equals(action)) {}
        else if (HtmlMemberEnum.register.getValue().equals(action)) {}
        else if(isLogin == 1 && HtmlMemberEnum.center.getValue().equals(action)) {
            String paramStr = context.getParamMap().get("param");
            try {
                param = (StringUtils.isEmpty(paramStr) ? 0 : Integer.parseInt(paramStr));
            }catch (Exception e){}
            if(param == 1 || param == 3 | param == 4){
                MemberEO eo = memberService.getEntity(MemberEO.class,member.getId());
                if(member != null){
                    BeanUtils.copyProperties(eo, member);
                }
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
        mapParams.put("member",member);
        //跳转id
        mapParams.put("goUrl",StringUtils.isEmpty(goUrl)?"":goUrl);
        //跳转参数
        return mapParams;
    }
}
