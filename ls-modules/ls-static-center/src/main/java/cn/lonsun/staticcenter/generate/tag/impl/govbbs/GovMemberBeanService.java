package cn.lonsun.staticcenter.generate.tag.impl.govbbs;

import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.govbbs.internal.dao.IBbsPostDao;
import cn.lonsun.govbbs.internal.entity.BbsSettingEO;
import cn.lonsun.govbbs.internal.vo.BbsMemberVO;
import cn.lonsun.govbbs.util.BbsSettingUtil;
import cn.lonsun.govbbs.util.CounterUtil;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.system.member.internal.dao.IMemberDao;
import cn.lonsun.system.member.internal.entity.MemberEO;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangchao on 2017/1/5.
 */
@Component
public class GovMemberBeanService extends AbstractBeanService {

    @Autowired
    private IMemberDao memberDao;

    @Autowired
    private IBbsPostDao bbsPostDao;


    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();

        //站点id
        Long siteId = paramObj.getLong("siteId");
        if (!(siteId != null && siteId != 0)) {
            siteId = context.getSiteId();
        }
        Map<String,Object> objects = new HashMap<String,Object>();
        HttpSession session = ContextHolderUtils.getSession();
        BbsMemberVO member = (BbsMemberVO) session.getAttribute("bbsUser");
        Integer showHandel = 0;
        if(member == null){
            //设置游客
            BbsSettingEO eo = BbsSettingUtil.getSiteBbsSetting(siteId);
            member = new BbsMemberVO();
            Long touristId = CounterUtil.getTouristId();
            member.setId(touristId);
            member.setMemberType(BbsMemberVO.MemberType.TOURIST.getMemberType());//游客
            member.setName(eo == null?"游客:":eo.getVisitorPrefix()+ touristId);
            session.setAttribute("bbsUser",member);
        }else{
            //如果是部门会员 查询所有的待办数
            if(member.getMemberType() != null && member.getMemberType() == 1){
                showHandel = 1;
                Long total = 0L;
                MemberEO eo = memberDao.getEntity(MemberEO.class,member.getId());
                if(eo != null && eo.getUnitId() != null){
                    total  = getUnitTotal(siteId,eo.getUnitId());
                }
                objects.put("total",total);
            }
        }
        objects.put("member",member);
        objects.put("showHandel",showHandel);
        return objects;
    }

    private Long getUnitTotal(Long siteId,Long unitId) {
        String hql = "from BbsPostEO b where b.siteId = ? and b.recordStatus = 'Normal' and b.isPublish = 1" +
                " and b.isAccept = 0 and b.acceptUnitId = ? ";
        return bbsPostDao.getCount(hql,new Object[]{siteId,unitId});

    }
}
