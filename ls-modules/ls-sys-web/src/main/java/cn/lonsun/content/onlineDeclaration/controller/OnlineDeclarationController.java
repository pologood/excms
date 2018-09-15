package cn.lonsun.content.onlineDeclaration.controller;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.onlineDeclaration.internal.entity.OnlineDeclarationEO;
import cn.lonsun.content.onlineDeclaration.internal.service.IOnlineDeclarationService;
import cn.lonsun.content.onlineDeclaration.vo.DeclaQueryVO;
import cn.lonsun.content.onlineDeclaration.vo.DeclaReplyVO;
import cn.lonsun.content.onlineDeclaration.vo.OnlineDeclarationVO;
import cn.lonsun.content.onlinepetition.OnlinePetitionController;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.system.role.internal.site.service.IRoleSiteOptService;
import cn.lonsun.util.ConcurrentUtil;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.ModelConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

import static cn.lonsun.cache.client.CacheHandler.getEntity;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-6-13<br/>
 */
@Controller
@RequestMapping("onlineDeclaration")
public class OnlineDeclarationController extends BaseController {
    @Autowired
    private IOnlineDeclarationService declarationService;

    @Autowired
    private IMongoDbFileServer mongoDbFileServer;

    @Autowired
    private IRoleSiteOptService optService;
    @Autowired
    private TaskExecutor taskExecutor;
    static Logger logger = LoggerFactory.getLogger(OnlinePetitionController.class);

    /**
     * 去往列表页面
     * @param model
     * @return
     */
    @RequestMapping("index")
    public String index(Model model,Long pageIndex){
        if(pageIndex==null){
            model.addAttribute("pageIndex",0);
        }else{
            model.addAttribute("pageIndex",pageIndex);
        }
        return "content/onlinedeclaration/list";
    }

    /**
     * 去往编辑页面
     * @param model
     * @return
     */
    @RequestMapping("edit")
    public String edit(Model model,Long columnId,Long pageIndex){
        List<ContentModelParaVO> recList= ModelConfigUtil.getParam(columnId,LoginPersonUtil.getSiteId(),null);
        Integer recType=null;
        if(recList!=null&&recList.size()>0){
            recType=recList.get(0).getRecType();
        }
        model.addAttribute("recType",recType);
        model.addAttribute("recList",recList);
        if(pageIndex==null){
            model.addAttribute("pageIndex", 0);
        }else{
            model.addAttribute("pageIndex", pageIndex);
        }
        return "content/onlinedeclaration/edit";
    }


    /**
     * 去往编辑页面(工作流专用)
     * @param model
     * @return
     */
    @RequestMapping("editDeclaration")
    public String editDeclaration(Long id,Long pageIndex,Long columnId,Model model){

        if(AppUtil.isEmpty(columnId)){
            BaseContentEO contentEO = getEntity(BaseContentEO.class, id);
            columnId = contentEO.getColumnId();
        }
        model.addAttribute("baseContentId",id);
        model.addAttribute("pageIndex",pageIndex);
        ColumnTypeConfigVO configVO=ModelConfigUtil.getCongfigVO(columnId,LoginPersonUtil.getSiteId());
        if(configVO==null){
            model.addAttribute("recType",null);
        }else{
            model.addAttribute("recType",configVO.getRecType());
        }

        List<ContentModelParaVO> statusList = ModelConfigUtil.getDealStatus(columnId, LoginPersonUtil.getSiteId());
        OnlineDeclarationVO vo=declarationService.getVO(id);
        model.addAttribute("statusList", statusList);
        model.addAttribute("declarationId",vo.getId());
        model.addAttribute("pageIndex",pageIndex);
        return "content/onlinedeclaration/editDeclaration";
    }

    /**
     * 获取分页列表
     * @param pageVO
     * @return
     */
    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(DeclaQueryVO pageVO){
        pageVO.setSiteId(LoginPersonUtil.getSiteId());
        Pagination page=declarationService.getPage(pageVO);
        if(page!=null&&page.getData()!=null&&page.getData().size()>0){
            List<OnlineDeclarationVO> list1=( List<OnlineDeclarationVO>)page.getData();
            ColumnTypeConfigVO setting=ModelConfigUtil.getCongfigVO(pageVO.getColumnId(),pageVO.getSiteId());
            Integer recType=null;
            if(setting!=null){
                if(!StringUtils.isEmpty(setting.getRecUnitIds())){
                   recType=0;
                    for(OnlineDeclarationVO vo:list1){
                        vo.setRecType(recType);
                    }
                }
            }
        }
        return page;
    }

    /**
     * 保存
     * @param vo
     * @return
     */
    @RequestMapping("saveVO")
    @ResponseBody
    public Object saveVO(OnlineDeclarationVO vo){
        vo.setCreateUnitId(LoginPersonUtil.getUnitId());
        declarationService.saveVO(vo);
        return getObject();
    }

    /**
     * 根据Id获取详细信息
     * @param id
     * @return
     */
    @RequestMapping("getVO")
    @ResponseBody
    public Object getVO(Long id){
        if(id==null){
            return getObject(new OnlineDeclarationVO());

        }
        OnlineDeclarationVO vo=declarationService.getVO(id);
        return getObject(vo);
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @RequestMapping("deleteVOs")
    @ResponseBody
    public Object deleteVOs(String ids){
        if(AppUtil.isEmpty(ids)){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "ID为空");
        }else {
            declarationService.deleteVOs(ids);
        }
        return getObject(0);
    }

    /**
     * 去往转办页面
     * @param declarationId
     * @param columnId
     * @param model
     * @return
     */
    @RequestMapping("doTransfer")
    public String doTransfer(Long declarationId,Long columnId,Model model){
        model.addAttribute("declarationId",declarationId);
        model.addAttribute("columnId",columnId);
        return "content/onlinedeclaration/transfer";
    }
    /**
     * 转办
     * @param declarationId
     * @param recUnitId
     * @param recUnitName
     * @param remark
     * @return
     */
    @RequestMapping("transfer")
    @ResponseBody
    public Object transfer(Long declarationId,Long recUnitId,String recUnitName,String remark){
        if(recUnitId==null||recUnitName==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "受理单位不能为空");
        }
        if(AppUtil.isEmpty(remark)){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "备注不能为空");
        }
        declarationService.transfer(declarationId,recUnitId,recUnitName,remark);
        OnlineDeclarationEO declarationEO=declarationService.getEntity(OnlineDeclarationEO.class,declarationId);
        final BaseContentEO contentEO= CacheHandler.getEntity(BaseContentEO.class, declarationEO.getBaseContentId());
        if(contentEO==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数出错");
        }
        String strIds="";
        if(recUnitId!=null){
            List<TreeNodeVO> list=optService.getUserAuthForColumn(recUnitId, contentEO.getColumnId());
            if(list==null||list.size()<=0){
                throw new BaseRunTimeException(TipsMode.Message.toString(), "该单位下没有办理人员");
            }
            for(int i=0;i<list.size();i++){
                strIds+=list.get(i).getUserId()+",";
            }
            if(StringUtils.isEmpty(strIds)){
                throw new BaseRunTimeException(TipsMode.Message.toString(), "该单位下没有办理人员");
            }
        }

        final String receiveIds=strIds;
        final Long userId= LoginPersonUtil.getUserId();
        //异步执行
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                logger.error("==============>  在线申报转办转办 <================");
                // 绑定session至当前线程中
                SessionFactory sessionFactory = SpringContextHolder.getBean(SessionFactory.class);
                boolean participate = ConcurrentUtil.bindHibernateSessionToThread(sessionFactory);
                sendMessge(contentEO, receiveIds, userId);
                // 关闭session
                ConcurrentUtil.closeHibernateSessionFromThread(participate, sessionFactory);
                logger.error("=============>  在线申报转办完成 <=====================");
            }
        });
        return getObject(0);
    }

    /**
     * 发送消息
     * @param eo
     * @param receiveIds
     * @param userId
     */
    private void sendMessge(BaseContentEO eo,String receiveIds,Long userId) {
        MessageSystemEO message=new MessageSystemEO();
        message.setSiteId(eo.getSiteId());
        message.setColumnId(eo.getColumnId());
        message.setMessageType(MessageSystemEO.TIP);
        message.setModeCode(BaseContentEO.TypeCode.onlineDeclaration.toString());
        message.setRecUserIds(receiveIds);
        //message.setRecOrganIds(videoEO.getCreateOrganId() + "");
        //message.setCreateOrganId(videoEO.getCreateOrganId());
        message.setCreateUserId(userId);
        // message.setLink("/videoNews/videoPlayer?id="+eo.getId());
        message.setResourceId(eo.getId());
        message.setTitle("在线申报转办");
        message.setContent(eo.getTitle());
        message.setMessageStatus(MessageSystemEO.MessageStatus.success.toString());
        MessageSender.sendMessage(message);
    }


    /**
     * 下载附件
     * @param response
     * @param attachId
     */
    @RequestMapping("downloadFile")
    public void downloadFile(HttpServletResponse response, String attachId){
        mongoDbFileServer.downloadFile(response,attachId,null);
    }

    /**
     * 去往详细页面
     * @param baseContentId
     * @param model
     * @return
     */
    @RequestMapping("doShow")
    public String doShow(Long baseContentId,Long pageIndex,Long columnId,Model model){
        model.addAttribute("baseContentId",baseContentId);
        model.addAttribute("pageIndex",pageIndex);
        ColumnTypeConfigVO configVO=ModelConfigUtil.getCongfigVO(columnId,LoginPersonUtil.getSiteId());
        if(configVO==null){
            model.addAttribute("recType",null);
        }else{
            model.addAttribute("recType",configVO.getRecType());
        }
        return "content/onlinedeclaration/show";
    }

    /**
     * 获取办理信息
     * @param declarationId
     * @return
     */
    @RequestMapping("getReplyVO")
    @ResponseBody
    public Object getReplyVO(Long declarationId) {
        if(declarationId==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "ID为空！");
        }
        OnlineDeclarationEO eo=declarationService.getEntity(OnlineDeclarationEO.class,declarationId);
        if(eo==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数错误！");
        }
        BaseContentEO contentEO=CacheHandler.getEntity(BaseContentEO.class,eo.getBaseContentId());
        if(contentEO==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数错误！");
        }
        DeclaReplyVO replyVO=new DeclaReplyVO();
        replyVO.setDeclarationId(declarationId);
        if(eo.getReplyDate()==null){
            replyVO.setReplyDate(new Date());
        }else{
            replyVO.setReplyDate(eo.getReplyDate());
        }
        replyVO.setDealStatus(eo.getDealStatus());
        ColumnTypeConfigVO configVO = ModelConfigUtil.getCongfigVO(contentEO.getColumnId(), contentEO.getSiteId());
        if (configVO != null) {
            if (!StringUtils.isEmpty(eo.getDealStatus()) && !StringUtils.isEmpty(configVO.getStatusCode())) {
                if (!configVO.getStatusCode().contains(eo.getDealStatus())) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("deal_status", eo.getDealStatus());
                    if (dictVO != null) {
                        replyVO.setStatusName(dictVO.getKey());
                    }
                }
            }
        }
        if(StringUtils.isEmpty(eo.getReplyUnitName())){
            replyVO.setReplyUnitName(LoginPersonUtil.getOrganName());
        }else{
            replyVO.setReplyUnitName(eo.getReplyUnitName());
        }
        replyVO.setReplyContent(eo.getReplyContent());
        return replyVO;
    }

    /**
     * 去往回复页面
     * @param declarationId
     * @param model
     * @return
     */
    @RequestMapping("doReply")
    public String doReply(Long declarationId,Long columnId,Long pageIndex,Model model){
        if(declarationId==null){
            return null;
        }
        List<ContentModelParaVO> statusList = ModelConfigUtil.getDealStatus(columnId, LoginPersonUtil.getSiteId());
        model.addAttribute("statusList", statusList);
        model.addAttribute("declarationId",declarationId);
        model.addAttribute("pageIndex",pageIndex);
        return "content/onlinedeclaration/reply";
    }

    /**
     * 保存回复
     * @param replyVO
     * @return
     */
    @RequestMapping("saveReply")
    @ResponseBody
    public Object saveReply(DeclaReplyVO replyVO){
        if(StringUtils.isEmpty(replyVO.getDealStatus())){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "办理状态不能为空");
        }
        if(replyVO.getReplyDate()==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "办理时间不能为空");
        }
        OnlineDeclarationEO eo=declarationService.getEntity(OnlineDeclarationEO.class,replyVO.getDeclarationId());
        if(eo==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数出错");
        }
        eo.setDealStatus(replyVO.getDealStatus());
        eo.setReplyContent(replyVO.getReplyContent());
        eo.setReplyUnitName(replyVO.getReplyUnitName());
        eo.setReplyDate(replyVO.getReplyDate());
        declarationService.updateEntity(eo);
        return getObject();
    }

}
