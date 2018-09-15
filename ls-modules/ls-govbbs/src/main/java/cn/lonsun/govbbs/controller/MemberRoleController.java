package cn.lonsun.govbbs.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.govbbs.internal.entity.BbsMemberRoleEO;
import cn.lonsun.govbbs.internal.service.IBbsMemberRoleService;
import cn.lonsun.govbbs.util.BbsMemberRoleUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *会员角色组
 */
@Controller
@RequestMapping(value = "memberRole", produces = { "application/json;charset=UTF-8" })
public class MemberRoleController extends BaseController {

    @Autowired
    private IBbsMemberRoleService memberRoleService;

    @RequestMapping("list")
    public String list(Model m) {
        m.addAttribute("siteId", LoginPersonUtil.getSiteId());
        return "/bbs/member_role";
    }

    @RequestMapping(value ="getBbsMemberRoleList")
    @ResponseBody
    public Object getBbsMemberRoleList(){
        Long siteId = LoginPersonUtil.getSiteId();
        if(siteId == null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "siteId不能为空");
        }
        List<BbsMemberRoleEO> memberRoleEOs = memberRoleService.BbsMemberRoleList(LoginPersonUtil.getSiteId());
        return getObject(memberRoleEOs);
    }

    @RequestMapping(value ="getBbsMemberPoints")
    @ResponseBody
    public Object getBbsMemberPoints(Integer memberPoints){
        Long siteId = LoginPersonUtil.getSiteId();
        BbsMemberRoleEO memberRoleEO = memberRoleService.getMemberRoleByPoints(memberPoints,siteId);
        return getObject(memberRoleEO);
    }


    @RequestMapping("memberRoleInfo")
    public String memberRoleInfo(Long id,Model m) {
        m.addAttribute("memberRoleId", id);
        return "/bbs/member_role_edit";
    }

    @RequestMapping("add")
    public String add() {
        return "/bbs/member_role_edit";
    }

    @RequestMapping("getBbsMemberRole")
    @ResponseBody
    public Object getBbsMemberRole(Long siteId,Long id){
        BbsMemberRoleEO memberRoleEO = memberRoleService.getEntity(BbsMemberRoleEO.class,id);
        if(memberRoleEO == null){
            memberRoleEO = new BbsMemberRoleEO();
            memberRoleEO.setSiteId(siteId);
        }
        return getObject(memberRoleEO);
    }

    @RequestMapping("save")
    @ResponseBody
    public Object save(BbsMemberRoleEO memberRoleEO){
        if(memberRoleEO.getSiteId() == null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "站点id不能为空");
        }
        if(memberRoleEO.getRiches()!=null){
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("riches", memberRoleEO.getRiches());
            map.put("recordStatus", BbsMemberRoleEO.RecordStatus.Normal.toString());
            BbsMemberRoleEO bbsMemberRoleEO = memberRoleService.getEntity(BbsMemberRoleEO.class,map);
            if(bbsMemberRoleEO!=null&&!bbsMemberRoleEO.getId().equals(memberRoleEO.getId())){
                throw new BaseRunTimeException(TipsMode.Message.toString(), "该积分已存在,请重新填写!");
            }
        }
        if(memberRoleEO.getId() != null){
            BbsMemberRoleEO memberRoleEO1 = memberRoleService.getEntity(BbsMemberRoleEO.class,memberRoleEO.getId());
            AppUtil.copyProperties(memberRoleEO1,memberRoleEO);
            memberRoleService.updateEntity(memberRoleEO1);
        }else{
            memberRoleService.saveEntity(memberRoleEO);
        }
        //放进m缓存
        CacheHandler.saveOrUpdate(BbsMemberRoleEO.class,memberRoleEO);
        putMemberRole();
        return getObject();
    }

    @RequestMapping("delete")
    @ResponseBody
    public Object delete(Long id){
        if(id == null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择您要删除的数据!");
        }
        BbsMemberRoleEO memberRoleEO = memberRoleService.getEntity(BbsMemberRoleEO.class,id);
        if(memberRoleEO !=null){
            memberRoleService.delete(memberRoleEO);
            putMemberRole();
        }
        //删除缓存
        CacheHandler.delete(BbsMemberRoleEO.class,memberRoleEO);
        return getObject();
    }

    // 批量删除
    @RequestMapping("batchDelete")
    @ResponseBody
    public Object batchDelete(@RequestParam("ids[]") Long[] ids) {

        List<BbsMemberRoleEO> mrs = memberRoleService.getEntities(BbsMemberRoleEO.class,ids);
        // 批量删除主表（假删）
        memberRoleService.delete(BbsMemberRoleEO.class, ids);
        //删除缓存
        CacheHandler.delete(BbsMemberRoleEO.class,mrs);
        putMemberRole();
        return getObject();
    }

    //把整个对象集合存到map中   实时更新
    private void putMemberRole(){
        Map<String ,Object> map  = new HashMap<String, Object>();
        map.put("siteId", LoginPersonUtil.getSiteId());
        map.put("recordStatus", BbsMemberRoleEO.RecordStatus.Normal.toString());
        List<BbsMemberRoleEO> memberRoleEOs = memberRoleService.getEntities(BbsMemberRoleEO.class,map);
        //放进map
        BbsMemberRoleUtil.putMemberRole(LoginPersonUtil.getSiteId(), memberRoleEOs);
    }

}
