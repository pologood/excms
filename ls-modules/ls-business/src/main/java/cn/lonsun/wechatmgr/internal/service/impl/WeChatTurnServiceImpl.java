package cn.lonsun.wechatmgr.internal.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.wechatmgr.internal.dao.IWeChatTurnDao;
import cn.lonsun.wechatmgr.internal.entity.WeChatTurnEO;
import cn.lonsun.wechatmgr.internal.entity.WeChatUserEO;
import cn.lonsun.wechatmgr.internal.service.IWeChatTurnService;
import cn.lonsun.wechatmgr.internal.service.IWeChatUserService;
import cn.lonsun.wechatmgr.vo.WeChatProcessVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lonsun on 2016-10-10.
 */
@Service
public class WeChatTurnServiceImpl extends MockService<WeChatTurnEO> implements IWeChatTurnService {
    @Autowired
    private IWeChatTurnDao weChatTurnDao;
    @Autowired
    private IWeChatUserService weChatUserService;
    @Override
    public Pagination getInforByMsgId(WeChatProcessVO weChatProcessVO) {
          Pagination page =  weChatTurnDao.getInforByMsgId(weChatProcessVO);
          for( WeChatTurnEO eo :  (List<WeChatTurnEO>)  page.getData()){
            WeChatUserEO weChatUserEO = weChatUserService.getUserByName(eo.getOriginUserName());
            if(null!=weChatUserEO){
                eo.setNickname(weChatUserEO.getNickname());
            }
            if(eo.getType().equals(WeChatTurnEO.TYPE.sub.toString())){

                eo.setDetails(eo.getNickname()+"提交");
            }

            else if(eo.getType().equals(WeChatTurnEO.TYPE.reply.toString())){
                eo.setDetails(eo.getOperateUnitName()+eo.getOperateUserName()+"回复留言");

            } else if(eo.getType().equals(WeChatTurnEO.TYPE.turn.toString())){

                eo.setDetails(eo.getOperateUnitName()+eo.getOperateUserName()+"转办到"+eo.getTurnUnitName());
            }

        }

        return page;
    }

    @Override
    public List<WeChatTurnEO> getProcessListNew(WeChatProcessVO weChatProcessVO) {
        List<WeChatTurnEO>  list =  weChatTurnDao.getProcessListNew(weChatProcessVO);
        for( WeChatTurnEO eo :  list){
            WeChatUserEO weChatUserEO = weChatUserService.getUserByName(eo.getOriginUserName());
            if(null!=weChatUserEO){
                eo.setNickname(weChatUserEO.getNickname());
            }
            if(eo.getType().equals(WeChatTurnEO.TYPE.sub.toString())){

                eo.setDetails(eo.getNickname()+"提交");
            }

             else if(eo.getType().equals(WeChatTurnEO.TYPE.reply.toString())){
                eo.setDetails(eo.getOperateUnitName()+eo.getOperateUserName()+"回复留言");

            } else if(eo.getType().equals(WeChatTurnEO.TYPE.turn.toString())){

                eo.setDetails(eo.getOperateUnitName()+eo.getOperateUserName()+"转办到"+eo.getTurnUnitName());
            }

        }
        return list;
    }
}
