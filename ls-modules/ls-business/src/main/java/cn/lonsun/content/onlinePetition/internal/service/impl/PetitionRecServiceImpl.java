package cn.lonsun.content.onlinePetition.internal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.onlinePetition.internal.entity.OnlinePetitionEO;
import cn.lonsun.content.onlinePetition.internal.entity.PetitionRecEO;
import cn.lonsun.content.onlinePetition.internal.service.IOnlinePetitionService;
import cn.lonsun.content.onlinePetition.internal.service.IPetitionRecService;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.RequestUtil;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.FileUploadUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.SysLog;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-27<br/>
 */
@Service("petitionRecSevice")
public class PetitionRecServiceImpl extends MockService<PetitionRecEO> implements IPetitionRecService {
    @Autowired
    private IOnlinePetitionService petitionService;
    @Autowired
    private IBaseContentService baseService;

    @Override
    public PetitionRecEO saveReply(PetitionRecEO recEO) {
        if(recEO.getPetitionId()!=null){
            OnlinePetitionEO eo=petitionService.getEntity(OnlinePetitionEO.class,recEO.getPetitionId());
            eo.setDealStatus(recEO.getDealStatus());
            petitionService.updateEntity(eo);
            SysLog.log("改变信访信息状态 >> ID：" + eo.getId() ,
                    "OnlinePetitionEO", CmsLogEO.Operation.Update.toString());

            if(recEO.getId()==null){
                recEO.setReplyIp(RequestUtil.getIpAddr(LoginPersonUtil.getRequest()));
                if(recEO.getPetitionId()==null){
                    throw new BaseRunTimeException(TipsMode.Message.toString(), "参数出错");
                }
                recEO.setRecUserId(eo.getCreateUserId());
                BaseContentEO baseEO=baseService.getEntity(BaseContentEO.class,eo.getContentId());
                recEO.setReUserName(baseEO.getAuthor());
                recEO.setReplyUserName(LoginPersonUtil.getUserName());
                saveEntity(recEO);
                if (!AppUtil.isEmpty(recEO.getAttachId())) {
                    FileUploadUtil.setStatus(eo.getAttachId(), 1, baseEO.getId(), baseEO.getColumnId(), baseEO.getSiteId());
                }
                SysLog.log("添加信访回复 >> ID：" + recEO.getId() ,
                        "PetitionRecEO", CmsLogEO.Operation.Add.toString());
                return recEO;
            }else{
                PetitionRecEO reo=getEntity(PetitionRecEO.class, recEO.getId());
                reo.setReplyIp(RequestUtil.getIpAddr(LoginPersonUtil.getRequest()));
                reo.setAttachId(recEO.getAttachId());
                reo.setAttachName(recEO.getAttachName());
                reo.setReplyContent(recEO.getReplyContent());
                reo.setReplyUserName(LoginPersonUtil.getUserName());
                reo.setReplyDate(recEO.getReplyDate());
                reo.setDealStatus(recEO.getDealStatus());
                updateEntity(reo);
                if (!AppUtil.isEmpty(recEO.getAttachId())) {
                    FileUploadUtil.setStatus(eo.getAttachId(), 1, eo.getContentId(), eo.getColumnId(), eo.getSiteId());
                }
                SysLog.log("修改信访回复 >> ID：" + reo.getId() ,
                        "PetitionRecEO", CmsLogEO.Operation.Update.toString());
                return reo;
            }
        }
        return null;

    }
}
