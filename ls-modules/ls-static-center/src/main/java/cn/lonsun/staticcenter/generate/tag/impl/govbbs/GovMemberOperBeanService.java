package cn.lonsun.staticcenter.generate.tag.impl.govbbs;

import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.govbbs.internal.vo.BbsMemberVO;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.system.member.internal.dao.IMemberDao;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作登录 注册操作
 * Created by zhangchao on 2017/1/7.
 */
@Component
public class GovMemberOperBeanService extends AbstractBeanService {

    @Autowired
    private IMemberDao memberDao;

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();
        //站点id
        Long siteId = paramObj.getLong("siteId");
        if (!(siteId != null && siteId != 0)) {
            siteId = context.getSiteId();
        }

        String oper = paramObj.getString("oper");
        if (StringUtils.isEmpty(oper)) {
            oper = context.getParamMap().get("oper");
        }
        Map<String, Object> objects = new HashMap<String, Object>();
        Integer isLogin = 0;
        if(!StringUtils.isEmpty(oper) && oper.equals("loginIndex")){
            HttpSession session = ContextHolderUtils.getSession();
            BbsMemberVO member = (BbsMemberVO) session.getAttribute("bbsUser");
            //如果是部门会员 查询所有的待办数
            if(member.getMemberType() != null && member.getMemberType() != 2){
                isLogin = 1;
                objects.put("member",member);
            }
        }
        objects.put("siteId",siteId);
        objects.put("oper",oper);
        objects.put("isLogin",isLogin);
        return objects;
    }
}
