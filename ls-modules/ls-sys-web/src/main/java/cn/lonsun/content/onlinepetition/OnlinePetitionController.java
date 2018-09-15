package cn.lonsun.content.onlinepetition;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.onlinePetition.internal.entity.OnlinePetitionEO;
import cn.lonsun.content.onlinePetition.internal.service.IOnlinePetitionService;
import cn.lonsun.content.onlinePetition.vo.OnlinePetitionVO;
import cn.lonsun.content.onlinePetition.vo.PetitionPageVO;
import cn.lonsun.content.onlinePetition.vo.PetitionQueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.site.site.internal.cache.DictItemCache;
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

import static cn.lonsun.util.ModelConfigUtil.getParam;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-30<br/>
 */
@Controller
@RequestMapping("onlinePetition")
public class OnlinePetitionController extends BaseController {

    @Autowired
    private IOnlinePetitionService petitionService;
    @Autowired
    private IMongoDbFileServer mongoDbFileServer;

    @Autowired
    private IRoleSiteOptService optService;
    @Autowired
    private TaskExecutor taskExecutor;
    static Logger logger = LoggerFactory.getLogger(OnlinePetitionController.class);

    /**
     * 去往信访列表页
     * @param pageIndex
     * @param model
     * @return
     */
    @RequestMapping("index")
    public String index(Long pageIndex, Model model){
        if (pageIndex == null) pageIndex = 0L;
        model.addAttribute("pageIndex", pageIndex);
        return "content/onlinepetition/petition_index";
    }

    /**
     * 获取信访分页列表
     * @param pageVO
     * @return
     */
    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(PetitionQueryVO pageVO){
        pageVO.setSiteId(LoginPersonUtil.getSiteId());
        Pagination page=petitionService.getPage(pageVO);
        if(page!=null&&page.getData()!=null&&page.getData().size()>0){
            List<PetitionPageVO> list1=( List<PetitionPageVO>)page.getData();
            ColumnTypeConfigVO setting=ModelConfigUtil.getCongfigVO(pageVO.getColumnId(),pageVO.getSiteId());
            Integer recType=null;
            if(setting!=null){
                if(!StringUtils.isEmpty(setting.getRecUnitIds())){
                    recType=0;
                    for(PetitionPageVO vo:list1){
                        vo.setRecType(recType);
                    }
                }
            }
        }
        return page;
    }

    /**
     * 去往信访编辑页面
     * @param id
     * @param columnId
     * @param pageIndex
     * @param model
     * @return
     */
    @RequestMapping("edit")
    public String edit(Long id,Long columnId,Long pageIndex,Model model){
        if (id == null) {
            model.addAttribute("contentId", "");
        } else {
            model.addAttribute("contentId", id);
        }
        List<DataDictVO> pList=DictItemCache.get("petition_purpose");
        List<ContentModelParaVO> recList= getParam(columnId,LoginPersonUtil.getSiteId(),null);
        List<DataDictVO> cList= DictItemCache.get("petition_category");
        Integer recType=null;
        if(recList!=null&&recList.size()>0){
            recType=recList.get(0).getRecType();
        }
        model.addAttribute("recType",recType);
        model.addAttribute("pList",pList);
        model.addAttribute("recList",recList);
        model.addAttribute("cList",cList);
        model.addAttribute("pageIndex", pageIndex);
        return "content/onlinepetition/petition_edit";
    }

    /**
     * 保存信访
     * @param vo
     * @return
     */
    @RequestMapping("saveVO")
    @ResponseBody
    public Object  saveVO(OnlinePetitionVO vo){
        if (AppUtil.isEmpty(vo.getTitle())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "标题不能为空");
        }
        if(AppUtil.isEmpty(vo.getAuthor())){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "作者不能为空");
        }
        if(AppUtil.isEmpty(vo.getCategoryCode())){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "信件类型不能为空");
        }

        if(AppUtil.isEmpty(vo.getPurposeCode())){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "信件目的不能为空");
        }
        if(AppUtil.isEmpty(vo.getPhoneNum())){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "联系电话不能为空");
        }
        if(AppUtil.isEmpty(vo.getContent())){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "内容不能为空");
        }
        vo.setCreateUnitId(LoginPersonUtil.getUnitId());
        petitionService.saveVO(vo);
        return getObject("0");

    }


    /**
     * 根据主表ID获取信访实体类
     * @param id
     * @return
     */
    @RequestMapping("getVO")
    @ResponseBody
    public Object getVO(Long id){
        OnlinePetitionVO vo=new OnlinePetitionVO();
        if(id==null){
            vo.setPublishDate(new Date());
            return getObject(vo);
        }else{
            vo=petitionService.getVO(id);
            if(vo==null){
                throw new BaseRunTimeException(TipsMode.Message.toString(), "信件内容不存在");
            }else{
                return getObject(vo);
            }
        }
    }

    /**
     * 获取数据字典里配置的信访类别
     * @return
     */
    @RequestMapping("getCategoryCode")
    @ResponseBody
    public Object getCategoryCode(){
        //DataDictionaryUtil.getDDList("petition_category");
        return  DataDictionaryUtil.getItemList("petition_category",LoginPersonUtil.getSiteId());
    }

    /**
     * 获取数据字典里配置的信访目的
     * @return
     */
    @RequestMapping("getPurposeCode")
    @ResponseBody
    public Object getPurposeCode(){
        return  DataDictionaryUtil.getItemList("petition_purpose",LoginPersonUtil.getSiteId());
    }

    /**
     * 批量删除信访
     * @param ids
     * @return
     */
    @RequestMapping("deleteVOs")
    @ResponseBody
    public Object deleteVOs(String ids){
        if(AppUtil.isEmpty(ids)){
            return getObject(1);
        }else {
            petitionService.deleteVOs(ids);
        }
        return getObject(0);
    }

    /**
     * 去往转办页面
     * @param petitionId
     * @param columnId
     * @param model
     * @return
     */
    @RequestMapping("getRecObj")
    public String getRecObj(Long petitionId,Long columnId,Model model){
        if(petitionId==null||columnId==null){
            return null;
        }
        ColumnTypeConfigVO vo = ModelConfigUtil.getCongfigVO(columnId,LoginPersonUtil.getSiteId());
        if(vo==null){
            return null;
        }else{
            model.addAttribute("recType",vo.getRecType());
            model.addAttribute("petitionId",petitionId);
            model.addAttribute("columnId",columnId);
            return "content/onlinepetition/trans_edit";
        }
    }

    /**
     * 获取内容模型里配置的接收单位或人员
     * @param columnId
     * @param isTurn
     * @return
     */
    @RequestMapping("getRecUnitOrPerson")
    @ResponseBody
    public Object getRecUnitOrPerson(Long columnId,Integer isTurn){
        List<ContentModelParaVO> list= getParam(columnId,LoginPersonUtil.getSiteId(),isTurn);
        return getObject(list);
    }

    /**
     * 转办信访
     * @param petitionId
     * @param recUnitId
     * @param recUnitName
     * @param recType
     * @param remark
     * @return
     */
    @RequestMapping("transfer")
    @ResponseBody
    public Object transfer(Long petitionId,Long recUnitId,String recUnitName,Integer recType,String remark){
        if(recUnitId==null||recUnitName==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "流转单位不能为空");
        }
        if(AppUtil.isEmpty(remark)){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "流转备注不能为空");
        }
        petitionService.transfer(petitionId,recUnitId,recUnitName,recType,remark);
        OnlinePetitionEO petitionEO=petitionService.getEntity(OnlinePetitionEO.class,petitionId);
        final BaseContentEO contentEO=CacheHandler.getEntity(BaseContentEO.class, petitionEO.getContentId());
        if(contentEO==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数出错");
        }
        String strIds="";
        if(recUnitId!=null){
            List<TreeNodeVO>list=optService.getUserAuthForColumn(recUnitId, contentEO.getColumnId());
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
                logger.error("==============>  信访转办 <================");
                // 绑定session至当前线程中
                SessionFactory sessionFactory = SpringContextHolder.getBean(SessionFactory.class);
                boolean participate = ConcurrentUtil.bindHibernateSessionToThread(sessionFactory);
                sendMessge(contentEO, receiveIds, userId);
                // 关闭session
                ConcurrentUtil.closeHibernateSessionFromThread(participate, sessionFactory);
                logger.error("=============>  信访转办完成 <=====================");
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
        message.setModeCode(BaseContentEO.TypeCode.onlinePetition.toString());
        message.setRecUserIds(receiveIds);
        //message.setRecOrganIds(videoEO.getCreateOrganId() + "");
        //message.setCreateOrganId(videoEO.getCreateOrganId());
        message.setCreateUserId(userId);
        // message.setLink("/videoNews/videoPlayer?id="+eo.getId());
        message.setResourceId(eo.getId());
        message.setTitle("信访转办");
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
    public void downloadFile(HttpServletResponse response,String attachId){
        mongoDbFileServer.downloadFile(response,attachId,null);
    }


    /**
     * 当前栏目留言条数
     * @param columnId
     * @return
     */
    @RequestMapping("countData")
    public Long countData(Long columnId){
        return petitionService.countData(columnId);
    }

    /**
     * 根据密码查询信访
     * @param checkCode
     * @return
     */
    @RequestMapping("getByCheckCode")
    @ResponseBody
    public Object getByCheckCode(String checkCode){
        if(StringUtils.isEmpty(checkCode)){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "随机码不能为空");
        }
        OnlinePetitionVO vo=petitionService.getByCheckCode(checkCode,LoginPersonUtil.getSiteId());
        return  getObject(vo);
    }

    /**
     * 去往查看信访页面
     * @param contentId
     * @param model
     * @return
     */
    @RequestMapping("showVO")
    public String showVO(Long contentId,Model model){
        model.addAttribute("contentId",contentId);
        return "content/onlinepetition/petition_show";
    }
}
