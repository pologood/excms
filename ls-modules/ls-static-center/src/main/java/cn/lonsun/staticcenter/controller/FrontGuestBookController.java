package cn.lonsun.staticcenter.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.filedownload.internal.service.IFileDownloadService;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.GuestBookEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardForwardEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardEditVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardForwardVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardPageVO;
import cn.lonsun.content.messageBoard.service.IMessageBoardForwardService;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.content.vo.GuestBookEditVO;
import cn.lonsun.content.vo.HitVO;
import cn.lonsun.content.vo.MessageBoardSearchVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.core.util.IpUtil;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.rbac.internal.service.IPersonService;
import cn.lonsun.site.contentModel.internal.service.IContentModelService;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.staticcenter.eo.MessageBoardCallBack;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import cn.lonsun.statistics.StatisticsQueryVO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.internal.service.IMemberService;
import cn.lonsun.system.member.vo.MemberSessionVO;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.DateUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.ModelConfigUtil;
import com.alibaba.fastjson.JSONPObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static cn.lonsun.common.util.AppUtil.getLongs;
import static cn.lonsun.util.DataDictionaryUtil.getItem;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-5-16<br/>
 */

@Controller
@RequestMapping(value = "/frontGuestBook")
public class FrontGuestBookController extends BaseController {

    @Autowired
    private IGuestBookService guestBookService;

    @Autowired
    private IBaseContentService contentService;
    @Autowired
    private IMemberService memberService;

    @Autowired
    private IMessageBoardService messageBoardService;

    @Autowired
    private IPersonService personService;

    @Autowired
    private IOrganService organService;

    @Autowired
    private IMessageBoardForwardService forwardRecordService;

    @Autowired
    private IContentModelService contModelService;

    @Resource
    private IMongoDbFileServer mongoDbFileServer;

    @Autowired
    private IFileDownloadService downloadService;

    private String page = "pageIndex";// 分页参数

    @RequestMapping(value = "saveVO")
    @ResponseBody
    public Object saveVO(GuestBookEditVO vo, String checkCode) {
        ColumnTypeConfigVO configVO = ModelConfigUtil.getCongfigVO(vo.getColumnId(), vo.getSiteId());
        MemberSessionVO memberVO = (MemberSessionVO) ContextHolderUtils.getSession().getAttribute("member");
        if (configVO != null) {
            Integer isLogin = configVO.getIsLoginGuest();
            if (isLogin != null && isLogin == 1) {
                if (memberVO == null || memberVO.getId() == null) {
                    return ajaxErr("请重新登录！");
                }
            }
        }
        if (StringUtils.isEmpty(vo.getPersonName())) {
            return ajaxErr("您的姓名为空！");
        }
        if (vo.getPersonPhone() == null) {
            return ajaxErr("联系方式为空！");
        }
        if (StringUtils.isEmpty(vo.getTitle())) {
            return ajaxErr("标题为空！");
        }
        if (StringUtils.isEmpty(vo.getGuestBookContent())) {
            return ajaxErr("内容不能为空！");
        }
        if (StringUtils.isEmpty(checkCode)) {
            return ajaxErr("验证码不能为空！");
        }
        String webCode = (String) ContextHolderUtils.getSession().getAttribute("webCode");
        if (!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())) {
            return ajaxErr("验证码不正确，请重新输入！");
        }

        BaseContentEO contentEO = new BaseContentEO();
        AppUtil.copyProperties(contentEO, vo);
        if (memberVO != null) {
            contentEO.setCreateUserId(memberVO.getId());
        }

        GuestBookEO guestBookEO = new GuestBookEO();
        AppUtil.copyProperties(guestBookEO, vo);
        Long id = contentService.saveEntity(contentEO);
        CacheHandler.saveOrUpdate(BaseContentEO.class, contentEO);
        guestBookEO.setPersonIp(IpUtil.getIpAddr(ContextHolderUtils.getRequest()));
        guestBookEO.setBaseContentId(id);
        guestBookEO.setAddDate(new Date());
        if (memberVO != null) {
            guestBookEO.setCreateUserId(memberVO.getId());
        }
        DataDictVO dictVO = DataDictionaryUtil.getItem("petition_purpose", guestBookEO.getClassCode());
        if (dictVO == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言类型未配置");
        } else {
            guestBookEO.setClassName(dictVO.getKey());
        }
        GuestBookEO eo = guestBookService.saveGuestBook(guestBookEO, vo.getSiteId(), vo.getColumnId());
        return getObject(eo);
    }

    @RequestMapping(value = "saveComment")
    @ResponseBody
    public Object saveComment(Long baseContentId, String docNum, String randomCode, String commentCode) {
        /*MemberSessionVO memberVO = (MemberSessionVO) ContextHolderUtils.getSession().getAttribute("member");
        if(memberVO==null||memberVO.getId()==null){
            return ajaxErr("请重新登录！");
        }*/
        if (StringUtils.isEmpty(docNum)) {
            return ajaxErr("查询编号不能为空！");
        }
        if (StringUtils.isEmpty(randomCode)) {
            return ajaxErr("查询密码不能为空！");
        }
        GuestBookEditVO editVO = guestBookService.getVO(baseContentId);
        if (editVO == null) {
            return ajaxErr("参数错误！");
        }
        if (!docNum.equals(editVO.getDocNum())) {
            return ajaxErr("查询编号错误！");
        }
        if (!randomCode.equals(editVO.getRandomCode())) {
            return ajaxErr("查询密码错误！");
        }
        if (StringUtils.isEmpty(commentCode)) {
            return ajaxErr("评价结果不能为空！");
        }

        GuestBookEO guestBookEO = guestBookService.getEntity(GuestBookEO.class, editVO.getId());
        if (guestBookEO == null) {
            return ajaxErr("参数错误！");
        }
        guestBookEO.setCommentCode(commentCode);
        DataDictVO dictVO = DataDictionaryUtil.getItem("guest_comment", commentCode);
        String commentName = null;
        if (dictVO != null) {
            commentName = dictVO.getKey();
        }
        //guestBookEO.setUpdateUserId(memberVO.getId());
        guestBookService.updateEntity(guestBookEO);
        return getObject(commentName);

    }


    @RequestMapping(value = "saveMessageBoardComment")
    @ResponseBody
    public Object saveMessageBoardComment(Long baseContentId, String docNum, String randomCode, String commentCode) {
        if (StringUtils.isEmpty(docNum)) {
            return ajaxErr("查询编号不能为空！");
        }
        if (StringUtils.isEmpty(randomCode)) {
            return ajaxErr("查询密码不能为空！");
        }
        MessageBoardEditVO editVO = messageBoardService.getVO(baseContentId);
        if (editVO == null) {
            return ajaxErr("参数错误！");
        }
        if (!docNum.equals(editVO.getDocNum())) {
            return ajaxErr("查询编号错误！");
        }
        if (!randomCode.equals(editVO.getRandomCode())) {
            return ajaxErr("查询密码错误！");
        }
        if (StringUtils.isEmpty(commentCode)) {
            return ajaxErr("未选择评价结果！");
        }

        MessageBoardEO messageBoardEO = messageBoardService.getEntity(MessageBoardEO.class, editVO.getId());
        if (messageBoardEO == null) {
            return ajaxErr("参数错误！");
        }
        messageBoardEO.setCommentCode(commentCode);
        DataDictVO dictVO = DataDictionaryUtil.getItem("guest_comment", commentCode);
        String commentName = null;
        if (dictVO != null) {
            commentName = dictVO.getKey();
        }
        messageBoardService.saveComment(messageBoardEO);
        return getObject(commentName);

    }

    /**
     * 后台新增留言(安徽省质量技术监督局)
     *
     * @param vo
     * @return
     */
    @RequestMapping(value = "saveMessageBoardVOAJ")
    @ResponseBody
    public Object saveMessageBoardVO(MessageBoardEditVO vo) {
        ColumnTypeConfigVO configVO = ModelConfigUtil.getCongfigVO(vo.getColumnId(), vo.getSiteId());
        MemberSessionVO memberVO = (MemberSessionVO) ContextHolderUtils.getSession().getAttribute("member");
        if (configVO != null) {
            Integer isLogin = configVO.getIsLoginGuest();
            if (isLogin != null && isLogin == 1) {
                if (memberVO == null || memberVO.getId() == null) {
                    throw new BaseRunTimeException(TipsMode.Message.toString(), "请先登录！");
                }
            }
        }
        if (StringUtils.isEmpty(vo.getTitle())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言标题为空！");
        }
        if (StringUtils.isEmpty(vo.getPersonName())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言人姓名为空！");
        }
        if (vo.getPersonPhone() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言人联系电话为空！");
        }

        final BaseContentEO contentEO = new BaseContentEO();
        AppUtil.copyProperties(contentEO, vo);
        MessageBoardEO messageBoardEO = new MessageBoardEO();
        AppUtil.copyProperties(messageBoardEO, vo);
        Long id = contentService.saveEntity(contentEO);
        CacheHandler.saveOrUpdate(BaseContentEO.class, contentEO);
        messageBoardEO.setPersonIp(IpUtil.getIpAddr(LoginPersonUtil.getRequest()));
        messageBoardEO.setBaseContentId(id);
        if (memberVO != null) {
            messageBoardEO.setCreateUserId(memberVO.getId());
        }
        messageBoardEO.setAddDate(new Date());
        DataDictVO dictVO = getItem("petition_purpose", messageBoardEO.getClassCode());
        if (dictVO == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言类型未配置");
        } else {
            messageBoardEO.setClassName(dictVO.getKey());
        }
        MessageBoardEO messageBoardEO1 = messageBoardService.saveMessageBoard(messageBoardEO, vo.getSiteId(), vo.getColumnId());

        return getObject(messageBoardEO1);
    }

    /**
     * 后台新增留言
     *
     * @param vo
     * @return
     */
    @RequestMapping(value = "saveMessageBoardVO")
    @ResponseBody
    public Object saveMessageBoardVO(MessageBoardEditVO vo, String checkCode) {
        ColumnTypeConfigVO configVO = ModelConfigUtil.getCongfigVO(vo.getColumnId(), vo.getSiteId());
        MemberSessionVO memberVO = (MemberSessionVO) ContextHolderUtils.getSession().getAttribute("member");
        if (configVO != null) {
            Integer isLogin = configVO.getIsLoginGuest();
            if (isLogin != null && isLogin == 1) {
                if (memberVO == null || memberVO.getId() == null) {
                    return ajaxErr("请重新登录！");
                }
            }
        }

        if (StringUtils.isEmpty(vo.getTitle())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言标题为空！");
        }
        if (StringUtils.isEmpty(vo.getMessageBoardContent())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言内容为空！");
        }
        if (StringUtils.isEmpty(vo.getPersonName())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言人姓名为空！");
        }
        if (StringUtils.isEmpty(vo.getPersonPhone())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言人联系电话为空！");
        }
        String webCode = (String) ContextHolderUtils.getSession().getAttribute("webCode");
        if (!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())) {
            return ajaxErr("验证码不正确，请重新输入！");
        }


        if (vo.getReceiveUnitId() != null) {
            String strId = "";
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("unitId", vo.getReceiveUnitId());
            result.put("recordStatus", PersonEO.RecordStatus.Normal.toString());
            List<PersonEO> list = personService.getEntities(PersonEO.class, result);
            if (list == null || list.size() <= 0) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), vo.getReceiveUnitName() + "下没有办理人员");
            }
            for (int j = 0; j < list.size(); j++) {
                strId += list.get(j).getUserId() + ",";
            }
            if (StringUtils.isEmpty(strId)) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), vo.getReceiveUnitName() + "下没有办理人员");
            }
        }

        final BaseContentEO contentEO = new BaseContentEO();
        AppUtil.copyProperties(contentEO, vo);
        MessageBoardEO messageBoardEO = new MessageBoardEO();
        AppUtil.copyProperties(messageBoardEO, vo);
        Long id = contentService.saveEntity(contentEO);
        CacheHandler.saveOrUpdate(BaseContentEO.class, contentEO);
        messageBoardEO.setPersonIp(IpUtil.getIpAddr(LoginPersonUtil.getRequest()));
        messageBoardEO.setBaseContentId(id);
        if (vo.getReceiveUnitId() != null) {
            messageBoardEO.setForwardCount(1);
        }
        if (memberVO != null) {
            messageBoardEO.setCreateUserId(memberVO.getId());
        }
        messageBoardEO.setAddDate(new Date());
        DataDictVO dictVO = getItem("petition_purpose", messageBoardEO.getClassCode());
        if (dictVO == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言类型未配置");
        } else {
            messageBoardEO.setClassName(dictVO.getKey());
        }
        //设置超时
        if(configVO.getLimitDay()!=null&&configVO.getLimitDay()>0){
//            Date date=new Date();
//            Calendar calendar = new GregorianCalendar();
//            calendar.setTime(date);
//            calendar.add(calendar.DATE, configVO.getLimitDay());
//            date=calendar.getTime();
//            messageBoardEO.setDueDate(date);

            Date dueDate=new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(dueDate);
            int day = 0;
            while (day<configVO.getLimitDay()) {
                if (dueDate.getDay() != 6 && dueDate.getDay() != 0){
                    day++;
                }
                dueDate.setDate(dueDate.getDate() + 1);
            }
            messageBoardEO.setDueDate(dueDate);
            messageBoardEO.setDefaultDays(configVO.getLimitDay());
        }
        MessageBoardEO messageBoardEO1 = messageBoardService.saveMessageBoard(messageBoardEO, vo.getSiteId(), vo.getColumnId());

        if (null != configVO.getRecType() && (configVO.getRecType() == 0 || configVO.getRecType() == 1)) {
            MessageBoardForwardEO forwardEO = new MessageBoardForwardEO();
            forwardEO.setMessageBoardId(vo.getId());
            forwardEO.setOperationStatus(MessageBoardForwardEO.OperationStatus.Normal.toString());
            forwardEO.setMessageBoardId(messageBoardEO1.getId());
            forwardEO.setCreateDate(new Date());
            forwardEO.setIp(IpUtil.getIpAddr(LoginPersonUtil.getRequest()));
            if (memberVO != null) {
                forwardEO.setUsername(memberVO.getName());
                forwardEO.setCreateUserId(memberVO.getId());
            }
            forwardEO.setOperationStatus(MessageBoardForwardEO.OperationStatus.Normal.toString());
            forwardEO.setRemarks("请快速办理");
            if (configVO.getRecType() == 0) {
                forwardEO.setReceiveOrganId(vo.getReceiveUnitId());
                forwardEO.setReceiveUnitName(vo.getReceiveUnitName());
            }
            if (configVO.getRecType() == 1) {
                forwardEO.setReceiveUserCode(vo.getReceiveUserCode());
                forwardEO.setReceiveUserName(vo.getReceiveUserName());
            }
            //设置超时
            if(configVO.getLimitDay()!=null&&configVO.getLimitDay()>0) {
                forwardEO.setDefaultDays(messageBoardEO.getDefaultDays());
                forwardEO.setDueDate(messageBoardEO.getDueDate());
            }
            forwardRecordService.saveEntity(forwardEO);
        }

        return getObject(messageBoardEO1);
    }

    @RequestMapping(value = "countGuestbook")
    @ResponseBody
    public Object countGuestbook(String columnIds, Long siteId) {
        Date startDate = DateUtil.getToday();
        //来信总数
        Long guestbook_count = guestBookService.countGuestbook(siteId, columnIds, null, null, null);

        //回复总数
        Long guestbook_replyCount = guestBookService.countGuestbook(siteId, columnIds, null, 1, 1);

        //今日来信
        Long guestbook_todayCount = guestBookService.countGuestbook(siteId, columnIds, startDate, null, null);

        //今日回复
        Long guestbook_todayReplyCount = guestBookService.countGuestbook(siteId, columnIds, startDate, 1, 1);

        Map<String, Long> map = new HashMap<String, Long>();
        map.put("count", guestbook_count);
        map.put("replyCount", guestbook_replyCount);
        map.put("todayCount", guestbook_todayCount);
        map.put("todayReplyCount", guestbook_todayReplyCount);

        return getObject(map);
    }

    @RequestMapping("searchByCode")
    @ResponseBody
    public Object searchByCode(String docNum, String randomCode, Long siteId, Model model) {
        if (siteId == null) {
            return "2";
        }
        GuestBookEditVO vo = guestBookService.searchEO(randomCode, docNum, siteId);
        if (vo != null) {
            if (vo.getRecType() != null) {
                if (vo.getRecType().equals(0)) {
                    if (vo.getReceiveId() != null) {
                        OrganEO organEO = CacheHandler.getEntity(OrganEO.class, vo.getReceiveId());
                        if (organEO != null) {
                            vo.setReceiveName(organEO.getName());
                        }
                    }
                } else {
                    if (!StringUtils.isEmpty(vo.getReceiveUserCode())) {
                        DataDictVO dictVO = DataDictionaryUtil.getItem("guest_book_rec_users", vo.getReceiveUserCode());
                        if (dictVO != null) {
                            vo.setReceiveUserName(dictVO.getKey());
                        }
                    }
                }
            }
        }
        return getObject(vo);

    }

    @RequestMapping(value = "listGuestbook")
    @ResponseBody
    public Object listGuestbook(String idNum, Long siteId, String columnIds, Integer isReply, Integer num) {

        String createUserId = "";
        if (!StringUtils.isEmpty(idNum)) {
            List<MemberEO> mList = memberService.getByNumber(idNum, siteId);
            if (mList != null && mList.size() > 0) {
                for (MemberEO eo : mList) {
                    createUserId += eo.getId() + ",";
                }
                createUserId = createUserId.substring(0, createUserId.length() - 1);
            }
        }
        if (StringUtils.isEmpty(createUserId)) {
            return getObject();
        }
        List<GuestBookEditVO> list = guestBookService.listGuestBook(siteId, columnIds, isReply, createUserId, num);
        return getObject(list);

    }


    @RequestMapping(value = "listMessageBoard")
    @ResponseBody
    public Object listMessageBoard(String idNum, Long siteId, String columnIds, Integer isReply, Integer num) {

        String createUserId = "";
        if (!StringUtils.isEmpty(idNum)) {
            List<MemberEO> mList = memberService.getByNumber(idNum, siteId);
            if (mList != null && mList.size() > 0) {
                for (MemberEO eo : mList) {
                    createUserId += eo.getId() + ",";
                }
                createUserId = createUserId.substring(0, createUserId.length() - 1);
            }
        }
        if (StringUtils.isEmpty(createUserId)) {
            return getObject();
        }
        List<MessageBoardEditVO> list = messageBoardService.listMessageBoard(siteId, columnIds, isReply, createUserId, num);
        if (null != list && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setLink(PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.ACRTILE.getValue(), list.get(i).getColumnId(), list.get(i).getBaseContentId()));
                List<MessageBoardForwardVO> forwardVOList = forwardRecordService.getAllUnit(list.get(i).getId());
                if (forwardVOList != null && forwardVOList.size() > 0) {
                    list.get(i).setReceiveUnitName(forwardVOList.get(0).getReceiveUnitName());
                } else {
                    list.get(i).setReceiveUnitName("待分配");
                }
            }
        }
        return getObject(list);

    }

    //获取所有组织单位
    @RequestMapping(value = "getOrganUnit")
    @ResponseBody
    public Object getOrganUnit(Long columnId, Long siteId) {
        List<ContentModelParaVO> list = contModelService.getParam(columnId, siteId, null);
        List<Long> ids = null;
        List<OrganEO> organs = null;
        if (list != null && list.size() > 0) {
            ids = new ArrayList<Long>();
            for (ContentModelParaVO vo : list) {
                if (vo.getRecUnitId() != null) {
                    ids.add(vo.getRecUnitId());
                }
            }
            if (ids != null && ids.size() > 0) {
                organs = organService.getEntities(OrganEO.class, ids);
            }
        }
        return getObject(organs);

    }


    @RequestMapping("getHit")
    @ResponseBody
    public Object getHit(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return getObject();
        }
        Long[] idArr = getLongs(ids, ",");
        List<BaseContentEO> list = contentService.getEntities(BaseContentEO.class, idArr);
        if (list != null && list.size() > 0) {
            List<HitVO> newList = new ArrayList<HitVO>();
            for (BaseContentEO eo : list) {
                HitVO hitVO = new HitVO();
                hitVO.setId(eo.getId());
                hitVO.setCount(eo.getHit());
                newList.add(hitVO);
            }
            return getObject(newList);
        }
        return getObject();
    }

    @RequestMapping("getGuestVO")
    @ResponseBody
    public Object getGuestVO(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return getObject();
        }
        Long[] idArr = AppUtil.getLongs(ids, ",");
        List<GuestBookEO> list = guestBookService.getEntities(GuestBookEO.class, idArr);
        if (list != null && list.size() > 0) {
            for (GuestBookEO eo : list) {
                if (!StringUtils.isEmpty(eo.getCommentCode())) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("guest_comment", eo.getCommentCode());
                    if (dictVO != null) {
                        eo.setCommentName(dictVO.getKey());
                    }
                } else {
                    eo.setCommentName("暂无评价");
                }
            }
        }
        return getObject(list);
    }

    /**
     * 下载文件
     *
     * @param response
     * @param downId
     * @param filePath
     */
    @RequestMapping("downloadFile")
    public void downloadFile(HttpServletResponse response, Long downId, String filePath) {
        downloadService.addCount(downId);
        mongoDbFileServer.downloadFile(response, filePath, null);
    }


    /**
     * queryCallBackMessageBoard
     * 返回callback接口
     */
    @RequestMapping("queryCallBackMessageBoard")
    @ResponseBody
    public Object queryCallBackMessageBoard(MessageBoardCallBack messageBoardCallBack,HttpServletRequest  request,HttpServletResponse response) {

        // 判断jsonp请求
        String callback = request.getParameter("callback");

        MessageBoardPageVO pageVO = new MessageBoardPageVO();
        pageVO.setSiteId(messageBoardCallBack.getSiteId());
        pageVO.setColumnId(messageBoardCallBack.getColumnId());
        pageVO.setReceiveUnitId(messageBoardCallBack.getOrganId());
        pageVO.setPageSize(messageBoardCallBack.getPageSize());
        pageVO.setPageIndex(messageBoardCallBack.getPageIndex());

        Object obj = messageBoardService.getCallbackPage(pageVO);
        if (StringUtils.isNotEmpty(callback)) {
            response.setContentType("application/json;charset=utf-8");
            JSONPObject function = new JSONPObject(callback);
            function.addParameter(obj);
            return function;
        }
        return obj;
    }

    /**
     * queryCallBackMessageBoard
     * 返回callback接口
     */
    @RequestMapping("queryCallBackMessageBoardInfo")
    @ResponseBody
    public Object queryCallBackMessageBoardInfo(Long id,HttpServletRequest  request,HttpServletResponse response) {

        // 判断jsonp请求
        String callback = request.getParameter("callback");

        MessageBoardEditVO editVO = new MessageBoardEditVO();
        BaseContentEO baseContentEO = contentService.getEntity(BaseContentEO.class,id);
        Map<String  ,Object> map   =  new HashMap<String, Object>();
        map.put("baseContentId",baseContentEO.getId());
        MessageBoardEO messageBoardEO = messageBoardService.getEntity(MessageBoardEO.class,map);
        AppUtil.copyProperties(editVO, messageBoardEO);
        editVO.setTitle(baseContentEO.getTitle());
        if (StringUtils.isNotEmpty(callback)) {
            response.setContentType("application/json;charset=utf-8");
            JSONPObject function = new JSONPObject(callback);
            function.addParameter(editVO);
            return function;
        }
        return editVO;
    }

    @RequestMapping("getTypelist")
    @ResponseBody
    public Object getTypeList(Long columnId,Long siteId){
        List<ContentModelParaVO> list = ModelConfigUtil.getMessageBoardType(columnId, siteId);
        return list;
    }

    @RequestMapping("getPageByQuery")
    @ResponseBody
    public Object getPageByQuery(MessageBoardSearchVO searchVO){
        if(!StringUtils.isEmpty(searchVO.getStartDate())){
            searchVO.setStartTime(toDate(searchVO.getStartDate()));
        }
        if(!StringUtils.isEmpty(searchVO.getEndDate())){
            searchVO.setEndTime(toDate(searchVO.getEndDate()));
        }
        Pagination page=messageBoardService.getPageByQuery(searchVO);
        return getObject(page);
    }
    private static Date toDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


}
