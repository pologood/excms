package cn.lonsun.staticcenter.generate.tag.impl.govbbs;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.govbbs.internal.entity.BbsMemberRoleEO;
import cn.lonsun.govbbs.internal.entity.BbsPlateEO;
import cn.lonsun.govbbs.internal.entity.BbsPostEO;
import cn.lonsun.govbbs.internal.service.IBbsPostService;
import cn.lonsun.govbbs.internal.vo.BbsMemberVO;
import cn.lonsun.govbbs.internal.vo.PermissionVO;
import cn.lonsun.govbbs.util.MemberRoleUtil;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.rbac.internal.vo.OrganVO;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangchao on 2017/1/7.
 */
@Component
public class GovBbsPostReplyBeanService  extends AbstractBeanService {

    @Autowired
    private IBbsPostService bbsPostService;

    @Autowired
    private IOrganService organService;

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();
        Map<String, Object> objects = new HashMap<String, Object>();
        //站点id
        Long siteId = paramObj.getLong("siteId");
        if (!(siteId != null && siteId != 0)) {
            siteId = context.getSiteId();
        }
        Long plateId = paramObj.getLong("plateId");
        if (!(plateId != null && plateId != 0)) {
            String plateIdCh = context.getParamMap().get("plateId");
            plateId = StringUtils.isEmpty(plateIdCh)?null:Long.parseLong(plateIdCh);
        }
        Long postId = paramObj.getLong("postId");
        if (!(postId != null && postId != 0)) {
            String postIdCh = context.getParamMap().get("postId");
            postId = StringUtils.isEmpty(postIdCh)?null:Long.parseLong(postIdCh);
        }
        //当前登录人信息
        HttpSession session = ContextHolderUtils.getSession();
        BbsMemberVO member = (BbsMemberVO) session.getAttribute("bbsUser");

        String action = null;
        Integer isShow = 0;
        BbsPlateEO plate = null;
        if(plateId != null && plateId != 0){
            action = "post";
            plate = CacheHandler.getEntity(BbsPlateEO.class,plateId);

            String handelPlateIds = paramObj.getString("handelPlateIds");
            if(!StringUtils.isEmpty(handelPlateIds) && handelPlateIds.indexOf(plateId+"") > -1){
                List<OrganVO> organs = organService.getOrgansBySiteId(siteId,true);
                objects.put("organs",organs);
                isShow = 1;
            }
        }else if(postId != null && postId != 0 ){
            action = "reply";
            BbsPostEO post = bbsPostService.getEntity(BbsPostEO.class,postId);
            objects.put("post",post);
            if(post != null){
                plate = CacheHandler.getEntity(BbsPlateEO.class,post.getPlateId());
            }
        }
        PermissionVO permission = getPermissionVO(member,plate);
        if(isShow ==1){
            permission.setIsShowUnits(1);
        }
        objects.put("action",action);
        objects.put("permission", permission);
        objects.put("siteId",siteId);
        objects.put("plate", plate);
        return objects;
    }

    /**
     * 权限处理
     * @param plate   版块
     * @param sett    设置
     * @param member  会员
     * @return
     */
    private static PermissionVO getPermissionVO(BbsMemberVO member, BbsPlateEO plate) {
        PermissionVO permission = new PermissionVO();
        if(plate != null && plate.getCanUpload() == 1){
            permission.setAddFile(1);
        }
        if(member != null){
            if (member.getMemberType() == 2) { //判断游客类型
                permission.setAddFile(0);
            }else if(member.getMemberType() == 0){//会员
                if(member.getMemberRoleId() != null){
                    BbsMemberRoleEO mrole = MemberRoleUtil.getMemberRole(member.getMemberRoleId());
                    if(mrole != null){
                        if(permission.getAddFile() == 1){
                            permission.setAddFile(mrole.getCanUpload() == null?0:mrole.getCanUpload());
                        }
                    }
                }
            }
        }
        return permission;
    }


}
