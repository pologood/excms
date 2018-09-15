package cn.lonsun.staticcenter.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.ideacollect.internal.entity.CollectIdeaEO;
import cn.lonsun.content.ideacollect.internal.entity.CollectInfoEO;
import cn.lonsun.content.ideacollect.internal.service.ICollectIdeaService;
import cn.lonsun.content.ideacollect.internal.service.ICollectInfoService;
import cn.lonsun.content.ideacollect.vo.CollectInfoVO;
import cn.lonsun.content.ideacollect.vo.IdeaQueryVO;
import cn.lonsun.content.interview.internal.dao.IInterviewInfoDao;
import cn.lonsun.content.interview.internal.dao.IInterviewQuestionDao;
import cn.lonsun.content.interview.internal.entity.InterviewInfoEO;
import cn.lonsun.content.interview.internal.entity.InterviewQuestionEO;
import cn.lonsun.content.interview.internal.service.IInterviewInfoService;
import cn.lonsun.content.interview.internal.service.IInterviewQuestionService;
import cn.lonsun.content.interview.vo.InterviewInfoVO;
import cn.lonsun.content.interview.vo.InterviewQueryVO;
import cn.lonsun.content.survey.internal.entity.SurveyThemeEO;
import cn.lonsun.content.survey.internal.service.ISurveyIpService;
import cn.lonsun.content.survey.internal.service.ISurveyOptionsService;
import cn.lonsun.content.survey.internal.service.ISurveyQuestionService;
import cn.lonsun.content.survey.internal.service.ISurveyThemeService;
import cn.lonsun.content.survey.vo.SurveyWebVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.util.Jacksons;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.RequestUtil;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.system.member.vo.MemberSessionVO;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.ModelConfigUtil;
import cn.lonsun.wechatmgr.internal.wechatapiutil.HttpAccessUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/content", produces = {"application/json;charset=UTF-8"})
public class ContentWebController extends BaseController {

    @Autowired
    private ICollectIdeaService collectIdeaService;

    @Autowired
    private ICollectInfoService collectInfoService;

    @Autowired
    private IInterviewQuestionDao interviewQuestionDao;

    @Autowired
    private IInterviewInfoDao interviewInfoDao;


    @SuppressWarnings("unchecked")
    @RequestMapping("getIdeaPage")
    @ResponseBody
    public Object getIdeaPage(IdeaQueryVO query, String dateFormat) {
        if (query.getCollectInfoId() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "CollectInfoId不能为空");
        }
        if (query.getPageIndex() == null || query.getPageIndex() < 0) {
            query.setPageIndex(0L);
        }
        Integer size = query.getPageSize();
        if (size == null || size <= 0 || size > Pagination.MAX_SIZE) {
            query.setPageSize(10);
        }
        query.setIssued(CollectIdeaEO.Status.Yes.getStatus());
        Pagination page = collectIdeaService.getPage(query);
        //		List<CollectIdeaEO> list= (List<CollectIdeaEO>)page.getData();
        if (page.getData() != null && page.getData().size() > 0) {

            dateFormat = StringUtils.isEmpty(dateFormat) ? "yyyy/MM/dd HH:mm:ss" : dateFormat;
            SimpleDateFormat simple = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            try {
                simple = new SimpleDateFormat(dateFormat);
            } catch (Exception e) {
            }
            for (CollectIdeaEO idea : (List<CollectIdeaEO>) page.getData()) {
                idea.setCreateWebTime(idea.getCreateDate() != null ? simple.format(idea.getCreateDate()) : "");
            }
        }
        return getObject(page);
    }

    @RequestMapping("saveCollectWeb")
    @ResponseBody
    public Object saveCollectWeb(Long collectInfoId, String name, String phone,String content, String checkCode, HttpServletRequest request) {
        if (collectInfoId == null || StringUtils.isEmpty(name) || StringUtils.isEmpty(phone) || StringUtils.isEmpty(content)) {
            return ajaxErr("提交参数不能为空");
        }
        String webCode = (String) request.getSession().getAttribute("webCode");
        if (StringUtils.isEmpty(checkCode) || StringUtils.isEmpty(webCode)) {
            return ajaxErr("验证码不能为空！");
        }
        if (!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())) {
            return ajaxErr("验证码不正确，请重新输入");
        }
        CollectIdeaEO idea = new CollectIdeaEO();
        idea.setCollectInfoId(collectInfoId);
        idea.setName(name);
        idea.setPhone(phone);
        idea.setContent(content);
        idea.setIp(RequestUtil.getIpAddr(request));
        collectIdeaService.saveEntity(idea);
        if (idea != null && idea.getCollectInfoId() != null) {
            CollectInfoEO info = collectInfoService.getEntity(CollectInfoEO.class, idea.getCollectInfoId());
            if (info != null) {
                Long count = info.getIdeaCount() + 1;
                info.setIdeaCount(count);
                collectInfoService.updateEntity(info);
            }
        }
        return ajaxOk("保存成功！");
    }

    @RequestMapping("saveMobileCollect")
    @ResponseBody
    public Object saveMobileCollect(Long collectInfoId, String name, String phone, String content, String checkCode, HttpServletRequest request) {

        if (collectInfoId == null || StringUtils.isEmpty(content)) {
            return ajaxErr("提交参数不能为空");
        }

        CollectInfoVO collectInfo = collectInfoService.getCollectInfoVO(collectInfoId);
        if (collectInfo == null) {
            return ajaxErr("参数错误！");
        }

        CollectIdeaEO idea = new CollectIdeaEO();

        //判断模型中配置属性
        ColumnTypeConfigVO conVO = ModelConfigUtil.getCongfigVO(collectInfo.getColumnId(), collectInfo.getSiteId());
        if (conVO != null) {
            if (conVO.getIsLoginGuest() == 1) {

                MemberSessionVO sessionMember = (MemberSessionVO) LoginPersonUtil.getSession().getAttribute("member");
                if (sessionMember == null) {
                    return ajaxErr("您尚未登录！");
                }
                idea.setName(sessionMember.getName());
                idea.setPhone(sessionMember.getPhone());
                idea.setMemberId(sessionMember.getId());

            } else {

                if (StringUtils.isEmpty(name) || StringUtils.isEmpty(phone)) {
                    return ajaxErr("提交参数不能为空");
                }

                String webCode = (String) request.getSession().getAttribute("webCode");
                if (StringUtils.isEmpty(checkCode) || StringUtils.isEmpty(webCode)) {
                    return ajaxErr("验证码不能为空！");
                }

                if (!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())) {
                    return ajaxErr("验证码不正确，请重新输入");
                }

                idea.setName(name);
                idea.setPhone(phone);
                idea.setMemberId(0L);
            }
        } else {
            return ajaxErr("参数错误！");
        }

        idea.setCollectInfoId(collectInfoId);
        idea.setContent(content);
        idea.setIp(RequestUtil.getIpAddr(request));
        collectIdeaService.saveEntity(idea);
        if (idea != null && idea.getCollectInfoId() != null) {
            CollectInfoEO info = collectInfoService.getEntity(CollectInfoEO.class, idea.getCollectInfoId());
            if (info != null) {
                Long count = info.getIdeaCount() + 1;
                info.setIdeaCount(count);
                collectInfoService.updateEntity(info);
            }
        }
        return ajaxOk("保存成功！");
    }

    @Autowired
    private IInterviewQuestionService interviewQuestionService;

    @Autowired
    private IInterviewInfoService interviewInfoService;


    @SuppressWarnings("unchecked")
    @RequestMapping("getQuestionPage")
    @ResponseBody
    public Object getQuestionPage(InterviewQueryVO query, String dateFormat) {
        if (query.getInterviewId() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "interviewId不能为空");
        }
        if (query.getPageIndex() == null || query.getPageIndex() < 0) {
            query.setPageIndex(0L);
        }
        Integer size = query.getPageSize();
        if (size == null || size <= 0 || size > Pagination.MAX_SIZE) {
            query.setPageSize(10);
        }
        query.setIssued(InterviewInfoEO.Status.Yes.getStatus());
        Map<String, Object> map = new HashMap<String, Object>();
        Pagination page = interviewQuestionService.getPage(query);
        if (page.getData() != null && page.getData().size() > 0) {

            dateFormat = StringUtils.isEmpty(dateFormat) ? "yyyy/MM/dd HH:mm:ss" : dateFormat;
            SimpleDateFormat simple = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            try {
                simple = new SimpleDateFormat(dateFormat);
            } catch (Exception e) {
            }
            for (InterviewQuestionEO question : (List<InterviewQuestionEO>) page.getData()) {
                question.setCreateWebTime(simple.format(question.getCreateDate()));
                question.setReplyWebTime(question.getReplyTime() != null ? simple.format(question.getReplyTime()) : "");

            }
        }
        return getObject(page);
    }

    /*
    * 访谈直播刷新，返回Map
    * */
    @SuppressWarnings("unchecked")
    @RequestMapping("getQuestionPageMap")
    @ResponseBody
    public Object refreshQuestionPage(InterviewQueryVO query, String dateFormat) {
        if (query.getInterviewId() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "interviewId不能为空");
        }
        if (query.getPageIndex() == null || query.getPageIndex() < 0) {
            query.setPageIndex(0L);
        }
        Integer size = query.getPageSize();
        if (size == null || size <= 0 || size > Pagination.MAX_SIZE) {
            query.setPageSize(10);
        }
        query.setIssued(InterviewInfoEO.Status.Yes.getStatus());
        Map<String, Object> map = new HashMap<String, Object>();
        Pagination page = interviewQuestionService.getPage(query);
        Long queCount = 0L;
        Long repCount = 0L;
        Long membCount = 0L;
        //		List<InterviewQuestionEO> list= (List<InterviewQuestionEO>)page.getData();
        if (page.getData() != null && page.getData().size() > 0) {

            dateFormat = StringUtils.isEmpty(dateFormat) ? "yyyy/MM/dd HH:mm:ss" : dateFormat;
            SimpleDateFormat simple = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            try {
                simple = new SimpleDateFormat(dateFormat);
            } catch (Exception e) {
            }
            for (InterviewQuestionEO question : (List<InterviewQuestionEO>) page.getData()) {
                question.setCreateWebTime(simple.format(question.getCreateDate()));
                question.setReplyWebTime(question.getReplyTime() != null ? simple.format(question.getReplyTime()) : "");

            }
            queCount = interviewQuestionDao.getParticipationNum(query.getInterviewId());
            repCount = interviewQuestionDao.getAnswerNum(query.getInterviewId());
            membCount = interviewQuestionDao.getQtNetFriendNum(query.getInterviewId());

        }
        map.put("page", page);
        map.put("questionCount", queCount);
        map.put("replyCount", repCount);
        map.put("memberCount", membCount);
        return getObject(map);
    }

    @RequestMapping("saveInterviewWeb")
    @ResponseBody
    public Object saveInterviewWeb(Long interviewId, String name, String content, String checkCode, HttpServletRequest request) {
        if (interviewId == null || StringUtils.isEmpty(name) || StringUtils.isEmpty(content)) {
            return ajaxErr("提交参数不能为空");
        }
        String webCode = (String) request.getSession().getAttribute("webCode");
        if (StringUtils.isEmpty(checkCode) || StringUtils.isEmpty(webCode)) {
            return ajaxErr("验证码不能为空！");
        }
        if (!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())) {
            return ajaxErr("验证码不正确，请重新输入");
        }
        InterviewQuestionEO question = new InterviewQuestionEO();
        question.setInterviewId(interviewId);
        question.setName(name);
        question.setContent(content);
        question.setIp(RequestUtil.getIpAddr(request));
        interviewQuestionService.saveEntity(question);
        return ajaxOk("保存成功！");
    }

    @RequestMapping("saveMobileInterview")
    @ResponseBody
    public Object saveMobileInterview(Long interviewId, String name, String content, String checkCode, HttpServletRequest request) {

        if (interviewId == null || StringUtils.isEmpty(content)) {
            return ajaxErr("提交参数不能为空");
        }

        InterviewInfoVO interviewInfo = interviewInfoService.getInterviewInfoVO(interviewId);
        if (interviewInfo == null) {
            return ajaxErr("参数错误！");
        }

        InterviewQuestionEO question = new InterviewQuestionEO();

        //判断模型中配置属性
        ColumnTypeConfigVO conVO = ModelConfigUtil.getCongfigVO(interviewInfo.getColumnId(), interviewInfo.getSiteId());
        if (conVO != null) {
            if (conVO.getIsLoginGuest() == 1) {

                MemberSessionVO sessionMember = (MemberSessionVO) LoginPersonUtil.getSession().getAttribute("member");
                if (sessionMember == null) {
                    return ajaxErr("您尚未登录!");
                }
                question.setName(sessionMember.getName());
                question.setMemberId(sessionMember.getId());

            } else {

                if (StringUtils.isEmpty(name)) {
                    return ajaxErr("提交参数不能为空");
                }

                String webCode = (String) request.getSession().getAttribute("webCode");
                if (StringUtils.isEmpty(checkCode) || StringUtils.isEmpty(webCode)) {
                    return ajaxErr("验证码不能为空！");
                }
                if (!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())) {
                    return ajaxErr("验证码不正确，请重新输入");
                }

                question.setName(name);
                question.setMemberId(0L);
            }
        } else {
            return ajaxErr("参数错误！");
        }

        question.setInterviewId(interviewId);
        question.setContent(content);
        question.setIp(RequestUtil.getIpAddr(request));
        interviewQuestionService.saveEntity(question);
        return ajaxOk("保存成功！");
    }

    @Autowired
    private ISurveyThemeService surveyThemeService;

    @Autowired
    private ISurveyQuestionService surveyQuestionService;

    @Autowired
    private ISurveyOptionsService surveyOptionsService;

    @Autowired
    private ISurveyIpService surveyIpService;

    @RequestMapping("saverReviewWeb")
    @ResponseBody
    public Object saverReviewWeb(String resultList, String checkCode, HttpServletRequest request) {
        String webCode = (String) request.getSession().getAttribute("webCode");
        if (StringUtils.isEmpty(checkCode) || StringUtils.isEmpty(webCode)) {
            return ajaxErr("验证码不能为空！");
        }
        if (!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())) {
            return ajaxErr("验证码不正确，请重新输入");
        }
        if (!StringUtils.isEmpty(resultList)) {
            SurveyWebVO[] resultLists = Jacksons.json().fromJsonToObject(resultList, SurveyWebVO[].class);
            if (resultLists != null && resultLists.length > 0) {
                String ip = RequestUtil.getIpAddr(request);
                //获取主题id
                Long themeId = resultLists[0].getThemeId();
                SurveyThemeEO theme = surveyThemeService.getEntity(SurveyThemeEO.class, themeId);
                if (theme == null) {
                    return ajaxErr("评议主题不存在！");
                }
                //获取改ip已投票数
                Long ipCount = null;
                if (theme.getIpLimit() == 1) {
                    ipCount = surveyIpService.getSurveyIpCount(themeId, ip, null);
                    if (ipCount >= theme.getIpDayCount()) {
                        return ajaxErr("此评议主题你已投票" + theme.getIpDayCount() + "次！");
                    }
                } else {
                    ipCount = surveyIpService.getSurveyIpCount(themeId, ip, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                    if (ipCount >= theme.getIpDayCount()) {
                        return ajaxErr("此评议主题每天只能投票" + theme.getIpDayCount() + "次！");
                    }
                }
                //验证完成后，保存投票结果
                surveyOptionsService.saveReviewWeb(resultLists, themeId, ip);
            }
        }
        return ajaxOk("保存成功！");
    }

    @RequestMapping("saveSurveyWeb")
    @ResponseBody
    public Object saveSurveyWeb(String resultList, String checkCode, HttpServletRequest request) {
        String webCode = (String) request.getSession().getAttribute("webCode");
        if (StringUtils.isEmpty(checkCode) || StringUtils.isEmpty(webCode)) {
            return ajaxErr("验证码不能为空！");
        }
        if (!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())) {
            return ajaxErr("验证码不正确，请重新输入");
        }
        if (!StringUtils.isEmpty(resultList)) {
            //字符转义处理  因为参数被拦截处理所有需要反向转义
            resultList = StringEscapeUtils.unescapeHtml(resultList);
            SurveyWebVO[] resultLists = Jacksons.json().fromJsonToObject(resultList, SurveyWebVO[].class);
            if (resultLists != null && resultLists.length > 0) {
                String ip = RequestUtil.getIpAddr(request);
                //获取主题id
                Long themeId = resultLists[0].getThemeId();
                SurveyThemeEO theme = surveyThemeService.getEntity(SurveyThemeEO.class, themeId);
                if (theme == null) {
                    return ajaxErr("投票主题不存在！");
                }
                //获取改ip已投票数
                Long ipCount = null;
                if (theme.getIpLimit() == 1) {
                    ipCount = surveyIpService.getSurveyIpCount(themeId, ip, null);
                    if (ipCount >= theme.getIpDayCount()) {
                        return ajaxErr("此投票主题你已投票" + theme.getIpDayCount() + "次！");
                    }
                } else {
                    ipCount = surveyIpService.getSurveyIpCount(themeId, ip, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                    if (ipCount >= theme.getIpDayCount()) {
                        return ajaxErr("此投票主题每天只能投票" + theme.getIpDayCount() + "次！");
                    }
                }
                //验证完成后，保存投票结果
                surveyOptionsService.saveWeb(resultLists, themeId, ip, null);
            }
        }
        return ajaxOk("保存成功！");
    }

    @RequestMapping("saveMobileSurvey")
    @ResponseBody
    public Object saveMobileSurvey(String resultList, String checkCode, HttpServletRequest request) {
        MemberSessionVO sessionMember = (MemberSessionVO) request.getSession().getAttribute("member");
//        if (sessionMember == null) {
//            return ajaxErr("您尚未登录!");
//        }
//		String webCode = (String) request.getSession().getAttribute("webCode");
//		if(StringUtils.isEmpty(checkCode) || StringUtils.isEmpty(webCode)){
//			return ajaxErr("验证码不能为空！");
//		}
//		if(!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())){
//			return ajaxErr("验证码不正确，请重新输入");
//		}
        if (!StringUtils.isEmpty(resultList)) {
            resultList = StringEscapeUtils.unescapeHtml(resultList);
            SurveyWebVO[] resultLists = Jacksons.json().fromJsonToObject(resultList, SurveyWebVO[].class);
            if (resultLists != null && resultLists.length > 0) {
                String ip = RequestUtil.getIpAddr(request);
                //获取主题id
                Long themeId = resultLists[0].getThemeId();
                SurveyThemeEO theme = surveyThemeService.getEntity(SurveyThemeEO.class, themeId);
                if (theme == null) {
                    return ajaxErr("投票主题不存在！");
                }
                //获取改ip已投票数
                Long ipCount = null;
                if (theme.getIpLimit() == 1) {
                    ipCount = surveyIpService.getSurveyIpCount(themeId, ip, null);
                    if (ipCount >= theme.getIpDayCount()) {
                        return ajaxErr("此投票主题你已投票" + theme.getIpDayCount() + "次！");
                    }
                } else {
                    ipCount = surveyIpService.getSurveyIpCount(themeId, ip, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                    if (ipCount >= theme.getIpDayCount()) {
                        return ajaxErr("此投票主题每天只能投票" + theme.getIpDayCount() + "次！");
                    }
                }
                //验证完成后，保存投票结果
                surveyOptionsService.saveWeb(resultLists, themeId, ip, sessionMember);
            }
        }
        return ajaxOk("保存成功！");
    }


    @SuppressWarnings("unchecked")
    @RequestMapping("getQuestionList")
    @ResponseBody
    public Object getQuestionList(InterviewQueryVO query, String dateFormat) {
        if (query.getInterviewId() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "interviewId不能为空");
        }
        Pagination page = new Pagination();
        query.setIssued(InterviewInfoEO.Status.Yes.getStatus());
        Long InterviewId = query.getInterviewId();
        String sortOrder = query.getSortOrder();
        List<InterviewQuestionEO> list = interviewQuestionService.getListByInterviewId(InterviewId, sortOrder);
        if (list.size() > 0) {
            dateFormat = StringUtils.isEmpty(dateFormat) ? "yyyy/MM/dd HH:mm:ss" : dateFormat;
            SimpleDateFormat simple = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            try {
                simple = new SimpleDateFormat(dateFormat);
            } catch (Exception e) {
            }
            for (InterviewQuestionEO question : list) {
                question.setCreateWebTime(simple.format(question.getCreateDate()));
                question.setReplyWebTime(question.getReplyTime() != null ? simple.format(question.getReplyTime()) : "");
            }
            page.setData(list);
        }
        return getObject(page);
    }

    /**
     * 阳春网站调用EX6老网站数据
     * @param url
     * @param action
     * @param nums
     * @return
     */
    @RequestMapping("getYCBDatas")
    @ResponseBody
    public Object getYCBDatas(String url,String action, Integer nums) {
        String address = url+"?action="+action+"&nums="+nums;
        if(AppUtil.isEmpty(url)||AppUtil.isEmpty(action)||AppUtil.isEmpty(nums)){
            return getObject();
        }
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject = HttpAccessUtil.HttpGet(address);
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject;
    }

}
