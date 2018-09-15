package cn.lonsun.staticcenter.controller;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.ideacollect.internal.entity.CollectInfoEO;
import cn.lonsun.content.ideacollect.internal.service.ICollectInfoService;
import cn.lonsun.content.ideacollect.vo.CollectInfoVO;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.content.internal.service.IVideoNewsService;
import cn.lonsun.content.interview.internal.entity.InterviewInfoEO;
import cn.lonsun.content.interview.internal.service.IInterviewInfoService;
import cn.lonsun.content.interview.vo.InterviewInfoVO;
import cn.lonsun.content.leaderwin.internal.entity.LeaderTypeEO;
import cn.lonsun.content.leaderwin.internal.service.ILeaderInfoService;
import cn.lonsun.content.leaderwin.internal.service.ILeaderTypeService;
import cn.lonsun.content.leaderwin.vo.LeaderInfoVO;
import cn.lonsun.content.leaderwin.vo.LeaderWebVO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardEditVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardPageVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardReplyVO;
import cn.lonsun.content.messageBoard.service.IMessageBoardReplyService;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.content.onlinePetition.internal.service.IOnlinePetitionService;
import cn.lonsun.content.onlinePetition.vo.OnlinePetitionVO;
import cn.lonsun.content.onlinePetition.vo.PetitionPageVO;
import cn.lonsun.content.onlinePetition.vo.PetitionQueryVO;
import cn.lonsun.content.survey.internal.entity.SurveyOptionsEO;
import cn.lonsun.content.survey.internal.entity.SurveyQuestionEO;
import cn.lonsun.content.survey.internal.entity.SurveyReplyEO;
import cn.lonsun.content.survey.internal.entity.SurveyThemeEO;
import cn.lonsun.content.survey.internal.service.ISurveyOptionsService;
import cn.lonsun.content.survey.internal.service.ISurveyQuestionService;
import cn.lonsun.content.survey.internal.service.ISurveyReplyService;
import cn.lonsun.content.survey.internal.service.ISurveyThemeService;
import cn.lonsun.content.survey.util.MapUtil;
import cn.lonsun.content.survey.vo.SurveyThemeVO;
import cn.lonsun.content.vo.*;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.RequestUtil;
import cn.lonsun.core.util.ResultVO;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.mobile.internal.entity.MobileConfigEO;
import cn.lonsun.mobile.internal.service.IMobileConfigService;
import cn.lonsun.mobile.vo.MobileDataVO;
import cn.lonsun.net.service.entity.CmsWorkGuideEO;
import cn.lonsun.net.service.entity.vo.MobileInteractVO;
import cn.lonsun.net.service.entity.vo.MobileNetWorkVO;
import cn.lonsun.net.service.service.IWorkGuideService;
import cn.lonsun.publicInfo.internal.entity.*;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogService;
import cn.lonsun.publicInfo.internal.service.IPublicClassService;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publicInfo.vo.*;
import cn.lonsun.rbac.indicator.service.IIndicatorService;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.shiro.util.OnlineSessionUtil;
import cn.lonsun.site.contentModel.internal.service.IContentModelService;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.site.site.internal.cache.DictItemCache;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.sms.czsms.util.SmsHttpClient;
import cn.lonsun.sms.internal.entity.SendSmsEO;
import cn.lonsun.sms.internal.service.ISendSmsService;
import cn.lonsun.sms.util.SendSmsUtil;
import cn.lonsun.sms.util.SmsPropertiesUtil;
import cn.lonsun.solr.SolrQueryHolder;
import cn.lonsun.solr.vo.MobileResultVO;
import cn.lonsun.solr.vo.SolrIndexVO;
import cn.lonsun.solr.vo.SolrPageQueryVO;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.util.MongoUtil;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.generate.util.VideoUtil;
import cn.lonsun.system.datadictionary.internal.entity.DataDictEO;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.internal.service.IMemberService;
import cn.lonsun.system.member.vo.MemberSessionVO;
import cn.lonsun.util.*;
import cn.lonsun.wechatmgr.internal.wechatapiutil.MenuType;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.axiom.util.base64.Base64Utils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static cn.lonsun.cache.client.CacheHandler.getList;
import static cn.lonsun.util.ColumnUtil.getSiteUrl;

@Controller
@RequestMapping(value = "/mobile", produces = {"application/json;charset=UTF-8"})
public class MobileController extends BaseController {


    /**
     * 正则表达式：验证手机号
     */
    public static final String REGEX_MOBILE = "^((12[0-9])|(13[0-9])|(14[0-9])|(15[^4,\\D])|(16[0-9])|(17[0-9])|(19[0-9])|(18[0-9]))\\d{8}$";

    public enum guestBookType {
        INTERACT_LIST("interactlist"), // 互动列表
        CONSULT("consult"), // 咨询
        SUGGEST("suggest"), // 建议
        COMPLAIN("complain"), // 投诉
        REPORT("report"), // 举报
        INQUIRY("inquiry");// 查询

        private String name;

        guestBookType(String name) {
            this.name = name;
        }

        public String getValue() {
            return this.name;
        }
    }

    @Autowired
    private ILeaderTypeService leaderTypeService;

    @Autowired
    private ILeaderInfoService leaderInfoService;

    @Autowired
    private IMemberService memberService;

    @Autowired
    private IGuestBookService guestBookService;

    @Autowired
    private IMessageBoardService messageBoardService;

    @Autowired
    private ISendSmsService sendSmsService;

    @Autowired
    private IPublicContentService publicContentService;
    @Autowired
    private IPublicCatalogService publicCatalogService;

    @Autowired
    private IMobileConfigService mobileConfigService;

    @Autowired
    private IPublicClassService publicClassService;

    @Autowired
    private IIndicatorService indicatorService;

    @Autowired
    private IWorkGuideService workGuideService;

    @Autowired
    private IOnlinePetitionService petitionService;

    @Autowired
    private IBaseContentService baseContentService;

    @Autowired
    private IInterviewInfoService interviewInfoService;

    @Autowired
    private ICollectInfoService collectInfoService;

    @Autowired
    private ISurveyThemeService surveyThemeService;

    @Autowired
    private ISurveyQuestionService surveyQuestionService;

    @Autowired
    private ISurveyOptionsService surveyOptionsService;

    @Autowired
    private ISurveyReplyService surveyReplyService;

    @Autowired
    private IOrganService organService;

    @Autowired
    private IVideoNewsService videoNewsService;

    @Autowired
    private IColumnConfigService columnConfigService;

    @Autowired
    private IContentModelService contentModelService;

    @Autowired
    private IMessageBoardReplyService replyService;

    @Autowired
    private IColumnConfigService configService;

    /**
     * 注册
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("register")
    @ResponseBody
    public Object register(HttpServletRequest request, String siteId, String uid, String name, String password, String sex, String email, String phone,
                           String address, String question, String answer, String openType, String openId, String smsCode) {
        if (StringUtils.isEmpty(siteId) || StringUtils.isEmpty(uid) || StringUtils.isEmpty(password) || StringUtils.isEmpty(phone)) {
            return ajaxErr("注册登录名、密码、手机号、站点id不能为空，请重新输入~");
        }
        if (!phone.matches(REGEX_MOBILE)) {
            return ajaxErr("手机号格式不正确！");
        }
        if (StringUtils.isEmpty(smsCode)) {
            return ajaxErr("手机号注册验证码不能为空！");
        }
        Long siteIdL = Long.parseLong(siteId);
        Boolean hasUid = memberService.isExistUid(uid, siteIdL, null);
        if (hasUid) {
            return ajaxErr("账号已经存在，请重新输入~");
        }
        Boolean hasPhone = memberService.isExistPhone(phone, siteIdL, null);
        if (hasPhone) {
            return ajaxErr("手机号码已经存在，请重新输入~");
        }
        if (!"0000".equals(smsCode)) {
            Integer status = sendSmsService.isTimeOut(phone, smsCode);
            if (status == 2) {
                return ajaxErr("验证码无效！");
            }
            if (status == 3) {
                return ajaxErr("验证码错误！");
            }
        }
        MemberEO member = new MemberEO();
        member.setPlainpw(password);
        member.setPassword(DigestUtils.md5Hex(password));
        member.setSiteId(siteIdL);
        member.setUid(uid);
        member.setName(name);
        Integer sexM = MemberEO.Sex.Man.getSex();
        try {
            sexM = Integer.valueOf(sex);
        } catch (Exception e) {
        }
        member.setSex(sexM);
        member.setEmail(email);
        member.setPhone(phone);
        member.setAddress(address);
        member.setQuestion(question);
        member.setAnswer(answer);
        member.setOpenType(openType);
        member.setOpenId(openId);
        member.setStatus(MemberEO.Status.Enabled.getStatus());
        //短信已验证
        member.setSmsCheck(1);
        memberService.saveEntity(member);
        MemberSessionVO sessionMember = new MemberSessionVO();
        BeanUtils.copyProperties(member, sessionMember);
        return getObject(sessionMember);
    }

    /**
     * @param siteId
     * @param columnId
     * @param code
     * @param state
     * @param map
     * @param request
     * @return String return type
     * @throws
     * @Title: login
     * @Description: 在线互动
     */
    @RequestMapping("{siteId}/{columnId}/guestbook")
    public String interact(@PathVariable Long siteId, @PathVariable Long columnId, String code, String state, ModelMap map, HttpServletRequest request) {
        if (AppUtil.isEmpty(code)) {
            map.put("siteId", siteId);
            map.put("columnId", columnId);
        }

        List<ContentModelParaVO> recType = ModelConfigUtil.getParam(columnId, siteId, null);
        if (recType != null && recType.size() > 0) {
            map.put("recList", recType);
            map.put("recType", recType.get(0).getRecType());
        }

        List<ContentModelParaVO> typeList = ModelConfigUtil.getGuestBookType(columnId, siteId);
        if (typeList != null && typeList.size() > 0) {
            map.put("typeList", typeList);
            map.put("classCode", typeList.get(0).getClassCode());
        }

        String _vm = null;
        if (MenuType.INTERACT_LIST.equals(state)) {
            _vm = "/mobile/guestbook/guestbook_list";// 互动列表
        } else if (guestBookType.INQUIRY.getValue().equals(state)) {
            _vm = "/mobile/guestbook/guestbook_search";
        } else if (guestBookType.CONSULT.getValue().equals(state)) {
            map.put("title", "我要咨询");
            map.put("type", "do_consult");
            _vm = "/mobile/guestbook/guestbook_form";
        } else if (guestBookType.SUGGEST.getValue().equals(state)) {
            map.put("title", "我要建议");
            map.put("type", "do_suggest");
            _vm = "/mobile/guestbook/guestbook_form";
        } else if (guestBookType.COMPLAIN.getValue().equals(state)) {
            map.put("title", "我要投诉");
            map.put("type", "do_complain");
            _vm = "/mobile/guestbook/guestbook_form";
        } else if (guestBookType.REPORT.getValue().equals(state)) {
            map.put("title", "我要举报");
            map.put("type", "do_report");
            _vm = "/mobile/guestbook/guestbook_form";
        }
        return _vm;
    }

    // 保存表单
    @RequestMapping("saveGuestBook")
    @ResponseBody
    public Object saveGuestBook(GuestBookEditVO vo) {

        if (AppUtil.isEmpty(vo.getSiteId()) || AppUtil.isEmpty(vo.getColumnId())) {
            return ajaxErr("缺少必要参数！");
        }

        MemberSessionVO memberVO = null;
        ColumnTypeConfigVO conVO = ModelConfigUtil.getCongfigVO(vo.getColumnId(), vo.getSiteId());
        if (conVO != null) {
            if (conVO.getIsLoginGuest() == 1) {
                memberVO = (MemberSessionVO) LoginPersonUtil.getSession().getAttribute("member");
                if (memberVO == null || memberVO.getId() == null) {
                    return ajaxErr("请先登录后操作！");
                }
                vo.setCreateUserId(memberVO.getId());
                vo.setPersonName(memberVO.getName());
            }
        } else {
            return ajaxErr("模型不存在或已被删除！");
        }

        if (StringUtils.isEmpty(vo.getClassCode())) {
            return ajaxErr("留言类型不能为空！");
        }

        if (AppUtil.isEmpty(vo.getTitle())) {
            return ajaxErr("标题不能为空！");
        }

        if (AppUtil.isEmpty(vo.getGuestBookContent())) {
            return ajaxErr("内容不能为空！");
        }

        /*
         * MemberEO eo = memberService.getEntity(MemberEO.class, uid); if
         * (AppUtil.isEmpty(eo)) { return ajaxErr("登录用户不存在，请联系管理员！"); }
         */
        vo.setResourceType(3);// 手机
        vo.setIsRead(0);// 未读状态
        guestBookService.saveGusetBook(vo);
        return getObject();

    }

    // 保存表单
    @RequestMapping("saveMessageBoard")
    @ResponseBody
    public Object saveMessageBoard(MessageBoardEditVO vo) {

        if (AppUtil.isEmpty(vo.getSiteId()) || AppUtil.isEmpty(vo.getColumnId())) {
            return ajaxErr("缺少必要参数！");
        }

        MemberSessionVO memberVO = null;
        ColumnTypeConfigVO conVO = ModelConfigUtil.getCongfigVO(vo.getColumnId(), vo.getSiteId());
        if (conVO != null) {
            if (conVO.getIsLoginGuest() == 1) {
                memberVO = (MemberSessionVO) LoginPersonUtil.getSession().getAttribute("member");
                System.out.println(memberVO);
                if (memberVO == null || memberVO.getId() == null) {
                    return ajaxErr("请先登录后操作！");
                }

                vo.setPersonName(memberVO.getName());
                vo.setCreateUserId(memberVO.getId());
            }
        } else {
            return ajaxErr("模型不存在或已被删除！");
        }

        if (StringUtils.isEmpty(vo.getClassCode())) {
            return ajaxErr("留言类型不能为空");
        }

        if (AppUtil.isEmpty(vo.getTitle())) {
            return ajaxErr("标题不能为空！");
        }

        if (AppUtil.isEmpty(vo.getMessageBoardContent())) {
            return ajaxErr("内容不能为空！");
        }

        if (AppUtil.isEmpty(vo.getReceiveUnitId())) {
            return ajaxErr("受理单位不能为空！");
        }

        vo.setResourceType(3);// 手机
        MessageBoardEO messageBoardEO = messageBoardService.saveMessageBoardVO(vo);
        return getObject(messageBoardEO);

    }

    // 获取留言分页
    @RequestMapping(value = "getGuestBookPage")
    @ResponseBody
    public Object getGuestBookPage(Long pageIndex, Integer pageSize, Long userId, String classCode, Long siteId, Integer isReply, Integer orderType) {
        if (userId == null || siteId == null) {
            return null;
        }
        GuestBookPageVO pageVO = new GuestBookPageVO();
        if (pageIndex == null) {
            pageIndex = 0L;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        pageVO.setPageIndex(pageIndex);
        pageVO.setPageSize(pageSize);
        pageVO.setCreateUserId(userId);
        pageVO.setClassCode(classCode);
        pageVO.setTypeCode(BaseContentEO.TypeCode.guestBook.toString());
        pageVO.setSiteId(siteId);
        pageVO.setIsReply(isReply);
        pageVO.setOrderType(orderType);
        Pagination page = guestBookService.getMobilePage(pageVO);
        if (page != null && page.getData() != null && page.getData().size() > 0) {
            List<GuestBookEditVO> list = (List<GuestBookEditVO>) page.getData();
            for (GuestBookEditVO vo : list) {
                vo.setUri(getSiteUrl(null, siteId) + "/mobile/guestBookShow?id=" + vo.getBaseContentId());
                if (!StringUtils.isEmpty(vo.getClassCode())) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("petition_purpose", vo.getClassCode());
                    if (dictVO != null) {
                        vo.setClassName(dictVO.getKey());
                    }
                }
            }
        }
        return getObject(page);
    }

    // 获取留言分页
    @RequestMapping(value = "getMessageBoardPage")
    @ResponseBody
    public Object getMessageBoardPage(Long pageIndex, Integer pageSize, Long userId, String classCode, Long siteId, Integer isReply, Integer orderType) {
        if (userId == null || siteId == null) {
            return null;
        }
        MessageBoardPageVO pageVO = new MessageBoardPageVO();
        if (pageIndex == null) {
            pageIndex = 0L;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        pageVO.setPageIndex(pageIndex);
        pageVO.setPageSize(pageSize);
        pageVO.setCreateUserId(userId);
        pageVO.setClassCode(classCode);
        pageVO.setTypeCode(BaseContentEO.TypeCode.messageBoard.toString());
        pageVO.setSiteId(siteId);
        pageVO.setIsReply(isReply);
        /* pageVO.setOrderType(orderType); */
        Pagination page = messageBoardService.getMobilePage(pageVO);
        if (page != null && page.getData() != null && page.getData().size() > 0) {
            List<MessageBoardEditVO> list = (List<MessageBoardEditVO>) page.getData();
            for (MessageBoardEditVO vo : list) {
                vo.setUri(getSiteUrl(null, siteId) + "/mobile/messageBoardShow?id=" + vo.getBaseContentId());
                if (!StringUtils.isEmpty(vo.getClassCode())) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("petition_purpose", vo.getClassCode());
                    if (dictVO != null) {
                        vo.setClassName(dictVO.getKey());
                    }
                }
            }
        }
        return getObject(page);
    }

    // 获取留言分页
    @RequestMapping(value = "getReplyGuestBook")
    @ResponseBody
    public Object getReplyGuestBook(Long pageIndex, Integer pageSize, String classCode, Long siteId, Integer isReply, Integer orderType) {
        if (siteId == null) {
            return null;
        }
        GuestBookPageVO pageVO = new GuestBookPageVO();
        if (pageIndex == null) {
            pageIndex = 0L;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        pageVO.setPageIndex(pageIndex);
        pageVO.setPageSize(pageSize);
        pageVO.setClassCode(classCode);
        pageVO.setTypeCode(BaseContentEO.TypeCode.guestBook.toString());
        pageVO.setSiteId(siteId);
        pageVO.setIsReply(isReply);
        pageVO.setIsPublish(1);
        pageVO.setOrderType(orderType);
        Pagination page = guestBookService.getMobilePage(pageVO);
        List<MobileNetWorkVO> mobileVos = new ArrayList<MobileNetWorkVO>();
        if (page.getData() != null && page.getData().size() > 0) {
            List<GuestBookEditVO> list = (List<GuestBookEditVO>) page.getData();
            for (GuestBookEditVO editVO : list) {
                MobileNetWorkVO vo = new MobileNetWorkVO();
                vo.setId(editVO.getId());
                vo.setTitle(editVO.getTitle());
                vo.setUrl(getSiteUrl(null, siteId) + "/mobile/guestBookShow?id=" + editVO.getBaseContentId());
                if (orderType == null) {
                    vo.setDate(dateFormat(editVO.getAddDate()));
                } else if (orderType == 1) {
                    vo.setDate(dateFormat(editVO.getReplyDate()));
                } else if (orderType == 2) {
                    vo.setDate(dateFormat(editVO.getPublishDate()));
                }

                mobileVos.add(vo);
                if (!StringUtils.isEmpty(editVO.getClassCode())) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("petition_purpose", editVO.getClassCode());
                    if (dictVO != null) {
                        editVO.setClassName(dictVO.getKey());
                    }
                }
            }
        }
        page.setData(mobileVos);
        return getObject(page);
    }

    // 获取留言分页
    @RequestMapping(value = "getGuestBookVO")
    @ResponseBody
    public Object getGuestBookVO(Long id) {
        if (id == null) {
            return ajaxErr("留言ID为空!");
        }
        return getObject(guestBookService.getVO(id));
    }

    /**
     * 获取滁州短信验证码
     *
     * @param request
     * @param phone   手机号
     * @return
     */
    @RequestMapping("getCzSmsCode")
    @ResponseBody
    public Object getCzSmsCode(HttpServletRequest request, Long siteId, String phone, Long memberId) {
        if (siteId == null) {
            return ajaxErr("所属站点不能为空！");
        }
        if (StringUtils.isEmpty(phone)) {
            return ajaxErr("手机号不能为空！");
        }
        if (!phone.matches(REGEX_MOBILE)) {
            return ajaxErr("手机号格式不正确！");
        }

        Boolean hasPhone = memberService.isExistPhone(phone, siteId, memberId);
        if (hasPhone) {
            return ajaxErr("手机号码已经存在，请重新输入！");
        }
        Integer code = null;
        try {
            SiteMgrEO siteMgrs = CacheHandler.getEntity(SiteMgrEO.class, siteId);
            String siteName = siteMgrs == null ? "滁州市政府" : siteMgrs.getName();
            code = (int) ((Math.random() * 9 + 1) * 100000);
            String content = "您的注册验证码为：" + code + "【" + siteName + "】";
            Boolean isSend = SmsHttpClient.sendSmsUtil(phone, content, code.toString());
            if (!isSend) {
                return ajaxErr("短信发送失败,请重新发送！");
            }
        } catch (Exception e) {
            return ajaxErr("发送短信异常,请重新发送！");
        }
        return getObject();
    }

    /**
     * 获取短信验证码
     *
     * @param request
     * @param phone   手机号
     * @return
     */
    @RequestMapping("getSmsCode")
    @ResponseBody
    public Object getSmsCode(HttpServletRequest request, Long siteId, String phone) {
        if (StringUtils.isEmpty(phone)) {
            return ajaxErr("手机号不能为空！");
        }
        if (!phone.matches(REGEX_MOBILE)) {
            return ajaxErr("手机号格式不正确！");
        }
        if (siteId == null) {
            return ajaxErr("站点id不能为空！");
        }
        Integer code = null;
        try {
            SiteMgrEO siteMgrs = CacheHandler.getEntity(SiteMgrEO.class, siteId);
            String siteName = siteMgrs == null ? "龙讯科技" : siteMgrs.getName();
            // 获取域名
            String domain = SmsPropertiesUtil.domain;
            if (StringUtils.isEmpty(domain)) {
                String url = request.getRequestURL().toString().replace("http://", "");
                domain = url.substring(0, url.indexOf("/"));
            }
            String ip = RequestUtil.getIpAddr(request);
            String smsTail = "会员注册验证【" + siteName + "】";
            code = (int) ((Math.random() * 9 + 1) * 100000);
            String content = "您的注册验证码为：" + code + "【" + siteName + "】";
            String url =
                    SendSmsUtil.getUrl(SmsPropertiesUtil.smsurl, SmsPropertiesUtil.smsid, SmsPropertiesUtil.smspwd, phone, content, smsTail, domain, ip, ip,
                            1L, siteName, "15188888888", "注册会员", "");
            String result = SendSmsUtil.httpGet(url);
            if (result.equals("000")) {
                // 插入数据数
                SendSmsEO sms = new SendSmsEO();
                sms.setPhone(phone);
                sms.setCode(String.valueOf(code));
                sendSmsService.saveEntity(sms);
            } else if (result.equals("001")) {
                return ajaxErr("短信余额不足！");
            } else if (result.equals("002")) {
                return ajaxErr("域名没有授权！");
            } else if (result.equals("003")) {
                return ajaxErr("IP没有授权！");
            } else if (result.equals("004")) {
                return ajaxErr("用户密码不对！");
            } else if (result.equals("005")) {
                return ajaxErr("该用户已禁用！");
            } else if (result.equals("006")) {
                return ajaxErr("传入参数有误！");
            } else if (result.equals("007")) {
                return ajaxErr("无此用户！");
            } else if (result.equals("008")) {
                return ajaxErr("接收手机至少为一个有效手机号码！");
            } else if (result.equals("009")) {
                return ajaxErr("短信内容不能为空！");
            } else if (result.equals("010")) {
                return ajaxErr("短信内容长度超过了560个字节(280个汉字)！");
            } else if (result.equals("011")) {
                return ajaxErr("没有可用接口！");
            } else if (result.equals("999")) {
                return ajaxErr("未知错误！");
            } else if (result.equals("error")) {
                return ajaxErr("短信发送失败！");
            }
        } catch (Exception e) {
            return ajaxErr("发送短信异常！");
        }
        return getObject();
    }

    /**
     * 修改会员信息
     *
     * @param request
     * @param memberId
     * @param name
     * @param sex
     * @param address
     * @param phone
     * @param smsCode
     * @return
     */
    @RequestMapping("saveMember")
    @ResponseBody
    public Object saveMember(HttpServletRequest request, Long memberId, String name, String sex, String address, String phone, String smsCode) {
        if (memberId == null) {
            return ajaxErr("会员Id不能为空！");
        }
        MemberEO member = memberService.getEntity(MemberEO.class, memberId);
        if (member == null) {
            return ajaxErr("会员不存在！");
        }
        if (!StringUtils.isEmpty(name)) {
            member.setName(name);
        }
        if (!StringUtils.isEmpty(sex)) {
            Integer sexM = MemberEO.Sex.Man.getSex();
            try {
                sexM = Integer.valueOf(sex);
            } catch (Exception e) {
            }
            member.setSex(sexM);
        }
        if (!StringUtils.isEmpty(address)) {
            member.setAddress(address);
        }
        if (!StringUtils.isEmpty(phone)) {
            if (!phone.matches(REGEX_MOBILE)) {
                return ajaxErr("手机号格式不正确！");
            }
            if (StringUtils.isEmpty(smsCode)) {
                return ajaxErr("手机号注册验证码不能为空！");
            }
            Integer status = sendSmsService.isTimeOut(phone, smsCode);
            if (status == 2) {
                return ajaxErr("验证码无效！");
            }
            if (status == 3) {
                return ajaxErr("验证码错误！");
            }
            member.setPhone(phone);
        }
        memberService.updateEntity(member);
        MemberSessionVO sessionMember = new MemberSessionVO();
        BeanUtils.copyProperties(member, sessionMember);
        request.getSession().setAttribute("member", sessionMember);
        return getObject(sessionMember);
    }

    /**
     * 修改密码
     *
     * @param request
     * @param memberId
     * @param oldPw
     * @param newPw
     * @return
     */
    @RequestMapping("updatePassword")
    @ResponseBody
    public Object updatePassword(HttpServletRequest request, Long memberId, String oldPw, String newPw) {
        if (memberId == null) {
            return ajaxErr("会员Id不能为空！");
        }
        if (StringUtils.isEmpty(oldPw) || StringUtils.isEmpty(newPw)) {
            return ajaxErr("初始密码或新密码不能为空！");
        }
        MemberEO member = memberService.getEntity(MemberEO.class, memberId);
        if (member == null) {
            return ajaxErr("会员不存在！");
        }
        if (!member.getPassword().equals(DigestUtils.md5Hex(oldPw))) {
            return ajaxErr("初始密码不正确!");
        }
        member.setPassword(DigestUtils.md5Hex(newPw));
        member.setPlainpw(newPw);
        memberService.updateEntity(member);
        return getObject();
    }

    /**
     * 获取手机号 和 问题
     *
     * @param uid    登录名称
     * @param siteId 站点id
     * @return
     */
    @RequestMapping("getMemberVO")
    @ResponseBody
    public Object getMemberVO(String uid, Long siteId) {
        if (StringUtils.isEmpty(uid)) {
            return ajaxErr("登录名不能为空！");
        }
        if (siteId == null) {
            return ajaxErr("站点id不能为空！");
        }
        MemberEO member = memberService.getMemberByUid(uid, siteId);
        if (member == null) {
            return ajaxErr("会员信息不存在！");
        }
        MemberSessionVO memberVO = new MemberSessionVO();
        BeanUtils.copyProperties(member, memberVO);
        return getObject(memberVO);
    }

    /**
     * 验证回复答案是否正确
     *
     * @param memberId
     * @param answer
     * @return
     */
    @RequestMapping("checkQuestion")
    @ResponseBody
    public Object checkQuestion(Long memberId, String answer) {
        if (memberId == null) {
            return ajaxErr("会员id不能为空！");
        }
        if (StringUtils.isEmpty(answer)) {
            return ajaxErr("答案不能为空！");
        }
        MemberEO member = memberService.getEntity(MemberEO.class, memberId);
        if (member == null) {
            return ajaxErr("会员信息不存在！");
        }
        if (!answer.equals(member.getAnswer())) {
            return ajaxErr("回复答案错误！");
        }
        return getObject();
    }

    /**
     * 验证手机验证码是否正确
     *
     * @param phone
     * @param smsCode
     * @return
     */
    @RequestMapping("checkPhoneCode")
    @ResponseBody
    public Object checkPhoneCode(String phone, String smsCode) {
        if (StringUtils.isEmpty(phone)) {
            return ajaxErr("手机号不能为空！");
        }
        if (!phone.matches(REGEX_MOBILE)) {
            return ajaxErr("手机号格式不正确！");
        }
        if (StringUtils.isEmpty(smsCode)) {
            return ajaxErr("手机号注册验证码不能为空！");
        }
        Integer status = sendSmsService.isTimeOut(phone, smsCode);
        if (status == 2) {
            return ajaxErr("验证码无效！");
        }
        if (status == 3) {
            return ajaxErr("验证码错误！");
        }
        return getObject();
    }

    /**
     * 保存会员密码
     *
     * @param memberId
     * @param password
     * @return
     */
    @RequestMapping("savePassword")
    @ResponseBody
    public Object savePassword(Long memberId, String password) {
        if (memberId == null) {
            return ajaxErr("会员id不能为空！");
        }
        if (StringUtils.isEmpty(password)) {
            return ajaxErr("密码不能为空！");
        }
        MemberEO member = memberService.getEntity(MemberEO.class, memberId);
        if (member == null) {
            return ajaxErr("会员信息不存在！");
        }
        member.setPassword(DigestUtils.md5Hex(password));
        member.setPlainpw(password);
        memberService.updateEntity(member);
        return getObject();
    }

    /**
     * 信息公开全文检索
     *
     * @param pageVO
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getRetrievalPublicContent")
    public Object getRetrievalPublicContent(PublicContentRetrievalVO pageVO) {
        if (pageVO.getSiteId() == null) {
            return ajaxParamsErr("siteId不能为空！");
        }
        String siteUrl = getSiteUrl(null, pageVO.getSiteId());
        Pagination page = publicContentService.getRetrievalPagination(pageVO);
        if (page != null && page.getData() != null && page.getData().size() > 0) {
            List<?> list = (List<?>) page.getData();
            for (Object o : list) {
                PublicContentMobileVO vo = (PublicContentMobileVO) o;
                vo.setTitle(StringEscapeUtils.unescapeHtml4(vo.getTitle()));
                String redirectLink = vo.getRedirectLink();
                if (StringUtils.isEmpty(redirectLink)) {
                    vo.setRedirectLink(siteUrl + "/mobile/publicNews?id=" + vo.getId());
                }
            }
        }
        return getObject(page);
    }

    /**
     * 获取宿州信息公开列表数据，格式为市政府-data，政府部门-data，公共企事业单位-data
     *
     * @param queryVO
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping(value = "getSZPublicList")
    public Object getSZPublicList(PublicContentQueryVO queryVO) {
        if (queryVO.getSiteId() == null) {
            return ajaxParamsErr("siteId不能为空！");
        }
        String siteUrl = getSiteUrl(null, queryVO.getSiteId());
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        // 查询主动公开
        queryVO.setType(PublicContentEO.Type.DRIVING_PUBLIC.toString());
        queryVO.setOrganId(2655573L);// 查询市政府部门的
        List<PublicContentVO> list = publicContentService.getList(queryVO, 7);
        if (null != list && !list.isEmpty()) {
            for (PublicContentVO vo : list) {
                vo.setTitle(StringEscapeUtils.unescapeHtml4(vo.getTitle()));
                vo.setLink(siteUrl + "/mobile/publicNews?id=" + vo.getId());
            }
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "市政府");
        map.put("data", list);
        resultList.add(map);
        queryVO.setOrganId(null);
        queryVO.setCatId(1273L);// 查询政府部门的
        List<PublicCatalogEO> publicCatalogList = publicCatalogService.getAllChildListByCatId(queryVO.getCatId());
        if (null != publicCatalogList && !publicCatalogList.isEmpty()) {
            int index = 0;
            int size = publicCatalogList.size();
            Long[] catIds = new Long[size];
            for (PublicCatalogEO eo : publicCatalogList) {
                catIds[index++] = eo.getId();
            }
            queryVO.setCatIds(catIds);
            list = publicContentService.getList(queryVO, 7);
            if (null != list && !list.isEmpty()) {
                for (PublicContentVO vo : list) {
                    vo.setTitle(StringEscapeUtils.unescapeHtml4(vo.getTitle()));
                    vo.setLink(siteUrl + "/mobile/publicNews?id=" + vo.getId());
                }
            }
            map = new HashMap<String, Object>();
            map.put("name", "政府部门");
            map.put("data", list);
            resultList.add(map);
        }
        queryVO.setCatId(1274L);// 查询公共企事业单位的
        queryVO.setCatIds(null);
        publicCatalogList = publicCatalogService.getAllChildListByCatId(queryVO.getCatId());
        if (null != publicCatalogList && !publicCatalogList.isEmpty()) {
            int index = 0;
            int size = publicCatalogList.size();
            Long[] catIds = new Long[size];
            for (PublicCatalogEO eo : publicCatalogList) {
                catIds[index++] = eo.getId();
            }
            queryVO.setCatIds(catIds);
            list = publicContentService.getList(queryVO, 7);
            if (null != list && !list.isEmpty()) {
                for (PublicContentVO vo : list) {
                    vo.setTitle(StringEscapeUtils.unescapeHtml4(vo.getTitle()));
                    vo.setLink(siteUrl + "/mobile/publicNews?id=" + vo.getId());
                }
            }
            map = new HashMap<String, Object>();
            map.put("name", "公共企事业单位");
            map.put("data", list);
            resultList.add(map);
        }
        return getObject(resultList);
    }

    /**
     * 获取信息公开详细信息
     *
     * @param id
     * @param m
     * @return
     */
    @RequestMapping("publicNews")
    public String publicNews(Long id, ModelMap m) {
        PublicContentVO publicContentInfo = publicContentService.getPublicContent(id);
        m.put("publicContentInfo", publicContentInfo);
        return "/mobile/publicInfo/detail";
    }

    /**
     * 返回手机端配置和相关信息
     *
     * @return
     */
    @RequestMapping("getConfigList")
    @ResponseBody
    public Object mobileConfigList(MobileDataVO mobileDataVO, Long siteId, @RequestParam(defaultValue = "0") Integer nav) {
        if (AppUtil.isEmpty(siteId)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目、站点id不能为空");
        }

        List<MobileConfigEO> eoList = mobileConfigService.getMobileConfigList(siteId);

        Map<String, Object> map = new HashMap<String, Object>();

        if (nav == 1) {//取导航
            map.put("navigation", mobileNavigation(eoList));
        } else if (nav == 2) {//导航+新闻+专题
            map.put("focus", mobileFocus(siteId, 5, eoList));
            map.put("articleNews", mobileArticleNews(eoList));
            map.put("special", mobileSpecialNews(eoList));
        } else {//导航+轮播+新闻+专题
            map.put("navigation", mobileNavigation(eoList));
            map.put("focus", mobileFocus(siteId, 5, eoList));
            map.put("articleNews", mobileArticleNews(eoList));
            map.put("special", mobileSpecialNews(eoList));
        }
        return getObject(map);
    }

    // APP导航
    private List<MobileDataVO> mobileNavigation(List<MobileConfigEO> configList) {
        List<MobileDataVO> navigation = new ArrayList<MobileDataVO>();
        List<ColumnMgrEO> columnMgrList = new ArrayList<ColumnMgrEO>();
        for (MobileConfigEO eo : configList) {
            if (eo.getType() != null && eo.getType().equals("nav")) {
                MobileDataVO vo = new MobileDataVO();
                vo.setId(eo.getIndicatorId());
                if (eo.getCode().equals("leaderInfo")) {
                    vo.setUrl(getSiteUrl(null, eo.getSiteId()) + "/mobile/getLeaderTypes?siteId=" + eo.getSiteId() + "&columnId=" + eo.getIndicatorId());
                } else {
                    vo.setUrl(getSiteUrl(null, eo.getSiteId()) + "/mobile/column/" + eo.getSiteId() + "/" + eo.getIndicatorId());
                }
                columnMgrList = configService.getColumnByPId(eo.getIndicatorId());
                if (columnMgrList != null && columnMgrList.size() > 0) {
                    vo.setType(eo.getType() + "Parent");
                } else {
                    vo.setType(eo.getType());
                }
                vo.setName(eo.getName());
                vo.setCode(eo.getCode());
                vo.setChecked(eo.getIsChecked() == 1 ? true : false);
                navigation.add(vo);
            }
        }
//        map.put("special", mobileSpecialNews(eoList));
        return navigation;
    }

    // APP导航父级
    private List<MobileDataVO> mobileNavigationPartent(List<ColumnMgrEO> columnMgrList) {
        List<MobileDataVO> navigation = new ArrayList<MobileDataVO>();
        for (ColumnMgrEO eo : columnMgrList) {
            if (eo.getColumnTypeCode().equals("articleNews")) {
                MobileDataVO vo = new MobileDataVO();
                vo.setId(eo.getIndicatorId());
                vo.setUrl(getSiteUrl(null, eo.getSiteId()) + "/mobile/column/" + eo.getSiteId() + "/" + eo.getIndicatorId());
                vo.setName(eo.getName());
                vo.setType(eo.getType());
                vo.setCode(eo.getColumnTypeCode());
                vo.setChecked(true);
                navigation.add(vo);
            }
        }
//        map.put("special", mobileSpecialNews(eoList));
        return navigation;
    }

    // 轮播图
    private List<MobileDataVO> mobileFocus(Long siteId, Integer num, List<MobileConfigEO> configList) {
        StringBuffer sb = new StringBuffer();
        for (MobileConfigEO eo : configList) {
            if (eo.getType() != null && eo.getType().equals("focus")) {
                sb.append(eo.getIndicatorId()).append(",");
            }
        }
        List<MobileDataVO> focusVO = new ArrayList<MobileDataVO>();
        if (sb.length() > 0) {
            String ids = sb.toString().indexOf(",") > 0 ? sb.substring(0, sb.length() - 1) : sb.toString();
            String TypeCode = BaseContentEO.TypeCode.articleNews.toString() + "," + BaseContentEO.TypeCode.pictureNews.toString();
            Long[] columnIds = ColumnUtil.getQueryColumnIdByChild(ids, false, TypeCode);// new
            // Long[]{54039L};
            // siteId=53310L;
            // columnIds[0]=59391L;
            List<BaseContentEO> baseContentEO = mobileConfigService.getMobileIdsConfig(siteId, columnIds, num);
            for (BaseContentEO eo : baseContentEO) {
                MobileDataVO vo = new MobileDataVO();
                vo.setId(eo.getId());
                if (!AppUtil.isEmpty(eo.getImageLink())) {
                    vo.setImg(PathUtil.getUrl(eo.getImageLink()));
                }


                if (!AppUtil.isEmpty(eo.getRedirectLink())) {
                    if (eo.getRedirectLink().contains("http") || eo.getRedirectLink().contains("ftp")) {
                        vo.setUrl(eo.getRedirectLink());
                    } else {
                        vo.setUrl(getSiteUrl(null, eo.getSiteId()) + eo.getRedirectLink());
                    }
                } else {
                    vo.setUrl(getSiteUrl(null, eo.getSiteId()) + "/mobile/articleNews?contentId=" + eo.getId());
                }

                vo.setName(StringEscapeUtils.unescapeHtml4(eo.getTitle()));
                vo.setCode(eo.getTypeCode());
                vo.setDate(eo.getPublishDate());
                focusVO.add(vo);
            }
        }
        return focusVO;
    }

    // 新闻版块
    private List<MobileDataVO> mobileArticleNews(List<MobileConfigEO> configList) {
        List<MobileDataVO> articleNews = new ArrayList<MobileDataVO>();
        for (MobileConfigEO eo : configList) {
            if (eo.getType() != null && (eo.getType().equals("articleNews") || eo.getType().equals("videoNews"))) {
                MobileDataVO vo = new MobileDataVO();
                vo.setId(eo.getId());
                vo.setUrl(eo.getUri());
                vo.setName(eo.getName());
                vo.setType(eo.getType());
                vo.setCode(eo.getCode());
                ContentPageVO cpv = new ContentPageVO();
                cpv.setSiteId(eo.getSiteId());
                cpv.setColumnId(eo.getIndicatorId());
                cpv.setNum(5);
                List<MobileDataVO> contentList = articleNewsList(cpv);
                if (contentList != null && contentList.size() > 0) {
                    vo.setData(contentList);
                }
                articleNews.add(vo);
            }
        }
        return articleNews;
    }

    // 视频版块
    private List<MobileDataVO> mobileVideoNews(List<MobileConfigEO> configList) {
        List<MobileDataVO> articleNews = new ArrayList<MobileDataVO>();
        for (MobileConfigEO eo : configList) {
            if (eo.getType() != null && eo.getType().equals("videoNews")) {
                MobileDataVO vo = new MobileDataVO();
                vo.setId(eo.getId());
                vo.setUrl(eo.getUri());
                vo.setName(eo.getName());
                vo.setType(eo.getType());
                vo.setCode(eo.getCode());
                List<MobileDataVO> contentList = videoNewsList(eo.getSiteId(), eo.getIndicatorId(), 3);
                if (contentList != null && contentList.size() > 0) {
                    vo.setData(contentList);
                }
                articleNews.add(vo);
            }
        }
        return articleNews;
    }

    // 专题
    private List<MobileDataVO> mobileSpecialNews(List<MobileConfigEO> configList) {
        List<MobileDataVO> specialNews = new ArrayList<MobileDataVO>();
        for (MobileConfigEO eo : configList) {
            if (eo.getType() != null && eo.getType().equals("special")) {
                MobileDataVO vo = new MobileDataVO();
                vo.setId(eo.getId());
                vo.setUrl(getSiteUrl(null, eo.getSiteId()) + "/mobile/column/" + eo.getSiteId() + "/" + eo.getIndicatorId());
                vo.setName(eo.getName());
                vo.setType(eo.getType());
                vo.setCode(eo.getCode());
                vo.setImg(null);
                ContentPageVO cpv = new ContentPageVO();
                cpv.setSiteId(eo.getSiteId());
                cpv.setColumnId(eo.getIndicatorId());
                cpv.setNum(5);
                List<MobileDataVO> contentList = articleNewsList(cpv);
                if (contentList != null && contentList.size() > 0) {
                    vo.setData(contentList);
                }
                specialNews.add(vo);
            }
        }
        return specialNews;
    }

    // 新闻列表
    @RequestMapping("articleNewsList")
    @ResponseBody
    private List<MobileDataVO> articleNewsList(ContentPageVO pageVO) {
        List<MobileDataVO> contentVO = new ArrayList<MobileDataVO>();
        pageVO.setIsPublish(1);
        List<BaseContentEO> contentList = baseContentService.getList(pageVO);
        if (contentList != null && contentList.size() > 0) {
            for (BaseContentEO eo : contentList) {
                MobileDataVO vo = new MobileDataVO();
                if (!AppUtil.isEmpty(eo.getImageLink())) {
                    vo.setImg(PathUtil.getUrl(eo.getImageLink()));
                }
                vo.setId(eo.getId());
                vo.setName(eo.getTitle());
                if (AppUtil.isEmpty(eo.getRedirectLink())) {
                    if ("videoNews".equals(eo.getTypeCode())) {
                        vo.setUrl(getSiteUrl(null, eo.getSiteId()) + "/mobile/getVideoEO?contentId=" + eo.getId());
                    } else {
                        vo.setUrl(getSiteUrl(null, eo.getSiteId()) + "/mobile/articleNews?contentId=" + eo.getId());
                    }
                } else {
                    if (eo.getRedirectLink().contains("http") || eo.getRedirectLink().contains("ftp")) {
                        vo.setUrl(eo.getRedirectLink());
                    } else {
                        vo.setUrl(getSiteUrl(null, eo.getSiteId()) + eo.getRedirectLink());
                    }
                }
                vo.setDate(eo.getPublishDate());
                contentVO.add(vo);
            }
        }
        return contentVO;
    }

    // 视频列表
    private List<MobileDataVO> videoNewsList(Long siteId, Long columnId, Integer num) {
        List<MobileDataVO> contentVO = new ArrayList<MobileDataVO>();
        List<VideoNewsVO> contentList = videoNewsService.getVideoList(columnId, siteId, num);
        if (contentList != null && contentList.size() > 0) {
            for (BaseContentEO eo : contentList) {
                MobileDataVO vo = new MobileDataVO();
                vo.setImg(PathUtil.getUrl(eo.getImageLink()));
                vo.setId(eo.getId());
                vo.setName(eo.getTitle());
                vo.setUrl(getSiteUrl(null, eo.getSiteId()) + "/mobile/getVideoEO?contentId=" + eo.getId());
                vo.setDate(eo.getPublishDate());
                contentVO.add(vo);
            }
        }
        return contentVO;
    }

    /**
     * 手机端互动配置信息
     *
     * @return
     */
    @RequestMapping("getInteractionConfigList")
    @ResponseBody
    public Object mobileInteractionConfigList(MobileDataVO mobileDataVO, Long siteId) {
        if (AppUtil.isEmpty(siteId)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目、站点id不能为空");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        List<MobileConfigEO> eoList = mobileConfigService.getMobileConfigList(siteId);
        map.put("interaction", mobileInteraction(eoList, mobileDataVO.getOrderType(), mobileDataVO.getIsReply()));
        return getObject(map);
    }

    // 互动版块
    private List<MobileDataVO> mobileInteraction(List<MobileConfigEO> configList, Integer orderType, Integer isReply) {

        List<MobileDataVO> interaction = new ArrayList<MobileDataVO>();
        for (MobileConfigEO eo : configList) {
            if (eo.getType() != null && eo.getType().equals("interaction")) {
                MobileDataVO vo = new MobileDataVO();
                vo.setId(eo.getId());
                vo.setIndicatorId(eo.getIndicatorId());
                vo.setUrl(eo.getUri() + "?typeCode=" + eo.getCode());
                if (eo.getCode().equals("guestBook")) {
                    if (orderType != null) {
                        vo.setUrl(vo.getUrl() + "&orderType=" + orderType);
                    }
                    if (isReply != null) {
                        vo.setUrl(vo.getUrl() + "&isReply=" + isReply);
                    }
                    vo.setName(eo.getName());
                } else {
                    vo.setName(eo.getName());
                }
                vo.setType(eo.getType());
                vo.setCode(eo.getCode());
                if (eo.getCode() != null && eo.getCode().equals("interviewInfo")) {// 在线访谈
                    QueryVO queryVO = new QueryVO();
                    queryVO.setPageSize(3);
                    queryVO.setSiteId(eo.getSiteId());
                    queryVO.setColumnId(eo.getIndicatorId());
                    List<MobileDataVO> contentList = getInterviewPageList(queryVO);
                    if (contentList != null && contentList.size() > 0) {
                        vo.setData(contentList);
                    }
                    interaction.add(vo);
                } else if (eo.getCode() != null && eo.getCode().equals("survey")) {// 在线调查
                    QueryVO queryVO = new QueryVO();
                    queryVO.setPageSize(3);
                    queryVO.setSiteId(eo.getSiteId());
                    queryVO.setColumnId(eo.getIndicatorId());
                    List<MobileDataVO> contentList = getSurveyThemePageList(queryVO);
                    if (contentList != null && contentList.size() > 0) {
                        vo.setData(contentList);
                    }
                    interaction.add(vo);
                } else if (eo.getCode() != null && eo.getCode().equals("collectInfo")) {// 民意征集
                    QueryVO queryVO = new QueryVO();
                    queryVO.setPageSize(3);
                    queryVO.setSiteId(eo.getSiteId());
                    queryVO.setColumnId(eo.getIndicatorId());
                    List<MobileDataVO> contentList = getCollectInfoPageList(queryVO);
                    if (contentList != null && contentList.size() > 0) {
                        vo.setData(contentList);
                    }
                    interaction.add(vo);
                } else if (eo.getCode() != null && eo.getCode().equals("guestBook")) {// 回复选登
                    GuestBookPageVO queryVO = new GuestBookPageVO();
                    queryVO.setPageSize(3);
                    queryVO.setSiteId(eo.getSiteId());
                    queryVO.setColumnId(eo.getIndicatorId());
                    queryVO.setIsReply(isReply);
                    queryVO.setOrderType(orderType);
                    List<MobileDataVO> contentList = getReplyGuestBookList(queryVO);
                    if (contentList != null && contentList.size() > 0) {
                        vo.setData(contentList);
                    }
                    interaction.add(vo);
                }
            }
        }
        return interaction;
    }

    /**
     * 手机端互动配置信息(滁州用messageBoard)
     *
     * @return
     */
    @RequestMapping("getInteractionConfigListCZ")
    @ResponseBody
    public Object mobileInteractionConfigListCZ(MobileDataVO mobileDataVO, Long siteId) {
        if (AppUtil.isEmpty(siteId)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目、站点id不能为空");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        List<MobileConfigEO> eoList = mobileConfigService.getMobileConfigList(siteId);

        map.put("interaction", mobileInteractionCZ(eoList));
        return getObject(map);
    }


    // 互动版块
    private List<MobileDataVO> mobileInteractionCZ(List<MobileConfigEO> configList) {

        List<MobileDataVO> interaction = new ArrayList<MobileDataVO>();
        for (MobileConfigEO eo : configList) {
            if (eo.getType() != null && eo.getType().equals("interaction")) {
                MobileDataVO vo = new MobileDataVO();
                vo.setId(eo.getId());
                vo.setIndicatorId(eo.getIndicatorId());
                vo.setUrl(eo.getUri() + "?typeCode=" + eo.getCode());
                if (eo.getCode().equals("messageBoard")) {
                    vo.setName(eo.getName());
                } else {
                    vo.setName(eo.getName());
                }
                vo.setType(eo.getType());
                vo.setCode(eo.getCode());
                if (eo.getCode() != null && eo.getCode().equals("interviewInfo")) {// 在线访谈
                    QueryVO queryVO = new QueryVO();
                    queryVO.setPageSize(3);
                    queryVO.setSiteId(eo.getSiteId());
                    queryVO.setColumnId(eo.getIndicatorId());
                    List<MobileDataVO> contentList = getInterviewPageList(queryVO);
                    if (contentList != null && contentList.size() > 0) {
                        vo.setData(contentList);
                    }
                    interaction.add(vo);
                } else if (eo.getCode() != null && eo.getCode().equals("survey")) {// 在线调查
                    QueryVO queryVO = new QueryVO();
                    queryVO.setPageSize(3);
                    queryVO.setSiteId(eo.getSiteId());
                    queryVO.setColumnId(eo.getIndicatorId());
                    List<MobileDataVO> contentList = getSurveyThemePageList(queryVO);
                    if (contentList != null && contentList.size() > 0) {
                        vo.setData(contentList);
                    }
                    interaction.add(vo);
                } else if (eo.getCode() != null && eo.getCode().equals("collectInfo")) {// 民意征集
                    QueryVO queryVO = new QueryVO();
                    queryVO.setPageSize(3);
                    queryVO.setSiteId(eo.getSiteId());
                    queryVO.setColumnId(eo.getIndicatorId());
                    List<MobileDataVO> contentList = getCollectInfoPageList(queryVO);
                    if (contentList != null && contentList.size() > 0) {
                        vo.setData(contentList);
                    }
                    interaction.add(vo);
                } else if (eo.getCode() != null && eo.getCode().equals("messageBoard")) {// 回复选登
                    MessageBoardPageVO queryVO = new MessageBoardPageVO();
                    queryVO.setPageSize(3);
                    queryVO.setSiteId(eo.getSiteId());
                    queryVO.setColumnId(eo.getIndicatorId());
                    queryVO.setIsReply(eo.getIsReply());
                    if (eo.getIndicatorId() != null) {
                        queryVO.setColumnIds(String.valueOf(eo.getIndicatorId()));
                    }
                    List<MobileDataVO> contentList = getReplyMessageBoardList(queryVO);
                    if (contentList != null && contentList.size() > 0) {
                        vo.setData(contentList);
                    }
                    interaction.add(vo);
                }
            }
        }
        return interaction;
    }

    /**
     * 在线访谈
     *
     * @param query
     * @return
     */
    private List<MobileDataVO> getInterviewPageList(QueryVO query) {
        if (query.getColumnId() == null || query.getSiteId() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目、站点id不能为空");
        }
        if (query.getPageIndex() == null || query.getPageIndex() < 0) {
            query.setPageIndex(0L);
        }
        Integer size = query.getPageSize();
        if (size == null || size <= 0 || size > Pagination.MAX_SIZE) {
            query.setPageSize(15);
        }
        query.setIsPublish(1);
        query.setIsMobile(true);
        query.setTypeCode(BaseContentEO.TypeCode.interviewInfo.toString());
        Pagination page = interviewInfoService.getPage(query);
        List<MobileDataVO> content = new ArrayList<MobileDataVO>();
        if (page.getData() != null && page.getData().size() > 0) {
            for (InterviewInfoVO interviewInfo : (List<InterviewInfoVO>) page.getData()) {
                MobileDataVO vo = new MobileDataVO();
                vo.setId(interviewInfo.getInterviewId());
                vo.setName(interviewInfo.getTitle());
                if (!StringUtils.isEmpty(interviewInfo.getOutLink())) {
                    vo.setUrl(interviewInfo.getOutLink());
                } else {
                    vo.setUrl(getSiteUrl(null, query.getSiteId()) + "/mobile/getInterviewInfo?interviewId=" + interviewInfo.getInterviewId());
                }
                vo.setImg(PathUtil.getUrl(interviewInfo.getPicUrl()));
                if (interviewInfo.getIssuedTime() != null) {
                    vo.setDate(interviewInfo.getIssuedTime());
                }
                content.add(vo);
                // if (interviewInfo.getStartTime() != null &&
                // interviewInfo.getEndTime() != null) {
                // interviewInfo.setIsTimeOut(TimeOutUtil.getTimeOut(interviewInfo.getStartTime(),
                // interviewInfo.getEndTime()));
                // }
                // interviewInfo.setUri(ColumnUtil.getSiteUrl(null,
                // query.getSiteId())+"/mobile/getInterviewInfo?interviewId=" +
                // interviewInfo.getInterviewId());
            }
        }

        return content;
    }

    /**
     * 调查管理
     *
     * @param query
     * @return
     */
    private List<MobileDataVO> getSurveyThemePageList(QueryVO query) {
        if (query.getColumnId() == null || query.getSiteId() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目或站点id不能为空");
        }
        // 页码与查询最多查询数据量纠正
        if (query.getPageIndex() == null || query.getPageIndex() < 0) {
            query.setPageIndex(0L);
        }
        Integer size = query.getPageSize();
        if (size == null || size <= 0 || size > Pagination.MAX_SIZE) {
            query.setPageSize(15);
        }
        query.setIsPublish(1);
        query.setIsMobile(true);
        query.setTypeCode(BaseContentEO.TypeCode.survey.toString());
        Pagination page = surveyThemeService.getPage(query);
        List<MobileDataVO> content = new ArrayList<MobileDataVO>();
        if (page.getData() != null && page.getData().size() > 0) {
            for (SurveyThemeVO surveyTheme : (List<SurveyThemeVO>) page.getData()) {
                MobileDataVO vo = new MobileDataVO();
                vo.setId(surveyTheme.getThemeId());
                vo.setName(surveyTheme.getTitle());
                if (surveyTheme.getIsLink() == 1) {
                    vo.setUrl(surveyTheme.getLinkUrl());
                } else {
                    vo.setUrl(getSiteUrl(null, query.getSiteId()) + "/mobile/getSurveyTheme?themeId=" + surveyTheme.getThemeId());
                }
                vo.setDate(surveyTheme.getStartTime());
                content.add(vo);
                // if(surveyTheme.getStartTime() != null &&
                // surveyTheme.getEndTime() !=null){
                // surveyTheme.setIsTimeOut(TimeOutUtil.getTimeOut(surveyTheme.getStartTime(),
                // surveyTheme.getEndTime()));
                // }
            }
        }

        return content;
    }

    /**
     * 民意征集
     *
     * @param query
     * @return
     */
    public List<MobileDataVO> getCollectInfoPageList(QueryVO query) {
        if (query.getColumnId() == null || query.getSiteId() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目或站点id不能为空");
        }
        if (query.getPageIndex() == null || query.getPageIndex() < 0) {
            query.setPageIndex(0L);
        }
        Integer size = query.getPageSize();
        if (size == null || size <= 0 || size > Pagination.MAX_SIZE) {
            query.setPageSize(15);
        }
        query.setIsPublish(1);
        query.setIsMobile(true);
        query.setTypeCode(BaseContentEO.TypeCode.collectInfo.toString());
        Pagination page = collectInfoService.getPage(query);
        List<MobileDataVO> content = new ArrayList<MobileDataVO>();
        if (page.getData() != null && page.getData().size() > 0) {
            for (CollectInfoVO collectInfo : (List<CollectInfoVO>) page.getData()) {
                MobileDataVO vo = new MobileDataVO();
                vo.setId(collectInfo.getCollectInfoId());
                vo.setName(collectInfo.getTitle());
                if (!StringUtils.isEmpty(collectInfo.getLinkUrl())) {
                    vo.setUrl(collectInfo.getLinkUrl());
                } else {
                    vo.setUrl(getSiteUrl(null, query.getSiteId()) + "/mobile/getCollectInfo?infoId=" + collectInfo.getCollectInfoId());
                }
                vo.setDate(collectInfo.getStartTime());
                content.add(vo);
                // if (collectInfo.getStartTime() != null &&
                // collectInfo.getEndTime() != null) {
                // collectInfo.setIsTimeOut(TimeOutUtil.getTimeOut(collectInfo.getStartTime(),
                // collectInfo.getEndTime()));
                // }
                // collectInfo.setUri(ColumnUtil.getSiteUrl(null,
                // query.getSiteId()) + "/mobile/getCollectInfo?infoId=" +
                // collectInfo.getCollectInfoId());
            }
        }

        return content;
    }


    /**
     * 民意征集阳西
     *
     * @param query
     * @return
     */
    public List<MobileInteractVO> getCollectInfoPageListXY(QueryVO query) {
        if (query.getColumnId() == null || query.getSiteId() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目或站点id不能为空");
        }
        if (query.getPageIndex() == null || query.getPageIndex() < 0) {
            query.setPageIndex(0L);
        }
        Integer size = query.getPageSize();
        if (size == null || size <= 0 || size > Pagination.MAX_SIZE) {
            query.setPageSize(15);
        }
        query.setIsPublish(1);
        query.setIsMobile(true);
        query.setTypeCode(BaseContentEO.TypeCode.collectInfo.toString());
        Pagination page = collectInfoService.getPage(query);
        List<MobileInteractVO> content = new ArrayList<MobileInteractVO>();
        if (page.getData() != null && page.getData().size() > 0) {
            for (CollectInfoVO collectInfo : (List<CollectInfoVO>) page.getData()) {
                MobileInteractVO vo = new MobileInteractVO();
                vo.setTheme(collectInfo.getTitle());
                if (!StringUtils.isEmpty(collectInfo.getLinkUrl())) {
                    vo.setUrl(collectInfo.getLinkUrl());
                } else {
                    vo.setUrl(getSiteUrl(null, query.getSiteId()) + "/mobile/getCollectInfo?infoId=" + collectInfo.getCollectInfoId());
                }

                if (collectInfo.getStartTime() != null &&
                        collectInfo.getEndTime() != null) {
                    collectInfo.setIsTimeOut(TimeOutUtil.getTimeOut(collectInfo.getStartTime(),
                            collectInfo.getEndTime()));
                }
                vo.setIsTimeOut(collectInfo.getIsTimeOut());
                if (vo.getIsTimeOut().equals(1)) {
                    vo.setDeptName("未开始");
                } else if (vo.getIsTimeOut().equals(2)) {
                    vo.setDeptName("进行中");
                } else if (vo.getIsTimeOut().equals(3)) {
                    vo.setDeptName("已结束");
                }
                content.add(vo);
            }
        }

        return content;
    }


    /**
     * 回复选登
     *
     * @param query
     * @return
     */
    public List<MobileDataVO> getReplyGuestBookList(GuestBookPageVO query) {
        if (query.getColumnId() == null || query.getSiteId() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目或站点id不能为空");
        }
        if (query.getPageIndex() == null || query.getPageIndex() < 0) {
            query.setPageIndex(0L);
        }
        Integer size = query.getPageSize();
        if (size == null || size <= 0 || size > Pagination.MAX_SIZE) {
            query.setPageSize(15);
        }
        query.setIsPublish(1);
        query.setTypeCode(BaseContentEO.TypeCode.guestBook.toString());
        if (query.getOrderType() == null) {
            query.setOrderType(1);
        }
        if (query.getIsReply() == null) {// 默认调取已回复
            query.setIsReply(1);
        } else if (query.getIsReply() == 0) {// 获取所有的
            query.setIsReply(null);
        }

        Pagination page = guestBookService.getMobilePage(query);
        List<MobileDataVO> content = new ArrayList<MobileDataVO>();
        if (page.getData() != null && page.getData().size() > 0) {
            for (GuestBookEditVO editVO : (List<GuestBookEditVO>) page.getData()) {
                MobileDataVO vo = new MobileDataVO();
                vo.setId(editVO.getId());
                vo.setName(editVO.getTitle());
                vo.setUrl(getSiteUrl(null, query.getSiteId()) + "/mobile/guestBookShow?id=" + editVO.getBaseContentId());
                if (query.getOrderType() == 0) {
                    vo.setDate(editVO.getAddDate());
                } else if (query.getOrderType() == 1) {
                    vo.setDate(editVO.getReplyDate());
                } else if (query.getOrderType() == 2) {
                    vo.setDate(editVO.getPublishDate());
                } else {
                    vo.setDate(editVO.getAddDate());
                }

                content.add(vo);
                if (!StringUtils.isEmpty(editVO.getClassCode())) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("petition_purpose", editVO.getClassCode());
                    if (dictVO != null) {
                        editVO.setClassName(dictVO.getKey());
                    }
                }
            }
        }

        return content;
    }

    /**
     * 回复选登
     *
     * @param query
     * @return
     */
    public List<MobileDataVO> getReplyMessageBoardList(MessageBoardPageVO query) {
        if (query.getColumnId() == null || query.getSiteId() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目或站点id不能为空");
        }
        if (query.getPageIndex() == null || query.getPageIndex() < 0) {
            query.setPageIndex(0L);
        }
        Integer size = query.getPageSize();
        if (size == null || size <= 0 || size > Pagination.MAX_SIZE) {
            query.setPageSize(15);
        }
        if (query.getIsReply() == null) {// 默认调取已回复
            query.setIsReply(1);
        } else if (query.getIsReply() == 0) {// 获取所有的
            query.setIsReply(null);
        }
        query.setIsPublish(1);
        query.setTypeCode(BaseContentEO.TypeCode.messageBoard.toString());

        Pagination page = messageBoardService.getMobilePage(query);
        List<MobileDataVO> content = new ArrayList<MobileDataVO>();
        if (page.getData() != null && page.getData().size() > 0) {
            for (MessageBoardEditVO editVO : (List<MessageBoardEditVO>) page.getData()) {
                MobileDataVO vo = new MobileDataVO();
                vo.setId(editVO.getId());
                vo.setName(editVO.getTitle());
                vo.setUrl(getSiteUrl(null, query.getSiteId()) + "/mobile/messageBoardShow?id=" + editVO.getBaseContentId());
                vo.setDate(editVO.getAddDate());
                content.add(vo);
                if (!StringUtils.isEmpty(editVO.getClassCode())) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("petition_purpose", editVO.getClassCode());
                    if (dictVO != null) {
                        editVO.setClassName(dictVO.getKey());
                    }
                }
            }
        }

        return content;
    }

    /**
     * 分类列表
     *
     * @param siteId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getPublicClassify")
    public Object getPublicClassify(Long siteId) {
        // PublicContentRetrievalVO pageVO = new PublicContentRetrievalVO();
        // pageVO.setSiteId(siteId);
        // return getObject(publicClassService.getPublicClassify(pageVO));

        List<PublicClassMobileVO> list = new ArrayList<PublicClassMobileVO>();

        Map<String, Object> map = new HashMap<String, Object>();
        if (!AppUtil.isEmpty(siteId)) {
            map.put("siteId", siteId);
        }
        map.put("type", "publicClass");
        List<MobileConfigEO> configList = mobileConfigService.getEntities(MobileConfigEO.class, map);// getMobileConfigList(LoginPersonUtil.getSiteId());

        for (MobileConfigEO eo : configList) {
            PublicClassEO publicClassEO = CacheHandler.getEntity(PublicClassEO.class, CacheGroup.CMS_ID, eo.getIndicatorId());
            PublicClassMobileVO publicClassMobileVO = new PublicClassMobileVO();
            publicClassMobileVO.setId(publicClassEO.getId());
            publicClassMobileVO.setName(publicClassEO.getName());
            publicClassMobileVO.setParentId(publicClassEO.getParentId());
            publicClassMobileVO.setSortNum(publicClassEO.getSortNum());
            list.add(publicClassMobileVO);
        }
        return getObject(list);
    }

    /**
     * 查询顶层目录树
     *
     * @param organId
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getPublicTopCatalog")
    public Object getPublicTopCatalog(Long organId) {
        // List<PublicCatalogEO> list =
        // CacheHandler.getList(PublicCatalogEO.class, CacheGroup.CMS_PARENTID,
        // 0);
        List<PublicCatalogEO> list = new ArrayList<PublicCatalogEO>();

        Map<String, Object> map = new HashMap<String, Object>();
        // map.put("siteId",LoginPersonUtil.getSiteId());
        map.put("type", "publicInfo");
        List<MobileConfigEO> configList = mobileConfigService.getEntities(MobileConfigEO.class, map);// getMobileConfigList(LoginPersonUtil.getSiteId());

        for (MobileConfigEO eo : configList) {
            PublicCatalogEO publicCatalogEO = CacheHandler.getEntity(PublicCatalogEO.class, CacheGroup.CMS_ID, eo.getIndicatorId());
            list.add(publicCatalogEO);
        }

        if (null != organId && null != list && !list.isEmpty()) {// 排除单位
            OrganConfigEO organConfigEO = CacheHandler.getEntity(OrganConfigEO.class, CacheGroup.CMS_ORGAN_ID, organId);
            if (null != organConfigEO && null != organConfigEO.getCatId()) {
                Long catId = organConfigEO.getCatId();
                for (Iterator<PublicCatalogEO> it = list.iterator(); it.hasNext(); ) {
                    if (catId.equals(it.next().getId())) {
                        it.remove();
                    }
                }
            }
        }
        return getObject(list);
    }

    /**
     * 组配分类
     *
     * @param organId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getPublicCatalog")
    public Object getPublicCatalog(Long organId) {
        if (null == organId) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "单位id不能为空！");
        }
        OrganConfigEO organConfigEO = CacheHandler.getEntity(OrganConfigEO.class, CacheGroup.CMS_ORGAN_ID, organId);
        if (null == organConfigEO || null == organConfigEO.getCatId()) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "本单位没有配置信息公开目录！");
        }
        Long parentId = organConfigEO.getCatId();// 顶层组配分类id
        // 查询出单位配置的私有目录和隐藏目录
        List<PublicCatalogOrganRelEO> relList = getList(PublicCatalogOrganRelEO.class, CacheGroup.CMS_PARENTID, organId);
        Map<Long, PublicCatalogOrganRelEO> relMap = new HashMap<Long, PublicCatalogOrganRelEO>();
        if (null != relList && !relList.isEmpty()) {
            for (PublicCatalogOrganRelEO organRel : relList) {
                relMap.put(organRel.getCatId(), organRel);
            }
        }
        // 查询出所有目录信息
        List<PublicCatalogEO> childList = publicCatalogService.getAllChildListByCatId(parentId);
        // 过滤出显示的
        if (null != childList && !childList.isEmpty()) {
            for (Iterator<PublicCatalogEO> it = childList.iterator(); it.hasNext(); ) {
                PublicCatalogEO eo = it.next();
                // 删除不显示的目录
                if (relMap.containsKey(eo.getId())) {
                    if (!relMap.get(eo.getId()).getIsShow()) {
                        it.remove();
                        continue;
                    } else {
                        PublicCatalogOrganRelEO relEO = relMap.get(eo.getId());
                        eo.setCode(relEO.getCode());
                        eo.setName(relEO.getName());
                        eo.setLink(relEO.getLink());
                        eo.setSortNum(relEO.getSortNum());
                        eo.setDescription(relEO.getDescription());
                    }
                } else if (eo.getType() == 2) {// 删除私有
                    it.remove();
                    continue;
                }
            }
        }
        Object obj = getObject(childList);
        if (obj instanceof ResultVO) {
            ((ResultVO) obj).setDesc(parentId + "");
        }
        return obj;
    }

    /**
     * 返回手机新闻信息
     *
     * @return
     */
    @RequestMapping(value = "getNews")
    @ResponseBody
    public Object getNews(SolrPageQueryVO vo) {
        String str = vo.getKeywords();
        if (!StringUtils.isEmpty(str)) {
            try {
                if (isBase64(str)) {//如果编码过，则需要解码，如果没有，则不需要
                    vo.setKeywords(new String(Base64Utils.decode(str), "utf-8"));
                }
            } catch (UnsupportedEncodingException e) {
            }
        }
        vo.setIslight(false);
        vo.setTypeCode("articleNews,pictureNews,videoNews,workGuide,messageBoard,public_content");
        Pagination page = getSearchList(vo);
        page.setPageSize(vo.getPageSize());
        page.setPageIndex(vo.getPageIndex());
        try {
            Long count = SolrQueryHolder.queryCount(vo);
            page.setTotal(count);
            page.getPageCount();
        } catch (Exception e) {
            page.getPageCount();
        }

        return getObject(page);
    }

    //判断是否base64编码
    private boolean isBase64(String str) {
        String base64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        return Pattern.matches(base64Pattern, str);
    }

    private Pagination getSearchList(SolrPageQueryVO vo) {
        vo.setIslight(false);
        vo.setTypeCode("articleNews,pictureNews,videoNews,workGuide,messageBoard,public_content");
        Pagination page = new Pagination();
        page.setPageSize(vo.getPageSize());
        page.setPageIndex(vo.getPageIndex());
        try {
            Long count = SolrQueryHolder.queryCount(vo);
            page.setTotal(count);
            page.getPageCount();
        } catch (Exception e) {
            page.getPageCount();
        }

        try {
            List<SolrIndexVO> list = SolrQueryHolder.query(vo);
            List<MobileResultVO> mobilevos = new ArrayList<MobileResultVO>();
            if (list != null) {
                for (SolrIndexVO indexVO : list) {
                    MobileResultVO resultVO = new MobileResultVO();
                    resultVO.setTitle(indexVO.getTitle());
                    resultVO.setContent(indexVO.getContent());
                    if (indexVO.getColumnId() != null) {
                        String link = null;
                        if (indexVO.getTypeCode().contains(BaseContentEO.TypeCode.workGuide.toString())) {
                            // 网上办事模块增加链接ID
                            if (null != indexVO.getId()) {
                                CmsWorkGuideEO guide = workGuideService.getByContentId(Long.valueOf(indexVO.getId()));
                                if (null != guide) {
                                    String linkUrl = guide.getLinkUrl();
                                    if (null != guide.getLinkUrl() && !"".equals(guide.getLinkUrl())) {
                                        link = linkUrl;
                                    } else {
                                        link = getSiteUrl(indexVO.getColumnId(), indexVO.getSiteId()) + "/mobile/getWorkGuidesDetail?id=" + guide.getId();
                                    }
                                }
                            }
                        } else if (indexVO.getTypeCode().contains(BaseContentEO.TypeCode.articleNews.toString())
                                || vo.getTypeCode().contains(BaseContentEO.TypeCode.pictureNews.toString())
                                || vo.getTypeCode().contains(BaseContentEO.TypeCode.videoNews.toString())) {

                            if (null != indexVO.getUrl() && !"".equals(indexVO.getUrl())) {
                                link = indexVO.getUrl();
                            } else {
                                // 新闻类的增加链接ID
                                link = ColumnUtil.getSiteUrl(indexVO.getColumnId(), indexVO.getSiteId()) + "/mobile/articleNews?contentId=" + indexVO.getId();// 拿到栏目页和文章页id
                            }
                        }
                        resultVO.setUrl(link);
                    }

                    resultVO.setDate(indexVO.getCreateDate());
                    resultVO.setContent("");
                    mobilevos.add(resultVO);
                }
            }
            page.setData(mobilevos);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return page;
    }

    /**
     * 网上办事栏目信息
     *
     * @param columnId
     * @return
     */
    @RequestMapping(value = "getOnlineWork")
    @ResponseBody
    public Object getOnlineWork(Long columnId, Integer count, Long unitColumnId) {
        if (null == count) {
            count = 0;
        }
//        List<IndicatorEO> list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_PARENTID, columnId);
        List<ColumnMgrEO> list = CacheHandler.getList(ColumnMgrEO.class, CacheGroup.CMS_PARENTID, columnId);
        if (list == null || list.size() == 0) {
            return getObject(list);
        }

        List<MobileNetWorkVO> mobileVos = new ArrayList<MobileNetWorkVO>();
        int c = count + 1;
        for (ColumnMgrEO eo : list) {
            if (null != eo.getIsShow() && eo.getIsShow() != 1) {
                continue;
            }
            MobileNetWorkVO vo = new MobileNetWorkVO();
            vo.setTitle(eo.getName());
            vo.setId(eo.getIndicatorId());
            vo.setParentId(eo.getParentId());
            if (null != unitColumnId && eo.getIndicatorId().intValue() == unitColumnId.intValue()) {
                vo.setUrl(getSiteUrl(eo.getIndicatorId(), null) + "/mobile/getWorkGuideOrgans?columnId=" + eo.getIndicatorId() + "&siteId=" + eo.getSiteId());
            } else {
                if (eo.getIsParent().intValue() == 1) {
                    if (count >= 2) {
                        vo.setUrl(getSiteUrl(eo.getIndicatorId(), null) + "/mobile/getAllColumnGuides?siteId=" + eo.getSiteId() + "&columnId="
                                + eo.getIndicatorId());
                    } else {
                        vo.setUrl(getSiteUrl(eo.getIndicatorId(), null) + "/mobile/getOnlineWork?count=" + c + "&columnId=" + eo.getIndicatorId()
                                + "&unitColumnId=" + (null == unitColumnId ? "" : unitColumnId));
                    }
                } else {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("indicatorId", eo.getIndicatorId());
                    ColumnConfigEO config = columnConfigService.getEntity(ColumnConfigEO.class, map);
                    if (null != config && config.getColumnTypeCode() != null && config.getColumnTypeCode().equals(BaseContentEO.TypeCode.articleNews.toString())) {
                        vo.setUrl(getSiteUrl(eo.getIndicatorId(), null) + "/mobile/getArticleNewsPageList?columnId=" + eo.getIndicatorId() + "&siteId="
                                + eo.getSiteId());
                    } else {
                        vo.setUrl(getSiteUrl(eo.getIndicatorId(), null) + "/mobile/getWorkGuides?siteId=" + eo.getSiteId() + "&columnId=" + eo.getIndicatorId());
                    }
                }
            }
            vo.setDate(dateFormat(eo.getCreateDate()));
            mobileVos.add(vo);
        }

        return getObject(mobileVos);
    }

    /**
     * 获取网上办事绑定的单位
     *
     * @return
     */
    @RequestMapping(value = "getWorkGuideOrgans")
    @ResponseBody
    public Object getWorkGuideOrgans(Long columnId, Long siteId) {
        // 获取所有网上办事绑定的单位
        List<OrganEO> list = contentModelService.getAllBindUnit(siteId);
        List<MobileNetWorkVO> mobileVos = new ArrayList<MobileNetWorkVO>();
        if (null != list) {
            for (OrganEO eo : list) {
                if (null != eo) {
                    MobileNetWorkVO vo = new MobileNetWorkVO();
                    vo.setTitle(eo.getName());
                    vo.setId(eo.getOrganId());
                    vo.setParentId(columnId);
                    vo.setUrl(getSiteUrl(columnId, null) + "/mobile/getOrganGuides?&organId=" + eo.getOrganId() + "&siteId=" + siteId);
                    mobileVos.add(vo);
                }
            }
        }

        return getObject(mobileVos);
    }

    /**
     * 获取网上办事绑定的单位的办事项
     *
     * @return
     */
    @RequestMapping(value = "getOrganGuides")
    @ResponseBody
    public Object getOrganGuides(ParamDto dto) {
        Pagination page = workGuideService.getPageEOs(dto, dto.getOrganId(), null, null, "workGuide");
        List<MobileNetWorkVO> mobileVos = new ArrayList<MobileNetWorkVO>();
        if (null != mobileVos) {
            List<CmsWorkGuideEO> guides = (List<CmsWorkGuideEO>) page.getData();
            for (CmsWorkGuideEO eo : guides) {
                MobileNetWorkVO vo = new MobileNetWorkVO();
                vo.setId(eo.getId());
                vo.setTitle(eo.getName());
                vo.setUrl(getSiteUrl(null, eo.getSiteId()) + "/mobile/getWorkGuidesDetail?id=" + eo.getId());
                if (null != eo.getCreateDate()) {
                    vo.setDate(dateFormat(eo.getCreateDate()));
                }
                mobileVos.add(vo);
            }
        }
        page.setData(mobileVos);
        return getObject(page);
    }

    /**
     * 服务配置信息
     *
     * @param siteId
     * @return
     */
    @RequestMapping(value = "getOnlineWorkConfigList")
    @ResponseBody
    public Object getOnlineWorkConfigList(Long siteId, Integer count, Long columnId) {

        if (null == count) {
            count = 0;
        }

        List<ColumnMgrEO> indicatorEOs = null;
        if (null != columnId) {
            indicatorEOs = CacheHandler.getList(ColumnMgrEO.class, CacheGroup.CMS_PARENTID, columnId);
        } else {
            List<MobileConfigEO> eoList = mobileConfigService.getMobileConfigList(siteId);
            indicatorEOs = new ArrayList<ColumnMgrEO>();
            for (MobileConfigEO eo : eoList) {
                if (eo.getCode() != null && ("workGuide,relatedRule,tableResources,redirect").contains(eo.getCode())) {
                    ColumnMgrEO indicatorEO = CacheHandler.getEntity(ColumnMgrEO.class, eo.getIndicatorId());
//                    IndicatorEO indicatorEO = indicatorService.getEntity(IndicatorEO.class, eo.getIndicatorId());
//                    indicatorEO.setCode(eo.getCode());
                    /*indicatorEO.setIndicatorId(eo.getIndicatorId());
                    indicatorEO.setSiteId(eo.getSiteId());
                    indicatorEO.setCreateDate(eo.getCreateDate());
                    indicatorEO.setCode(eo.getCode());
                    indicatorEO.setName(eo.getName());
                    if (("relatedRule,tableResources,redirect").contains(eo.getCode())) {
                        indicatorEO.setIsParent(0);
                    } else {
                        indicatorEO.setIsParent(1);
                    }

                    IndicatorEO indicator = CacheHandler.getEntity(IndicatorEO.class, eo.getIndicatorId());
                    if (null != indicator.getParentId()) {
                        indicatorEO.setParentId(indicator.getParentId());
                    }*/

                    indicatorEOs.add(indicatorEO);
                }
            }
        }
        List<MobileNetWorkVO> mobileVos = new ArrayList<MobileNetWorkVO>();
        int c = count + 1;
        for (ColumnMgrEO eo : indicatorEOs) {
            if (null != eo.getIsShow() && eo.getIsShow() != 1) {
                continue;
            }
            MobileNetWorkVO vo = new MobileNetWorkVO();
            vo.setTitle(eo.getName());
            vo.setId(eo.getIndicatorId());
            vo.setParentId(eo.getParentId());
            if (eo.getIsParent().intValue() == 1) {
                if (count >= 2) {
                    vo.setUrl(getSiteUrl(eo.getIndicatorId(), null) + "/mobile/getAllColumnGuides?siteId=" + eo.getSiteId() + "&columnId="
                            + eo.getIndicatorId());
                } else {
                    vo.setUrl(getSiteUrl(eo.getIndicatorId(), null) + "/mobile/getOnlineWorkConfigList?count=" + c + "&columnId=" + eo.getIndicatorId());
                }
            } else {

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("indicatorId", eo.getIndicatorId());
                ColumnConfigEO config = columnConfigService.getEntity(ColumnConfigEO.class, map);
                String typeCode = config.getColumnTypeCode();
                if (typeCode != null && typeCode.equals("redirect")) {
                    vo.setUrl(getSiteUrl(eo.getIndicatorId(), null) + "/mobile/getWorkGuideOrgans?columnId=" + eo.getIndicatorId() + "&siteId="
                            + eo.getSiteId());
                } else if (typeCode != null && typeCode.equals(BaseContentEO.TypeCode.articleNews.toString())) {
                    vo.setUrl(getSiteUrl(eo.getIndicatorId(), null) + "/mobile/getArticleNewsPageList?columnId=" + eo.getIndicatorId() + "&siteId="
                            + eo.getSiteId());
                } else {
                    vo.setUrl(getSiteUrl(eo.getIndicatorId(), null) + "/mobile/getWorkGuides?siteId=" + eo.getSiteId() + "&columnId=" + eo.getIndicatorId());
                }
                // vo.setUrl(getSiteUrl(eo.getIndicatorId(), null) +
                // "/mobile/getWorkGuides?siteId=" + eo.getSiteId() +
                // "&columnId=" + eo.getIndicatorId());
            }
            vo.setDate(dateFormat(eo.getCreateDate()));
            mobileVos.add(vo);
        }

        return getObject(mobileVos);
    }

    /**
     * 服务配置信息
     *
     * @param siteId
     * @return
     */
    @RequestMapping(value = "getOnlineWorkConfigListXY")
    @ResponseBody
    public Object getOnlineWorkConfigListXY(Long siteId, Integer count, Long columnId) {
        List<MobileNetWorkVO> mobileVos = new ArrayList<MobileNetWorkVO>();
        //热门服务
        MobileNetWorkVO hotServiceVo = new MobileNetWorkVO();
        hotServiceVo.setTitle("热门服务");
        List<MobileNetWorkVO> childHotServiceList = new ArrayList<MobileNetWorkVO>();

        List<BaseContentEO> baseContentEOs = baseContentService.getWorkGuidXYContent(siteId, 4294839L);
        for (BaseContentEO eo : baseContentEOs) {
            MobileNetWorkVO mobileNetWorkVO = new MobileNetWorkVO();
            mobileNetWorkVO.setTitle(eo.getTitle());
            mobileNetWorkVO.setUrl(eo.getRedirectLink());
            childHotServiceList.add(mobileNetWorkVO);
        }
        hotServiceVo.setMobileNetWorkVOs(childHotServiceList);
        mobileVos.add(hotServiceVo);
        MobileNetWorkVO level = new MobileNetWorkVO();
        List<MobileNetWorkVO> levelList = new ArrayList<MobileNetWorkVO>();
        //个人办事
        MobileNetWorkVO personServiceVo = new MobileNetWorkVO();
        personServiceVo.setTitle("个人办事");
        personServiceVo.setUrl("http://www.yjbs.gov.cn/index2.jspx");
        List<MobileNetWorkVO> childPersonServiceList = new ArrayList<MobileNetWorkVO>();
        baseContentEOs = baseContentService.getWorkGuidXYContent(siteId, 4294851L);
        for (BaseContentEO eo : baseContentEOs) {
            MobileNetWorkVO mobileNetWorkVO = new MobileNetWorkVO();
            mobileNetWorkVO.setTitle(eo.getTitle());
            mobileNetWorkVO.setUrl(eo.getRedirectLink());
            childPersonServiceList.add(mobileNetWorkVO);
        }
        personServiceVo.setMobileNetWorkVOs(childPersonServiceList);
        levelList.add(personServiceVo);
        //企业办事
        MobileNetWorkVO unitServiceVo = new MobileNetWorkVO();
        List<MobileNetWorkVO> childUnitServiceList = new ArrayList<MobileNetWorkVO>();
        unitServiceVo.setTitle("企业办事");
        unitServiceVo.setUrl("http://www.yjbs.gov.cn/index2.jspx?service=c");
        baseContentEOs = baseContentService.getWorkGuidXYContent(siteId, 4294859L);
        for (BaseContentEO eo : baseContentEOs) {
            MobileNetWorkVO mobileNetWorkVO = new MobileNetWorkVO();
            mobileNetWorkVO.setTitle(eo.getTitle());
            mobileNetWorkVO.setUrl(eo.getRedirectLink());
            childUnitServiceList.add(mobileNetWorkVO);
        }
        unitServiceVo.setMobileNetWorkVOs(childUnitServiceList);
        levelList.add(unitServiceVo);
        level.setMobileNetWorkVOs(levelList);
        mobileVos.add(level);
        //部门服务
        MobileNetWorkVO organServiceVo = new MobileNetWorkVO();
        List<MobileNetWorkVO> childOrganServiceList = new ArrayList<MobileNetWorkVO>();
        organServiceVo.setTitle("部门服务");

        baseContentEOs = baseContentService.getWorkGuidXYContent(siteId, 4294885L);
        for (BaseContentEO eo : baseContentEOs) {
            MobileNetWorkVO mobileNetWorkVO = new MobileNetWorkVO();
            mobileNetWorkVO.setTitle(eo.getTitle());
            mobileNetWorkVO.setUrl(eo.getRedirectLink());
            childOrganServiceList.add(mobileNetWorkVO);
        }
        organServiceVo.setMobileNetWorkVOs(childOrganServiceList);
        mobileVos.add(organServiceVo);
        //便民查询
        MobileNetWorkVO queryServiceVo = new MobileNetWorkVO();
        List<MobileNetWorkVO> childQueryServiceList = new ArrayList<MobileNetWorkVO>();
        queryServiceVo.setTitle("便民查询");
        baseContentEOs = baseContentService.getWorkGuidXYContent(siteId, 4294847L);
        for (BaseContentEO eo : baseContentEOs) {
            MobileNetWorkVO mobileNetWorkVO = new MobileNetWorkVO();
            mobileNetWorkVO.setTitle(eo.getTitle());
            mobileNetWorkVO.setUrl(eo.getRedirectLink());
            childQueryServiceList.add(mobileNetWorkVO);
        }

        queryServiceVo.setMobileNetWorkVOs(childQueryServiceList);
        mobileVos.add(queryServiceVo);
        return getObject(mobileVos);
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("GBK")));
            String jsonText = readAll(rd);
            JSONObject json = JSONObject.parseObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    /**
     * 服务配置信息
     *
     * @param siteId
     * @return
     */
    @RequestMapping(value = "getInteractListXY")
    @ResponseBody
    public Object getInteractListXY(String prUrl, Integer pageSize, Integer areaCode, String sType, Integer currPage, Integer count, Long siteId, Long columnId) {
        List<MobileInteractVO> mobileVos = new ArrayList<MobileInteractVO>();
        //信件处理
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        MobileInteractVO emailVo = new MobileInteractVO();
        emailVo.setTheme("信件处理");
        JSONObject obj = null;
        List<MobileInteractVO> chirldEailVO = new ArrayList<MobileInteractVO>();
        try {
            obj = readJsonFromUrl(prUrl + "?areaCode=" + areaCode + "&sType=" + sType + "&currPage=" + currPage + "&pageSize=" + pageSize);
            JSONArray mails = obj.getJSONArray("mails");
            JSONArray referStat = obj.getJSONArray("referStat");
            JSONObject jsonObject = referStat.getJSONObject(0);
            emailVo.setDealingRefer((Integer) jsonObject.get("dealingRefer"));
            emailVo.setDealRefer((Integer) jsonObject.get("dealRefer"));
            emailVo.setTotalRefer((Integer) jsonObject.get("totalRefer"));
            for (int i = 0; i < mails.size(); i++) {
                jsonObject = mails.getJSONObject(i);
                MobileInteractVO chirldEmil = new MobileInteractVO();
                chirldEmil.setTheme(jsonObject.getString("theme"));
                chirldEmil.setUrl("http://59.39.89.100/wcm/govrefer2/yxpub/yx_content.html?sn=" + jsonObject.getString("id"));
                chirldEmil.setStatus(jsonObject.getString("status"));
                chirldEmil.setDeptName(jsonObject.getString("deptName"));
                chirldEmil.setSunmitDate(format.format(jsonObject.getDate("sunmitDate")));
                chirldEailVO.add(chirldEmil);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //咨询,建议投诉按钮
        List<BaseContentEO> baseContentEOs = baseContentService.getWorkGuidXYContent(siteId, 4296481L);
        for (BaseContentEO eo : baseContentEOs) {
            MobileInteractVO chirldEmil = new MobileInteractVO();
            chirldEmil.setTheme(eo.getTitle());
            chirldEmil.setUrl(eo.getRedirectLink());
            chirldEailVO.add(chirldEmil);
        }
        emailVo.setMobileInteractVO(chirldEailVO);
        mobileVos.add(emailVo);
        //信箱
        MobileInteractVO unitVo = new MobileInteractVO();
        unitVo.setTheme("单位信箱");
        List<MobileInteractVO> chirldUnitVO = new ArrayList<MobileInteractVO>();
        //咨询,建议投诉按钮
        baseContentEOs = baseContentService.getWorkGuidXYContent(siteId, 4296522L);
        for (BaseContentEO eo : baseContentEOs) {
            MobileInteractVO chirldUnit = new MobileInteractVO();
            chirldUnit.setTheme(eo.getTitle());
            chirldUnit.setUrl(eo.getRedirectLink());
            chirldUnitVO.add(chirldUnit);
        }
        unitVo.setMobileInteractVO(chirldUnitVO);
        mobileVos.add(unitVo);
        //征集
        MobileInteractVO collectVo = new MobileInteractVO();
        collectVo.setTheme("民意征集");
        QueryVO queryVO = new QueryVO();
        queryVO.setPageSize(pageSize);
        queryVO.setSiteId(siteId);
        queryVO.setColumnId(columnId);
        List<MobileInteractVO> chirldCollectVO = getCollectInfoPageListXY(queryVO);
        collectVo.setMobileInteractVO(chirldCollectVO);
        mobileVos.add(collectVo);
        return getObject(mobileVos);
    }


    /**
     * 新闻列表
     *
     * @param vo
     * @return
     */
    @RequestMapping(value = "getArticleNewsPageList")
    @ResponseBody
    public Object getArticleNewsPageList(ContentPageVO vo) {
        List<MobileResultVO> mobileVos = new ArrayList<MobileResultVO>();
        Pagination page = baseContentService.getPage(vo);
        if (null != page) {
            List<BaseContentEO> contentEOs = (List<BaseContentEO>) page.getData();
            if (null != contentEOs) {
                for (BaseContentEO eo : contentEOs) {
                    MobileResultVO resultVO = new MobileResultVO();
                    resultVO.setTitle(eo.getTitle());
                    if (null != eo.getRedirectLink()) {
                        resultVO.setUrl(eo.getRedirectLink());
                    } else {
                        resultVO.setUrl(getSiteUrl(eo.getColumnId(), null) + "/mobile/articleNews?contentId=" + eo.getId());
                    }
                    resultVO.setDate(eo.getPublishDate());
                    mobileVos.add(resultVO);
                }

            }
        }
        page.setData(mobileVos);
        return getObject(page);
    }

    /**
     * 网上办事内容
     *
     * @param columnId
     * @return
     */
    @RequestMapping(value = "getAllColumnGuides")
    @ResponseBody
    public List<MobileNetWorkVO> getAllColumnGuides(Long siteId, Long columnId) {
        List<MobileNetWorkVO> mobileVos = new ArrayList<MobileNetWorkVO>();
        List<ColumnMgrEO> mgrs = columnConfigService.getChildColumn(columnId, new int[]{0});
        List<Long> cids = new ArrayList<Long>();
        if (null != mgrs) {
            for (ColumnMgrEO mgr : mgrs) {
                if (mgr.getIsParent() == 0) {
                    cids.add(mgr.getIndicatorId());
                }
            }
            ParamDto paramDto = new ParamDto();
            paramDto.setSiteId(siteId);
            Pagination page = workGuideService.getPageEOs(paramDto, cids, null);
            List<CmsWorkGuideEO> guides = (List<CmsWorkGuideEO>) page.getData();
            for (CmsWorkGuideEO eo : guides) {
                MobileNetWorkVO vo = new MobileNetWorkVO();
                vo.setId(eo.getId());
                vo.setTitle(eo.getName());
                vo.setUrl(getSiteUrl(null, eo.getSiteId()) + "/mobile/getWorkGuidesDetail?id=" + eo.getId());
                vo.setDate(dateFormat(eo.getCreateDate()));
                mobileVos.add(vo);
            }
        }

        return mobileVos;
    }

    /**
     * 网上办事内容
     *
     * @param paramDto
     * @return
     */
    @RequestMapping(value = "getWorkGuides")
    @ResponseBody
    public Object getWorkGuides(ParamDto paramDto) {
        List<Long> cids = new ArrayList<Long>();
        cids.add(paramDto.getColumnId());
        Pagination page = workGuideService.getPageEOs(paramDto, cids, null);
        List<CmsWorkGuideEO> list = (List<CmsWorkGuideEO>) page.getData();
        List<MobileNetWorkVO> mobileVos = new ArrayList<MobileNetWorkVO>();
        for (CmsWorkGuideEO eo : list) {
            MobileNetWorkVO vo = new MobileNetWorkVO();
            vo.setId(eo.getId());
            vo.setTitle(eo.getName());
            // vo.setUrl(ColumnUtil.getSiteUrl(null, eo.getSiteId()) +
            // "/content/article/" + eo.getContentId() + "?guideId=" +
            // eo.getId());
            if (null == eo.getLinkUrl() || "".equals(eo.getLinkUrl())) {
                vo.setUrl(getSiteUrl(null, eo.getSiteId()) + "/mobile/getWorkGuidesDetail?id=" + eo.getId());
            } else {
                vo.setUrl(eo.getLinkUrl());
            }
            vo.setDate(dateFormat(eo.getCreateDate()));
            mobileVos.add(vo);
        }
        page.setData(mobileVos);
        return getObject(page);
    }


    /**
     * 领导之窗所有分类和人员信息
     *
     * @return
     */
    @RequestMapping("getLeaderTypes")
    @ResponseBody
    public Object getLeaderTypes(Long siteId, Long columnId) {
        if (AppUtil.isEmpty(siteId) || AppUtil.isEmpty(columnId)) {
            return ajaxErr("栏目、站点id不能为空！");
        }
        List<LeaderTypeEO> types = leaderTypeService.getList(siteId, columnId);
        List<LeaderWebVO> leaders = null;
        if (types != null && types.size() > 0) {
            leaders = new ArrayList<LeaderWebVO>();
            int i = 0;
            Map<Long, List<LeaderInfoVO>> map = MapUtil.getLeadersMap(leaderInfoService.getList(siteId, columnId));
            for (LeaderTypeEO type : types) {
                LeaderWebVO vo = new LeaderWebVO();
                vo.setLeaderTypeId(type.getLeaderTypeId());
                vo.setColumnId(columnId);
                vo.setSiteId(type.getSiteId());
                vo.setTitle(type.getTitle());
                List<LeaderInfoVO> infos = (map == null ? null : map.get(type.getLeaderTypeId()));
                if (infos != null && infos.size() > 0) {
                    for (LeaderInfoVO in : infos) {//防止數據過大 至null
                        in.setWork(null);
                        in.setJobResume(null);
                        if (!StringUtils.isEmpty(in.getPicUrl())) {
                            in.setPicUrl(PathUtil.getUrl(in.getPicUrl()));
                        }
                    }
                    vo.setLeaderInfos(infos);
                }
                leaders.add(vo);
            }
        }
        return getObject(leaders);
    }

    /**
     * 领导之窗人员信息
     *
     * @return
     */
    @RequestMapping("getLeaderInfo")
    @ResponseBody
    public Object getLeaderInfo(Long leaderId, SolrPageQueryVO vo) {
        if (AppUtil.isEmpty(leaderId) || AppUtil.isEmpty(vo.getSiteId())) {
            return ajaxErr("leaderId不能为空！");
        }

        //返回领导详细信息
        LeaderInfoVO lvo = leaderInfoService.getLeaderInfoVO(leaderId);
        if (lvo != null && !StringUtils.isEmpty(lvo.getPicUrl())) {
            lvo.setPicUrl(PathUtil.getUrl(lvo.getPicUrl()));
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("leaderInfo", lvo);

        //返回调用领导活动的栏目IDS
        String ids = mobileLeaderInfoConfigList(vo.getSiteId());
        map.put("columnIds", ids);

        //返回领导活动新闻列表
        vo.setSiteId(vo.getSiteId());
        vo.setColumnIds(ids);
        vo.setKeywords(lvo.getName());
        vo.setPageSize(vo.getPageSize());
        vo.setPageIndex(vo.getPageIndex());
        map.put("listInfo", getSearchList(vo));

        return getObject(map);
    }

    /**
     * 手机端领导之窗-新闻调用
     *
     * @return
     */
    public String mobileLeaderInfoConfigList(Long siteId) {
        if (AppUtil.isEmpty(siteId)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目、站点id不能为空");
        }
        List<MobileConfigEO> configList = mobileConfigService.getMobileConfigList(siteId);
        StringBuilder ids = new StringBuilder();
        for (MobileConfigEO eo : configList) {
            if (eo.getType() != null && eo.getType().equals("leaderInfo")) {
                ids.append(eo.getIndicatorId()).append(",");
            }
        }
        if (!AppUtil.isEmpty(ids)) {
            ids.deleteCharAt(ids.length() - 1);
        }
        return ids.toString();
    }


    /**
     * 网上办事详细信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "getWorkGuidesDetail")
    public String getWorkGuidesDetail(Long id, ModelMap map) {
        CmsWorkGuideEO eo = workGuideService.getEntity(CmsWorkGuideEO.class, id);
        map.put("eo", eo);
        return "/mobile/workguide/index";
    }

    /**
     * 在线访谈
     *
     * @param query
     * @return
     */
    @RequestMapping("getInterviewPage")
    @ResponseBody
    public Object getInterviewPage(QueryVO query) {
        return getInterviewPageList(query);
    }

    /**
     * 调查管理
     *
     * @param query
     * @return
     */
    @RequestMapping("getSurveyThemePage")
    @ResponseBody
    public Object getSurveyThemePage(QueryVO query) {
        return getSurveyThemePageList(query);
    }

    /**
     * 民意征集
     *
     * @param query
     * @return
     */
    @RequestMapping("getCollectInfoPage")
    @ResponseBody
    public Object getCollectInfoPage(QueryVO query) {
        return getCollectInfoPageList(query);
    }

    /**
     * 获取在线访谈信息
     *
     * @param interviewId
     * @param m
     * @return
     */
    @RequestMapping("getInterviewInfo")
    public String getInterviewInfo(Long interviewId, ModelMap m) {
        InterviewInfoVO interviewInfo = interviewInfoService.getInterviewInfoVO(interviewId);
        ColumnTypeConfigVO conVO = ModelConfigUtil.getCongfigVO(interviewInfo.getColumnId(), interviewInfo.getSiteId());
        m.put("conVO", conVO);
        m.put("interview", interviewInfo);
        return "/mobile/interview/interview";
    }

    /**
     * 异步获取调查管理
     *
     * @param themeId
     * @return
     */
    @RequestMapping("getSurveyThemeVO")
    @ResponseBody
    public Object getSurveyThemeVO(Long themeId) {
        SurveyThemeVO surveyTheme = surveyThemeService.getSurveyThemeVO(themeId);
        if (surveyTheme != null && surveyTheme.getThemeId() != null) {
            List<SurveyQuestionEO> questions = surveyQuestionService.getListByThemeId(surveyTheme.getThemeId());
            if (questions != null && questions.size() > 0) {
                Map<Long, List<SurveyOptionsEO>> optionsMap = MapUtil.getOptionsMap(surveyOptionsService.getListByThemeId(surveyTheme.getThemeId()));
                Map<Long, List<SurveyReplyEO>> replysMap =
                        MapUtil.getReplysMap(surveyReplyService.getListByThemeId(surveyTheme.getThemeId(), SurveyReplyEO.Status.Yes.getStatus()));
                for (SurveyQuestionEO sq : questions) {
                    if (sq.getOptions() == 3) {
                        List<SurveyReplyEO> replysList = (replysMap == null ? null : replysMap.get(sq.getQuestionId()));
                        sq.setReplys(replysList);
                    } else {
                        List<SurveyOptionsEO> options = (optionsMap == null ? null : optionsMap.get(sq.getQuestionId()));
                        sq.setOptionsList(options);
                    }
                }
                surveyTheme.setQuestions(questions);
            }
            if (surveyTheme.getStartTime() != null && surveyTheme.getEndTime() != null) {
                surveyTheme.setIsTimeOut(TimeOutUtil.getTimeOut(surveyTheme.getStartTime(), surveyTheme.getEndTime()));
            }
        }
        return getObject(surveyTheme);
    }

    /**
     * 获取调查管理
     *
     * @param themeId
     * @param m
     * @return
     */
    @RequestMapping("getSurveyTheme")
    public String getSurveyTheme(Long themeId, ModelMap m) {
        SurveyThemeVO surveyTheme = surveyThemeService.getSurveyThemeVO(themeId);
        if (surveyTheme != null && surveyTheme.getThemeId() != null) {
            List<SurveyQuestionEO> questions = surveyQuestionService.getListByThemeId(surveyTheme.getThemeId());
            if (questions != null && questions.size() > 0) {
                Map<Long, List<SurveyOptionsEO>> optionsMap = MapUtil.getOptionsMap(surveyOptionsService.getListByThemeId(surveyTheme.getThemeId()));
                Map<Long, List<SurveyReplyEO>> replysMap =
                        MapUtil.getReplysMap(surveyReplyService.getListByThemeId(surveyTheme.getThemeId(), SurveyReplyEO.Status.Yes.getStatus()));
                for (SurveyQuestionEO sq : questions) {
                    if (sq.getOptions() == 3) {
                        List<SurveyReplyEO> replysList = (replysMap == null ? null : replysMap.get(sq.getQuestionId()));
                        sq.setReplys(replysList);
                    } else {
                        List<SurveyOptionsEO> options = (optionsMap == null ? null : optionsMap.get(sq.getQuestionId()));
                        sq.setOptionsList(options);
                    }
                }
                surveyTheme.setQuestions(questions);
            }
            if (surveyTheme.getStartTime() != null && surveyTheme.getEndTime() != null) {
                surveyTheme.setIsTimeOut(TimeOutUtil.getTimeOut(surveyTheme.getStartTime(), surveyTheme.getEndTime()));
            }
        }
        m.put("surveyTheme", surveyTheme);
        return "/mobile/survey/surveytheme";
    }

    /**
     * 获取民意征集信息
     *
     * @param infoId
     * @param m
     * @return
     */
    @RequestMapping("getCollectInfo")
    public String getCollectInfo(Long infoId, ModelMap m) {
        CollectInfoVO collectInfo = collectInfoService.getCollectInfoVO(infoId);
        if (collectInfo != null && collectInfo.getStartTime() != null && collectInfo.getEndTime() != null) {
            collectInfo.setIsTimeOut(TimeOutUtil.getTimeOut(collectInfo.getStartTime(), collectInfo.getEndTime()));
        }
        ColumnTypeConfigVO conVO = ModelConfigUtil.getCongfigVO(collectInfo.getColumnId(), collectInfo.getSiteId());
        m.put("conVO", conVO);
        m.put("collectInfo", collectInfo);
        return "/mobile/collectInfo/collectInfo";
    }

    @RequestMapping("getPetitionPage")
    @ResponseBody
    public Object getPetitionPage(PetitionQueryVO queryVO) {
        if (queryVO.getPageIndex() == null) {
            queryVO.setPageIndex(0L);
        }
        if (queryVO.getPageSize() == null) {
            queryVO.setPageIndex(10L);
        }
        queryVO.setIsFront(1);
        Pagination page = petitionService.getPage(queryVO);
        if (page != null && page.getData() != null && page.getData().size() > 0) {
            List<PetitionPageVO> list = (List<PetitionPageVO>) page.getData();
            for (PetitionPageVO vo : list) {
                vo.setUri(getSiteUrl(null, vo.getSiteId()) + "/mobile/getPetitionShow?id=" + vo.getId());
            }
        }
        return getObject(page);
    }

    @RequestMapping("getPetitionShow")
    public String getPetitionShow(Long id, Model model) {
        if (id == null) {
            return "ID为空";
        }
        OnlinePetitionVO vo = petitionService.getVO(id);
        if (vo != null && vo.getRecUnitId() != null) {
            OrganEO eo = CacheHandler.getEntity(OrganEO.class, vo.getRecUnitId());
            if (eo != null) {
                vo.setRecUnitName(eo.getName());
            }
        }
        List<DataDictVO> plist = DictItemCache.get("petition_purpose");
        List<DataDictVO> clist = DictItemCache.get("petition_category");
        model.addAttribute("pList", plist);
        model.addAttribute("cList", clist);
        model.addAttribute("l", vo);

        return "/mobile/petition/petition_show";
    }

    @RequestMapping("messageBoardShow")
    public String messageBoardShow(Long id, Model model) {
        if (id == null) {
            return "ID为空";
        }
        MessageBoardEditVO editVO = messageBoardService.getVO(id);
        if (editVO != null && !StringUtils.isEmpty(editVO.getClassCode())) {
            DataDictVO vo = DataDictionaryUtil.getItem("petition_purpose", editVO.getClassCode());
            editVO.setClassName(vo.getKey());
        }
        if (editVO.getDealStatus() != null && (editVO.getDealStatus().equals("handled") || editVO.getDealStatus().equals("replyed"))) {
            List<MessageBoardReplyVO> replyEOs = replyService.getAllDealReply(editVO.getId());
            model.addAttribute("replyEOs", replyEOs);
        }
        model.addAttribute("eo", editVO);
        return "/mobile/messageBoard/messageBoard_info";
    }

    @RequestMapping("guestBookShow")
    public String guestBookShow(Long id, Model model) {
        if (id == null) {
            return "ID为空";
        }
        GuestBookEditVO editVO = guestBookService.getVO(id);
        if (editVO != null && !StringUtils.isEmpty(editVO.getClassCode())) {
            DataDictVO vo = DataDictionaryUtil.getItem("petition_purpose", editVO.getClassCode());
            editVO.setClassName(vo.getKey());
        }
        model.addAttribute("eo", editVO);
        return "/mobile/guestbook/guestbook_info";
    }

    /**
     * 获取栏目文章分页
     *
     * @param contentPageVO
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("/column/{siteId}/{columnId}")
    public Object getPage(@PathVariable Long siteId, @PathVariable Long columnId, ContentPageVO contentPageVO) {
        List<ColumnMgrEO> columnMgrList = new ArrayList<ColumnMgrEO>();
        List<MobileDataVO> navigation = new ArrayList<MobileDataVO>();
        contentPageVO.setSiteId(siteId);
        contentPageVO.setColumnId(columnId);
        Pagination page = null;
        if (!AppUtil.isEmpty(contentPageVO.getTypeCode()) && BaseContentEO.TypeCode.guestBook.toString().equals(contentPageVO.getTypeCode())) {
            if (contentPageVO.getOrderType() == null) {
                contentPageVO.setOrderType(1);
            }
            if (contentPageVO.getIsReply() == null) {
                contentPageVO.setIsReply(1);
            } else if (contentPageVO.getIsReply() == 0) {
                contentPageVO.setIsReply(null);
            }
            contentPageVO.setSiteId(siteId);
            contentPageVO.setColumnId(columnId);
            page = guestBookService.getMobilePage2(contentPageVO);
        } else if (!AppUtil.isEmpty(contentPageVO.getTypeCode()) && BaseContentEO.TypeCode.messageBoard.toString().equals(contentPageVO.getTypeCode())) {
            if (contentPageVO.getIsReply() == null) {
                contentPageVO.setIsReply(1);
            } else if (contentPageVO.getIsReply() == 0) {
                contentPageVO.setIsReply(null);
            }
            contentPageVO.setSiteId(siteId);
            contentPageVO.setColumnId(columnId);
            page = messageBoardService.getMobilePage2(contentPageVO);
        } else {

            columnMgrList = configService.getColumnByPId(columnId);
            if (columnMgrList != null && columnMgrList.size() > 0) {
    /*                columnIds = new Long[columnMgrList.size()];
                    for(int i=0 ; i < columnMgrList.size(); i++){
                        columnIds[i] =  columnMgrList.get(i).getIndicatorId();
                    }*/
                navigation = mobileNavigationPartent(columnMgrList);
                return getObject(navigation);
            }

            contentPageVO.setColumnId(columnId);

            page = baseContentService.getPageByMobile(contentPageVO);
        }
        List<MobileDataVO> dataList = new ArrayList<MobileDataVO>();
        if (page != null && page.getData() != null && page.getData().size() > 0) {
            List<BaseContentEO> list = (List<BaseContentEO>) page.getData();
            for (BaseContentEO eo : list) {
                MobileDataVO vo = new MobileDataVO();
                vo.setId(eo.getId());
                vo.setName(eo.getTitle());
                if (!AppUtil.isEmpty(eo.getImageLink())) {
                    vo.setImg(PathUtil.getUrl(eo.getImageLink()));
                }
                if ("videoNews".equals(eo.getTypeCode())) {
                    vo.setUrl(getSiteUrl(null, eo.getSiteId()) + "/mobile/getVideoEO?contentId=" + eo.getId());
                } else if ("articleNews".equals(eo.getTypeCode()) || "pictureNews".equals(eo.getTypeCode())) {
                    if (!AppUtil.isEmpty(eo.getRedirectLink())) {
                        if (eo.getRedirectLink().contains("http") || eo.getRedirectLink().contains("ftp")) {
                            vo.setUrl(eo.getRedirectLink());
                        } else {
                            vo.setUrl(getSiteUrl(null, eo.getSiteId()) + eo.getRedirectLink());
                        }
                    } else {
                        vo.setUrl(getSiteUrl(null, eo.getSiteId()) + "/mobile/articleNews?contentId=" + eo.getId());
                    }
                } else if ("interviewInfo".equals(eo.getTypeCode())) {
                    InterviewInfoEO interviewInfoEO = interviewInfoService.getInterviewInfoByContentId(eo.getId());
                    vo.setUrl(ColumnUtil.getSiteUrl(null, eo.getSiteId()) + "/mobile/getInterviewInfo?interviewId=" + interviewInfoEO.getInterviewId());
                } else if ("survey".equals(eo.getTypeCode())) {
                    SurveyThemeEO surveyThemeEO = surveyThemeService.getSurveyThemeByContentId(eo.getId());
                    vo.setUrl(ColumnUtil.getSiteUrl(null, eo.getSiteId()) + "/mobile/getSurveyTheme?themeId=" + surveyThemeEO.getThemeId());
                } else if ("guestBook".equals(eo.getTypeCode())) {
                    // GuestBookEO guestBookEO =
                    // guestBookService.getGuestBookByContentId(eo.getId());
                    vo.setUrl(ColumnUtil.getSiteUrl(null, eo.getSiteId()) + "/mobile/guestBookShow?id=" + eo.getId());
                } else if ("messageBoard".equals(eo.getTypeCode())) {
                    // GuestBookEO guestBookEO =
                    // guestBookService.getGuestBookByContentId(eo.getId());
                    vo.setUrl(ColumnUtil.getSiteUrl(null, eo.getSiteId()) + "/mobile/messageBoardShow?id=" + eo.getId());
                } else if ("collectInfo".equals(eo.getTypeCode())) {
                    CollectInfoEO collectInfoEO = collectInfoService.getCollectInfoByContentId(eo.getId());
                    vo.setUrl(ColumnUtil.getSiteUrl(null, eo.getSiteId()) + "/mobile/getCollectInfo?infoId=" + collectInfoEO.getCollectInfoId());
                }
                vo.setType(eo.getTypeCode());
                vo.setDate(eo.getCreateDate());
                dataList.add(vo);
            }
            page.setData(dataList);
        }
        return getObject(page);
    }

    /**
     * 获取文章详细信息
     *
     * @param contentId
     * @return
     * @author fangtinghua
     */
    @RequestMapping("/articleNews")
    public String getContentDetail(Long contentId, ModelMap modelMap) {
        BaseContentEO baseContentEO = CacheHandler.getEntity(BaseContentEO.class, contentId);
        String articleContent = "";
        try {
            articleContent = MongoUtil.queryById(baseContentEO.getId());
            if (OnlineSessionUtil.isPhoneDevice()) {// 手机设备
                baseContentEO.setArticle(VideoUtil.processArticle(articleContent));
            } else {
                baseContentEO.setArticle(articleContent);
            }
        } catch (GenerateException e) {
            e.printStackTrace();
        }
        modelMap.put("baseContentEO", baseContentEO);
        return "/mobile/content/detail";
    }

    @RequestMapping("getGuestbookUnit")
    @ResponseBody
    public Object getGuestbookUnit(Long columnId, Long siteId) {
        List<ContentModelParaVO> list = ModelConfigUtil.getParam(columnId, siteId, null);
        return getObject(list);
    }

    /**
     * 获取公开指南
     *
     * @param queryVO
     * @return
     * @author fangtinghua
     */
    @RequestMapping(value = "getPublicGuide")
    public Object getPublicGuide(PublicContentQueryVO queryVO, ModelMap m) {
        if (queryVO.getSiteId() == null) {
            return ajaxParamsErr("siteId不能为空！");
        }
        if (queryVO.getOrganId() == null) {
            return ajaxParamsErr("organId不能为空！");
        }
        queryVO.setType(PublicContentEO.Type.PUBLIC_GUIDE.toString());
        PublicContentVO publicContentInfo = publicContentService.getPublicContent(queryVO);
        m.put("publicContentInfo", publicContentInfo);
        return "/mobile/publicguide/publicGuide";
    }

    /**
     * 获取公开年报
     *
     * @param queryVO
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getPublicAnnualReport")
    public Object getPublicAnnualReport(PublicContentQueryVO queryVO) {
        if (queryVO.getSiteId() == null) {
            return ajaxParamsErr("siteId不能为空！");
        }
        if (queryVO.getOrganId() == null) {
            return ajaxParamsErr("organId不能为空！");
        }
        queryVO.setType(PublicContentEO.Type.PUBLIC_ANNUAL_REPORT.toString());
        queryVO.setIsPublish(1);
        return getObject(publicContentService.getPagination(queryVO));
    }

    /**
     * 获取公开年报
     *
     * @param id
     * @param m
     * @return
     */
    @RequestMapping("getPublicAnnualReportInfo")
    public String getPublicAnnualReportInfo(Long id, ModelMap m) {
        PublicContentVO publicContentInfo = publicContentService.getPublicContent(id);
        m.put("publicContentInfo", publicContentInfo);
        return "/mobile/publicguide/annualReport";
    }

    /**
     * 获取公开制度分类列表
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getPublicInstitutionClass")
    public Object getPublicInstitutionClass() {
        DataDictEO dict = CacheHandler.getEntity(DataDictEO.class, CacheGroup.CMS_CODE, PublicContentEO.PUBLIC_ITEM_INSTITUTION_CODE);
        return getObject(getList(DataDictItemEO.class, CacheGroup.CMS_PARENTID, dict.getDictId()));
    }

    /**
     * 获取公开制度
     *
     * @param queryVO
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getPublicInstitution")
    public Object getPublicInstitution(PublicContentQueryVO queryVO) {
        if (queryVO.getSiteId() == null) {
            return ajaxParamsErr("siteId不能为空！");
        }
        if (queryVO.getOrganId() == null) {
            return ajaxParamsErr("organId不能为空！");
        }
        if (queryVO.getCatId() == null) {
            return ajaxParamsErr("catId不能为空！");
        }
        queryVO.setType(PublicContentEO.Type.PUBLIC_INSTITUTION.toString());
        return getObject(publicContentService.getPagination(queryVO));
    }

    /**
     * 获取公开制度详细信息
     *
     * @param id
     * @param m
     * @return
     */
    @RequestMapping("getPublicInstitutionInfo")
    public String getPublicInstitutionInfo(Long id, ModelMap m) {
        PublicContentVO publicContentInfo = publicContentService.getPublicContentInfo(id);
        if (null != publicContentInfo && null != publicContentInfo.getContentId()) {
            String article = MongoUtil.queryById(publicContentInfo.getContentId());
            if (!AppUtil.isEmpty(article)) {
                publicContentInfo.setContent(article);
            }
        }
        m.put("publicContentInfo", publicContentInfo);
        return "/mobile/publicguide/annualReport";
    }

    /**
     * 根据组配分类列表获取单位列表
     *
     * @param catId
     * @return
     */
    @ResponseBody
    @RequestMapping("getOrganListByCatId")
    public Object getOrganListByCatId(Long siteId, Long catId) {
        if (null == siteId) {
            return ajaxParamsErr("siteId不能为空！");
        }
        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, siteId);
        if (null == siteConfigEO) {
            return ajaxParamsErr("站点配置信息不存在！");
        }
        List<OrganEO> list = organService.getPublicOrgans(Long.valueOf(siteConfigEO.getUnitIds()));
        if (null != catId && null != list && !list.isEmpty()) {
            for (Iterator<OrganEO> it = list.iterator(); it.hasNext(); ) {
                OrganEO organEO = it.next();
                OrganConfigEO organConfigEO = CacheHandler.getEntity(OrganConfigEO.class, CacheGroup.CMS_ORGAN_ID, organEO.getOrganId());
                if (null == organConfigEO || !catId.equals(organConfigEO.getCatId())) {
                    it.remove();// 删除和查询不符的
                }
            }
        }
        return getObject(list);
    }

    /**
     * 获取单位信息
     *
     * @param parentId
     * @return
     */
    @RequestMapping(value = "getOrgans")
    @ResponseBody
    public Object getOrgans(Long parentId) {
        return getObject(organService.getOrgans(parentId, OrganEO.Type.Organ.toString()));
    }

    private String dateFormat(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(date);
        return str;
    }

    @RequestMapping(value = "getVideoPage")
    @ResponseBody
    public Object getVideoPage(Long columnId, Long siteId) {
        if (columnId == null || siteId == null) {
            return ajaxErr("参数出错");
        }
        ContentPageVO pageVO = new ContentPageVO();
        pageVO.setColumnId(columnId);
        pageVO.setSiteId(siteId);
        pageVO.setCondition("isPublish");
        pageVO.setStatus(1);
        Pagination page = videoNewsService.getPage(pageVO);
        if (page != null && page.getData() != null && page.getData().size() > 0) {
            List<VideoNewsVO> list = (List<VideoNewsVO>) page.getData();
            List<MobileDataVO> newList = new ArrayList<MobileDataVO>();
            for (VideoNewsVO eo : list) {
                MobileDataVO vo = new MobileDataVO();
                vo.setId(eo.getId());
                vo.setName(eo.getTitle());
                vo.setImg(PathUtil.getUrl(eo.getImageLink()));
                vo.setUrl(getSiteUrl(null, eo.getSiteId()) + "/mobile/getVideoEO?contentId=" + eo.getId());
                vo.setType(eo.getTypeCode());
                vo.setDate(eo.getCreateDate());
                newList.add(vo);
            }
            page.setData(newList);
        }
        return getObject(page);
    }

    @RequestMapping(value = "getVideoEO")
    public Object getVideoEO(Long contentId, Model model) {
        VideoNewsVO vo = videoNewsService.getEntityForPublish(contentId);
        String videoPath = vo.getVideoPath();
        String videoName = vo.getVideoName();
        if (!StringUtils.isEmpty(videoName) && !videoName.equals(videoPath)) {
            if (StringUtils.isEmpty(vo.getEditor()) || vo.getEditor().equals("null")) {
                videoPath = PathUtil.getUrl(videoPath + ".mp4");
                vo.setVideoPath(videoPath);
            }
        } else {
            vo.setVideoPath(videoName);
        }

        if (!StringUtils.isEmpty(vo.getImageLink())) {
            vo.setImageLink(PathUtil.getUrl(vo.getImageLink()));
        }
        model.addAttribute("videoVO", vo);
        return "/mobile/video/detail";
    }

    @RequestMapping("getUnhandleGuest")
    @ResponseBody
    public Object getUnhandleGuest(String columnIds, Long memberId, Long siteId) {
        if (memberId == null) {
            return ajaxErr("请重新登录！");
        }
        GuestBookPageVO pageVO = new GuestBookPageVO();
        pageVO.setColumnIds(columnIds);
        pageVO.setIsReply(0);
        pageVO.setSiteId(siteId);
        pageVO.setCreateUserId(memberId);
        Pagination page = guestBookService.getMobilePage(pageVO);
        if (page != null && page.getData() != null && page.getData().size() > 0) {
            List<GuestBookEditVO> list = (List<GuestBookEditVO>) page.getData();
            for (GuestBookEditVO vo : list) {
                vo.setUri(getSiteUrl(null, siteId) + "/mobile/guestBookShow?id=" + vo.getBaseContentId());
                if (!StringUtils.isEmpty(vo.getClassCode())) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("petition_purpose", vo.getClassCode());
                    if (dictVO != null) {
                        vo.setClassName(dictVO.getKey());
                    }
                }
            }
        }
        return getObject(page);
    }

    @RequestMapping("setRead")
    @ResponseBody
    public Object setRead(Long id) {
        guestBookService.setRead(id);
        return getObject();
    }

    @RequestMapping("setMessageBoardRead")
    @ResponseBody
    public Object setMessageBoardRead(Long id) {
        messageBoardService.setRead(id);
        return getObject();
    }

    @RequestMapping("getMessageBoardConfig")
    @ResponseBody
    public Object getMessageBoardConfig(Long siteId, Long columnId) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<ContentModelParaVO> list = ModelConfigUtil.getParam(columnId, siteId, null);

        if (list != null && list.size() > 0) {
            result.put("recList", list);
            result.put("recType", list.get(0).getRecType());
        }
        List<ContentModelParaVO> list1 = ModelConfigUtil.getMessageBoardType(columnId, siteId);
        if (list1 != null && list1.size() > 0) {
            result.put("typeList", list1);
        } else {
            result.put("typeList", null);
        }
        return getObject(result);
    }
}