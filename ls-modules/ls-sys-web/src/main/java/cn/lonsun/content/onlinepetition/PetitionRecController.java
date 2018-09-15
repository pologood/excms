package cn.lonsun.content.onlinepetition;

import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.onlinePetition.internal.entity.OnlinePetitionEO;
import cn.lonsun.content.onlinePetition.internal.entity.PetitionRecEO;
import cn.lonsun.content.onlinePetition.internal.service.IOnlinePetitionService;
import cn.lonsun.content.onlinePetition.internal.service.IPetitionRecService;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.ModelConfigUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-30<br/>
 */
@Controller
@RequestMapping("petitionRec")
public class PetitionRecController extends BaseController {
    @Autowired
    private IPetitionRecService recService;
    @Autowired
    private IBaseContentService contentService;
    @Autowired
    private IOnlinePetitionService petitionService;

    /**
     * 去往信访办理页面
     * @param petitionId
     * @param columnId
     * @param replyId
     * @param model
     * @return
     */
    @RequestMapping("doReply")
    public String doReply(Long petitionId,Long columnId,Long replyId,Model model){
        if(petitionId==null){
          return null;
        }
        if(replyId==null){
            model.addAttribute("replyId","");
        }else{
            model.addAttribute("replyId",replyId);
        }
        List<ContentModelParaVO> statusList = ModelConfigUtil.getDealStatus(columnId, LoginPersonUtil.getSiteId());
        model.addAttribute("statusList", statusList);
        model.addAttribute("petitionId",petitionId);
        model.addAttribute("columnId",columnId);
        return "content/onlinepetition/reply_edit";
    }

    /**
     * 办理信访
     * @param recEO
     * @return
     */
    @RequestMapping("saveReply")
    @ResponseBody
    public Object saveReply(PetitionRecEO recEO){
        if(StringUtils.isEmpty(recEO.getDealStatus())){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "办理状态不能为空");
        }
        if(recEO.getReplyDate()==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "办理时间不能为空");
        }
        PetitionRecEO newEO=recService.saveReply(recEO);
        if(newEO==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "保存失败");
        }
        OnlinePetitionEO petitionEO=petitionService.getEntity(OnlinePetitionEO.class,newEO.getPetitionId());
        if(petitionEO==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数出错");
        }
        return getObject(0);
    }

    /**
     * 根据主键获取信访接收信息
     * @param id
     * @param columnId
     * @return
     */
    @RequestMapping("getRecVO")
    @ResponseBody
    public Object getRecVO(Long id,Long columnId){
        PetitionRecEO recEO=new PetitionRecEO();
        if(id==null){
            recEO.setReplyDate(new Date());
        }else{
            recEO=recService.getEntity(PetitionRecEO.class,id);
        }
        if(recEO==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数错误！");
        }

        ColumnTypeConfigVO configVO = ModelConfigUtil.getCongfigVO(columnId, LoginPersonUtil.getSiteId());
        if (configVO != null&&!StringUtils.isEmpty(recEO.getDealStatus())) {
            if (!StringUtils.isEmpty(recEO.getDealStatus()) && !StringUtils.isEmpty(configVO.getStatusCode())) {
                if (!configVO.getStatusCode().contains(recEO.getDealStatus())) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("deal_status", recEO.getDealStatus());
                    if (dictVO != null) {
                        recEO.setStatusName(dictVO.getKey());
                    }
                }
            }
        }
        return recEO;
    }

}
