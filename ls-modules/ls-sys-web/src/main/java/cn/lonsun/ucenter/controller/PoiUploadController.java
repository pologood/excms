package cn.lonsun.ucenter.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.ldap.vo.PersonNodeVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.rbac.internal.service.IPersonService;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.internal.service.IMemberService;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.PoiExcelUtil;
import cn.lonsun.util.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangchao on 2016/7/26.
 */
@Controller
@RequestMapping(value = "/poiUpload")
public class PoiUploadController extends BaseController {

    @Autowired
    private IOrganService organService;
    @Autowired
    private IPersonService personService;

    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private IMemberService memberService;

    /**
     * 导入模版
     * @param request
     * @throws Exception
     */
    @RequestMapping("exportUnitxls")
    @ResponseBody
    public Object  exportUnitXls(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception{
        String organIdStr = request.getParameter("organId");
        if(organIdStr == null){
            return ajaxErr("父节点不能为空!");
        }
        if (file.getSize() == 0L){
            return ajaxErr("请选择导入文件!");
        }
        String suffix = PoiExcelUtil.getSuffix(file.getOriginalFilename());
        if (!suffix.equals("xls")){
            return ajaxErr("文件类型不正确!");
        }
        try {
            Long organId = organIdStr.equals("1")?null:Long.parseLong(organIdStr);
            List<OrganEO> organs = PoiExcelUtil.getOrgans(file.getInputStream(),organId);//更新父节点
            if(organs != null && organs.size() >0){
                for (int i = 0; i < organs.size(); i++) {
                    OrganEO organ =  organService.saveXlsOrgan(organs.get(i));
                    //保存科室
                    if(organ != null && organ.getOrganId() !=null && !StringUtils.isEmpty(organ.getActive())){
                        //保存部门
                        organ.setHasOrganUnits(1);
                        OrganEO organUnit = new OrganEO();
                        organUnit.setParentId(organ.getOrganId());
                        organUnit.setName(organ.getActive());
                        organUnit.setType(OrganEO.Type.OrganUnit.toString());
                        organUnit.setSortNum(1L);
                        organService.saveXlsOrgan(organUnit);
                    }
                }
                //更新父节点
                if (organId != null) {
                    organService.updateHasChildren4Organ(organId);
                    OrganEO organEO = organService.getEntity(OrganEO.class,organId);
                    SysLog.log("【系统管理】批量导入单位，导入到节点："+organEO.getName(),"OrganEO", CmsLogEO.Operation.Add.toString());

                }
                //			//异步执行
                taskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        organService.initSimpleOrgansCache();
                    }
                });
            }
        }catch(BaseRunTimeException e){
            return ajaxErr(e.getTipsMessage());
        }
        return getObject();
    }

    @RequestMapping("exportMemberxls")
    @ResponseBody
    public Object  exportMemberxls(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception{
        Long siteId= LoginPersonUtil.getSiteId();
        if (file.getSize() == 0L){
            return ajaxErr("请选择导入文件!");
        }
        String suffix = PoiExcelUtil.getSuffix(file.getOriginalFilename());
        if (!suffix.equals("xls")){
            return ajaxErr("文件类型不正确!");
        }
        try {
            List<MemberEO> members = PoiExcelUtil.getMembers(file.getInputStream(),siteId);//导入会员
            if(members != null && members.size() >0){
                for (int i = 0; i < members.size(); i++) {
                    String uid=members.get(i).getUid();
                    if(uid!=null&&siteId!=null){
                        MemberEO member=memberService.getMemberByUid(uid,siteId);
                        if(member == null) {
                            memberService.saveEntity(members.get(i));
                        }
                    }
                }
            }

        }catch(BaseRunTimeException e){
            return ajaxErr(e.getTipsMessage());
        }
        return getObject();
    }
   /* @RequestMapping("exportMemberxls")
    @ResponseBody
    public Object  exportMemberxls(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception{
       // Long siteId=Long.parseLong(request.getParameter("siteId"));
        Long siteId= LoginPersonUtil.getSiteId();
        if (file.getSize() == 0L){
            return ajaxErr("请选择导入文件!");
        }
        String suffix = PoiExcelUtil.getSuffix(file.getOriginalFilename());
        if (!suffix.equals("xls")){
            return ajaxErr("文件类型不正确!");
        }
        try {
            List<MemberEO> members = PoiExcelUtil.getMembers(file.getInputStream(),siteId);//导入会员
            if(members != null && members.size() >0){
                for (int i = 0; i < members.size(); i++) {
                    String uid=members.get(i).getUid();
                    if(uid!=null&&siteId!=null){
                        MemberEO member=memberService.getMemberByUid(uid,siteId);
                        if(member == null) {
                            memberService.saveEntity(members.get(i));
                        }
                    }
                }
            }

        }catch(BaseRunTimeException e){
            return ajaxErr(e.getTipsMessage());
        }
        return getObject();
    }*/
    /**
     * 导入模版
     * @param request
     * @throws Exception
     */
    @RequestMapping("exportPersonxls")
    @ResponseBody
    public Object  exportPersonXls(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        if (file.getSize() == 0L){
            return ajaxErr("请选择导入文件!");
        }
        String suffix = PoiExcelUtil.getSuffix(file.getOriginalFilename());
        if (!suffix.equals("xls")){
            return ajaxErr("文件类型不正确!");
        }
        try {
            List<PersonNodeVO> persons = PoiExcelUtil.getPersons(file.getInputStream());//更新父节点
            if(persons != null && persons.size() >0){
                for(int i=0,j= persons.size();i<j;i++) {
                    personService.saveXlsPerson(persons.get(i));
                }
                //		//异步执行
                taskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        personService.initSimplePersonsCache();
                    }
                });
            }
        }catch(BaseRunTimeException e){
            return ajaxErr(e.getTipsMessage());
        }
        return getObject();
    }
}
