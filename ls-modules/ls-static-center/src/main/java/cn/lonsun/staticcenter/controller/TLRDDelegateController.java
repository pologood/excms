package cn.lonsun.staticcenter.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.vo.VideoNewsVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.delegatemgr.entity.AdviceEO;
import cn.lonsun.delegatemgr.entity.DelegateEO;
import cn.lonsun.delegatemgr.internal.service.IAdviceService;
import cn.lonsun.delegatemgr.internal.service.IDelegateService;
import cn.lonsun.staticcenter.util.JdbcUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 铜陵郊区代表管理和建议管理老数据导入<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2017-6-13<br/>
 */
@Controller
@RequestMapping(value = "/TLRDDelegate")
public class TLRDDelegateController extends BaseController {
    @Autowired
    private IDelegateService delegateService;

    @Autowired
    private IAdviceService adviceService;

    private JdbcUtils jdbcUtils;

    @ModelAttribute
    public void get(@RequestParam(required = false) String id) {
        jdbcUtils = JdbcUtils.getInstance();
        jdbcUtils.setURLSTR("jdbc:jtds:sqlserver://61.191.61.136:31433;DatabaseName=tlrd_old;useLOBs=false;");
//        jdbcUtils.setURLSTR("jdbc:jtds:sqlserver://10.0.0.253:1433;DatabaseName=tlrd_old;useLOBs=false;");
        jdbcUtils.setUSERNAME("tlrd_old");
        jdbcUtils.setUSERPASSWORD("tlrd_old");

    }

    /**
     * 代表管理导入
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @RequestMapping("delegate")
    @ResponseBody
    public String delegate() throws ClassNotFoundException, SQLException {

        Long s = System.currentTimeMillis();

        //导入截止日期：2016-8-18号，下次导入19号以后的数据,手工从导入库删除
        //导入
        String sql = "select * from Tb_Deputy order by DeputyId asc";
        System.out.println(sql);

        List<Object> list = jdbcUtils.excuteQuery(sql, null);
        for (int i = 0, l = list.size(); i < l; i++) {
            Map<String, Object> map = (HashMap<String, Object>) list.get(i);
            DelegateEO eo=new DelegateEO();
            String deleNum = (String) map.get("DeputyNo");
            eo.setDeleNum(deleNum);
            String password = (String) map.get("DeputyPwd");
            eo.setPassword(password);
            String name = (String) map.get("DeputyName");
            eo.setName(name);

            //StrokesCode

            Boolean sexInt=(Boolean) map.get("Sex");
            String sex="";
            if(sexInt!=null){
                if(sexInt){
                    sex="男";
                }else{
                     sex="女";
                }
            }
            eo.setSex(sex);
            //出生日期
            String birthDate = (String) map.get("Birthday");
            if(!StringUtils.isEmpty(birthDate)){
                String[] str=birthDate.split("-");
                if(str!=null&&str.length>0){
                    if(str.length==3){
                        eo.setBirthYear(Integer.parseInt(str[0]));
                        eo.setBirthMonth(getMonthOrDay(str[1]));
                        eo.setBirthDay(getMonthOrDay(str[2]));
                    }else if(str.length==2){
                        eo.setBirthYear(Integer.parseInt(str[0]));
                        eo.setBirthMonth(getMonthOrDay(str[1]));
                    }else{
                        eo.setBirthYear(Integer.parseInt(str[0]));
                    }
                }
            }
            //民族
            String nation = (String) map.get("Nation");
            if(!StringUtils.isEmpty(nation)){
                nation=getNation(nation);
                eo.setNation(nation);
            }
            String birthPlace = (String) map.get("Nativeplace");
            eo.setBirthPlace(birthPlace);
            String idNum = (String) map.get("IDCard");
            eo.setIdNum(idNum);
            String health = (String) map.get("Health");
            eo.setHealth(health);
            String picPath = (String) map.get("PhotoPath");
           // "http://old.tlsrd.gov.cn/Photo/十五/";
            eo.setPicPath("http://old.tlsrd.gov.cn/Photo/十五/"+eo.getDeleNum()+".jpg");
            //党派
            String party = (String) map.get("Party");
            if(!StringUtils.isEmpty(party)){
                party=getParty(party);
                eo.setParty(party);
            }
            String partyDate = (String) map.get("JoinDate");
            if(!StringUtils.isEmpty(partyDate)){
                String[] str=partyDate.split("-");
                if(str!=null&&str.length>0){
                    if(str.length==3){
                        eo.setPartyYear(Integer.parseInt(str[0]));
                        eo.setPartyMonth(getMonthOrDay(str[1]));
                        eo.setPartyDay(getMonthOrDay(str[2]));
                    }else if(str.length==2){
                        eo.setPartyYear(Integer.parseInt(str[0]));
                        eo.setPartyMonth(getMonthOrDay(str[1]));
                    }else{
                        eo.setPartyYear(Integer.parseInt(str[0]));
                    }
                }
            }
            //代表团
            String delegation = (String) map.get("Delegation");
            if(!StringUtils.isEmpty(delegation)){
                delegation=getDelegation(delegation);
                eo.setDelegation(delegation);
            }
            //代表构成
            String deleComp = (String) map.get("Structure");
            if(!StringUtils.isEmpty(deleComp)){
                deleComp=getDeleComp(deleComp.trim());
                eo.setDeleComp(deleComp);
            }

            //行业分类
            String industryClass = (String) map.get("Industry");
            if(!StringUtils.isEmpty(industryClass)){
                industryClass=getIndustryClass(industryClass);
                eo.setIndustryClass(industryClass);
            }


            //职业构成
            String careerComp = (String) map.get("Profession");
            if(!StringUtils.isEmpty(careerComp)){
                careerComp=getCareerComp(careerComp);
                eo.setCareerComp(careerComp);
            }

            //代表资格
            String qualification = (String) map.get("Qualification");
            if(!StringUtils.isEmpty(qualification)){
                if("资格有效".equals(qualification.trim())){
                    eo.setDeleQualify("validity");
                }
            }
            //增加方式
            String addType = (String) map.get("AddMode");
            if(!StringUtils.isEmpty(addType)){
                if("换届选举".equals(addType.trim())){
                    eo.setAddType("general_election");
                }else{
                    eo.setAddType("by-election");
                }
            }

            Boolean isTerm=(Boolean) map.get("IsReappointment");
            if(isTerm!=null){
                if(isTerm){
                    eo.setIsTerm(1);
                }else{
                    eo.setIsTerm(0);
                }
            }

            String gradSchool = (String) map.get("GratuateSchool");
            eo.setGradSchool(gradSchool);
            String study = (String) map.get("Specialty");
            eo.setStudy(study);
            //学历
            String education = (String) map.get("Degree");
            if(!StringUtils.isEmpty(education)){
                education=getEducation(education);
                eo.setEducation(education);
            }
            //工作时间
            String workDate=(String) map.get("WorkDate");
            if(!StringUtils.isEmpty(workDate)){
                String[] str=workDate.split("-");
                if(str!=null&&str.length>0){
                    if(str.length==3){
                        eo.setWorkYear(Integer.parseInt(str[0]));
                        eo.setWorkMonth(getMonthOrDay(str[1]));
                        eo.setWorkDay(getMonthOrDay(str[2]));
                    }else if(str.length==2){
                        eo.setWorkYear(Integer.parseInt(str[0]));
                        eo.setWorkMonth(getMonthOrDay(str[1]));
                    }else{
                        eo.setWorkYear(Integer.parseInt(str[0]));
                    }
                }
            }

            String workUnit=(String) map.get("WorkDept");
            eo.setWorkUnit(workUnit);
            String address=(String) map.get("ContactAddress");
            eo.setAddress(address);
            String zipCode=(String) map.get("Zip");
            eo.setZipCode(zipCode);
            String work=(String) map.get("Headship");
            eo.setWork(work);
            String workTitle=(String) map.get("Technical");
            eo.setWorkTitle(workTitle);
            String telStr=(String) map.get("WorkPhone");
            eo.setTelNum(telStr);
            //FamilyAddress

            String phoneStr=(String) map.get("MobilePhone");
            if(!StringUtils.isEmpty(phoneStr)){
                Long phoneNum=Long.parseLong(phoneStr);
                eo.setPhoneNum(phoneNum);
            }
            String email=(String) map.get("Email");
            eo.setEmail(email);
            //Duty
            //Family
            //Resume
            String remark=(String) map.get("Comment");
            eo.setRemark(remark);
            //届
            String session = (String) map.get("TermName");
            if(!StringUtils.isEmpty(session)){
                if("十五".equals(session.trim())){
                    eo.setSession("fifteen");
                }
            }

            Boolean isShow=(Boolean) map.get("IsFirst");
            if(isShow!=null){
                if(isShow){
                    eo.setIsShow(1);
                }else{
                    eo.setIsShow(0);
                }
            }
            eo.setSiteId(4336083L);
            delegateService.saveEntity(eo);
        }
        Long s2 = System.currentTimeMillis();

        return "导入成功（耗时：" + (s2 - s) / 1000 + "秒）,条数："+list.size();
    }


    /**
     * 建议管理导入
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @RequestMapping("advice")
    @ResponseBody
    public String advice() throws ClassNotFoundException, SQLException {

        Long s = System.currentTimeMillis();

        //导入截止日期：2016-8-18号，下次导入19号以后的数据,手工从导入库删除
        //导入
        String sql = "select * from Tb_Proposal order by ProposalId asc";
        System.out.println(sql);

        List<Object> list = jdbcUtils.excuteQuery(sql, null);
        for (int i = 0, l = list.size(); i < l; i++) {
            Map<String, Object> map = (HashMap<String, Object>) list.get(i);
            AdviceEO eo=new AdviceEO();
            //届
            String session_first = (String) map.get("TermName");
            String session_second = (String) map.get("TimeName");
            if(!StringUtils.isEmpty(session_first)&&!StringUtils.isEmpty(session_second)){
                eo.setSession(getAdviceSession(session_first.trim(),session_second.trim()));
            }

            Long num = (Long) map.get("Number");
            String title = (String) map.get("Title");
            String content = (String) map.get("ProposalContent");
            String type = (String) map.get("Category");
            String leader = (String) map.get("Led");
            String attendant = (String) map.get("Seconded");
            String organizer = (String) map.get("Organizers");
            String coOrganizer = (String) map.get("Sponsors");
            String workUnit = (String) map.get("WorkUnit");
            String work = (String) map.get("WorkDuty");
            String address = (String) map.get("Address");
            String zipCode = (String) map.get("Zip");
            String phone = (String) map.get("Phone");
            String headDeal = (String) map.get("LedHandle");
            Date deliveryTime = (Date) map.get("AssignedTime");
            Date replyTime = (Date) map.get("ReplyTime");
            String dealStatus = (String) map.get("Status");
            String result = (String) map.get("Result");
            String rollDeal = (String) map.get("RollResult");
            eo.setNum(num);
            eo.setTitle(title);
            eo.setContent(content);
            eo.setType(type);
            eo.setLeader(leader);
            eo.setAttendant(attendant);
            eo.setOrganizer(organizer);
            eo.setCoOrganizer(coOrganizer);
            eo.setWorkUnit(workUnit);
            eo.setWork(work);
            eo.setAddress(address);
            eo.setZipCode(zipCode);
            eo.setPhone(phone);
            eo.setHeadDeal(headDeal);
            eo.setDeliveryTime(deliveryTime);
            eo.setReplyTime(replyTime);
            eo.setDealStatus(dealStatus);
            eo.setResult(result);
            eo.setRollDeal(rollDeal);
            eo.setSiteId(4336083L);
            adviceService.saveEntity(eo);

        }

        Long s2 = System.currentTimeMillis();

        return "导入成功（耗时：" + (s2 - s) / 1000 + "秒）,条数："+list.size();
    }

    private String getAdviceSession(String s_first,String s_second){
        String str="";
        if("十五".equals(s_first)){
            if("一".equals(s_second)) {
                str="one_fifteen";
            }else if("二".equals(s_second)){
                str="two_fifteen";
            }else if("四".equals(s_second)){
                str="four_fifteen";
            }else if("六".equals(s_second)){
                str="six_fifteen";
            }else if("七".equals(s_second)){
                str="seven_fifteen";
            }
        }
        return str;
    }


    //            <option value="01">汉族</option>
//            <option value="02">苗族</option>
//            <option value="03">满族</option>
//            <option value="04">壮族</option>
//            <option value="05">蒙古族</option>
//            <option value="06">藏族</option>
//            <option value="07">朝鲜族</option>
//            <option value="08">回族</option>
//            <option value="09">傣族</option>
//            <option value="10">维吾尔族</option>
//            <option value="11">白族</option>

    /**
     * 获取民族
     * @param oldNation
     * @return
     */
    private String getNation(String oldNation){
        String str="";
        if("01".equals(oldNation)) {
            str="han";
        }else if("02".equals(oldNation)){
            str="miao";
        }else if("03".equals(oldNation)){
            str="man";
        }else if("04".equals(oldNation)){
            str="zhuang";
        }else if("05".equals(oldNation)){
            str="meng_gu";
        }else if("06".equals(oldNation)){
            str="zang";
        }else if("07".equals(oldNation)){
            str="chao_xian";
        }else if("08".equals(oldNation)){
            str="hui";
        }else if("09".equals(oldNation)){
            str="dai";
        }else if("10".equals(oldNation)){
            str="wei_wu_er";
        }else if("11".equals(oldNation)){
            str="bai";
        }
        return str;
    }
//    <option value="01">枞阳县代表团</option>
//    <option value="02">铜官区代表团</option>
//    <option value="03">义安区代表团</option>
//    <option value="04">郊区代表团</option>
//    <option value="05">驻铜部队代表团</option>
    /**
     * 获取代表团
     * @param oldDelegation
     * @return
     */
    private String getDelegation(String oldDelegation){
        String str="";
        if("01".equals(oldDelegation)) {
            str="zongyangxian";
        }else if("02".equals(oldDelegation)){
            str="tongguanqu";
        }else if("03".equals(oldDelegation)){
            str="yianqu";
        }else if("04".equals(oldDelegation)){
            str="jiaoqu";
        }else if("05".equals(oldDelegation)){
            str="zhutong_budui";
        }
        return str;
    }
//    <option value="">==请选择==</option>
//    <option value="01">中共</option>
//    <option value="02">民革</option>
//    <option value="03">民盟</option>
//    <option value="04">民建</option>
//    <option value="05">民进</option>
//    <option value="06">农工党</option>
//    <option value="07">致公党</option>
//    <option value="08">九三学社</option>
//    <option value="09">台盟</option>
//    <option value="10">无党派人士人士</option>
    /**
     * 获取宗教
     * @param party
     * @return
     */
    private String getParty(String party) {
        String str="";
        if("01".equals(party)) {
            str="zhonggong";
        }else if("02".equals(party)){
            str="minge";
        }else if("03".equals(party)){
            str="minmeng";
        }else if("04".equals(party)){
            str="minjian";
        }else if("05".equals(party)){
            str="minjin";
        }else if("06".equals(party)){
            str="mingong";
        }else if("07".equals(party)){
            str="zhigong";
        }else if("08".equals(party)){
            str="jiusan";
        }else if("09".equals(party)){
            str="taimeng";
        }else if("10".equals(party)){
            str="wudang";
        }
        return str;
    }

//    <option value="">==请选择==</option>
//    <option value="01">工人</option>
//    <option value="02">农民</option>
//    <option value="03">干部</option>
//    <option value="04">知识分子</option>
//    <option value="05">归侨</option>
//    <option value="06">军人</option>
//    <option value="07">其他</option>

    /**
     * 获取代表构成
     * @param deleComp
     * @return
     */
    private String getDeleComp(String deleComp) {
        String str="";
        if("01".equals(deleComp)) {
            str="worker";
        }else if("02".equals(deleComp)){
            str="farmer";
        }else if("03".equals(deleComp)){
            str="cadre";
        }else if("04".equals(deleComp)){
            str="intellectual";
        }else if("05".equals(deleComp)){
            str="gui_qiao";
        }else if("06".equals(deleComp)){
            str="soldier";
        }else if("07".equals(deleComp)){
            str="others";
        }
        return str;
    }

//    <select name="ddlIndustry" id="ddlIndustry" style="width:100%;">
//    <option value="">==请选择==</option>
//    <option value="01">农、林、牧、渔业</option>
//    <option value="02">工业</option>
//    <option value="03">商业</option>
//    <option value="04">金融、保险业</option>
//    <option value="05">社会服务业</option>
//    <option value="06">国家机关</option>
//    <option value="07">教育</option>
//    <option value="08">军队</option>
//    <option value="09">科技</option>
//    <option value="10">文化</option>
//    <option value="11">医疗卫生</option>
//    <option value="12">宗教</option>
//    <option value="13">交通物流</option>
//    <option value="14">法律</option>
//    <option value="15">金融</option>
//    <option value="16">经济</option>
//    <option value="17">体育</option>
//    <option value="18">其他</option>
//
//    </select>

    /**
     * 获取行业分类
     * @param industryClass
     * @return
     */
    private String getIndustryClass(String industryClass) {
        String str="";
        if("01".equals(industryClass)) {
            str="nong_lin_mu_yu";
        }else if("02".equals(industryClass)){
            str="industry";
        }else if("03".equals(industryClass)){
            str="business";
        }else if("04".equals(industryClass)){
            str="finance_insurance";
        }else if("05".equals(industryClass)){
            str="social_service";
        }else if("06".equals(industryClass)){
            str="state_organ";
        }else if("07".equals(industryClass)){
            str="education";
        }else if("08".equals(industryClass)){
            str="army";
        }else if("09".equals(industryClass)){
            str="science";
        }else if("10".equals(industryClass)){
            str="culture";
        }else if("11".equals(industryClass)){
            str="meical_health";
        }else if("12".equals(industryClass)){
            str="religion";
        }else if("13".equals(industryClass)){
            str="traffic";
        }else if("14".equals(industryClass)){
            str="law";
        }else if("15".equals(industryClass)){
            str="finance";
        }else if("16".equals(industryClass)){
            str="economics";
        }else if("17".equals(industryClass)){
            str="sports";
        }else if("18".equals(industryClass)){
            str="others";
        }
        return str;
    }
//    <select name="ddlProfession" id="ddlProfession" style="width:100%;">
//    <option value="">==请选择==</option>
//    <option value="01">专业技术人员</option>
//    <option value="02">国家机关、党组织和企事业单位负责人</option>
//    <option value="03">办事员和有关人员</option>
//    <option value="04">商业工作人员</option>
//    <option value="05">服务业工作人员</option>
//    <option value="06">生产、运输设备操作人员及有关部门人员</option>
//    <option value="07">农、林、牧、渔、水利业生产人员</option>
//    <option value="08">军人</option>
//    <option value="09">其他人员</option>
//
//    </select>

    /**
     * 获取职业构成
     * @param careerComp
     * @return
     */
    private String getCareerComp(String careerComp) {
        String str="";
        if("01".equals(careerComp)) {
            str="professional_technical";
        }else if("02".equals(careerComp)){
            str="government_agency";
        }else if("03".equals(careerComp)){
            str="clerks_related";
        }else if("04".equals(careerComp)){
            str="business_staff";
        }else if("05".equals(careerComp)){
            str="service_workers";
        }else if("06".equals(careerComp)){
            str="product_related";
        }else if("07".equals(careerComp)){
            str="production";
        }else if("08".equals(careerComp)){
            str="soldier";
        }else if("09".equals(careerComp)){
            str="others";
        }
        return str;
    }

    /**
     * 获取增加方式
     * @param type
     * @return
     */
    private String getAddType(String type){
        String str="";
        if("01".equals(type)) {
            str="general_election";
        }else if("02".equals(type)){
            str="by-election";
        }
        return str;
    }

    /**
     * 获取学历
     * @param education
     * @return
     */
    private String getEducation(String education){
        String str="";
        if("01".equals(education)) {
            str="junior_middle";
        }else if("02".equals(education)){
            str="high_school";
        }else if("03".equals(education)){
            str="technical_school";
        }else if("04".equals(education)){
            str="university";
        }else if("05".equals(education)){
            str="zhongzhuan";
        }else if("06".equals(education)){
            str="dazhuan";
        }else if("07".equals(education)){
            str="undergraduate";
        }else if("08".equals(education)){
            str="master";
        }else if("09".equals(education)){
            str="doctor";
        }else if("10".equals(education)){
            str="graduate_student";
        } else if("11".equals(education)){
           str="graduate_equivalent";
        }
        return str;
    }

    /**
     * 获取月份或日份
     * @param str
     * @return
     */
    private Integer getMonthOrDay(String str){
        String first=str.substring(0,1);
        if("0".equals(first)){
            String second=str.substring(1,2);
           return  Integer.parseInt(second);
        }
        return  Integer.parseInt(str);
    }

}
