package cn.lonsun.govbbs.internal.service.impl;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.govbbs.internal.dao.IBbsLogDao;
import cn.lonsun.govbbs.internal.entity.BbsLogEO;
import cn.lonsun.govbbs.internal.service.IBbsLogService;
import cn.lonsun.govbbs.internal.service.IBbsPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangchao on 2016/12/21.
 */
@Service("bbsLogService")
public class IBbsLogServiceImpl extends BaseService<BbsLogEO> implements IBbsLogService {

    @Autowired
    private IBbsLogDao bbsLogDao;

    @Autowired
    private IBbsPostService bbsPostService;


    @Override
    public void saveSupportLog(BbsLogEO log) {
        Boolean isSave = true;
        //如果是浏览只能浏览一次
        if(log.getOperation().equals(BbsLogEO.Operation.View.toString())){
            Map<String ,Object> params = new HashMap<String ,Object>();
            params.put("caseId",log.getCaseId());
            params.put("memberId",log.getMemberId());
            params.put("operation", BbsLogEO.Operation.View.toString());
            BbsLogEO eo = getEntity(BbsLogEO.class,params);
            if(eo != null){
                isSave = false;
                eo.setStatus(log.getStatus());
                updateEntity(eo);
            }
        }
        if(isSave){
            saveEntity(log);
        }

        //更新是帖子否有支持
        try{
            //如果是会浏览  记录浏览数
            if(log.getCaseId() != null && log.getStatus() != null && log.getStatus() == 1){
                bbsPostService.updateSupport(log.getCaseId());
            }
        }catch (Exception e){}
    }

    @Override
    public void saveLog(final BbsLogEO eo) {
        if(eo == null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "保存日志不能为空");
        }
        Boolean isSave = true;
        //如果是浏览只能浏览一次
        if(eo.getOperation().equals(BbsLogEO.Operation.View.toString())){
            Long count = bbsLogDao.getByMemberId(eo.getCaseId(),eo.getMemberId(), BbsLogEO.Operation.View.toString());
            if(count >= 1){
                isSave = false;
            }
        }
        if(isSave){
            saveEntity(eo);
            try{
                //如果是会浏览  记录浏览数
                if(eo.getOperation().equals(BbsLogEO.Operation.View.toString())){
                    bbsPostService.updateViewCount(eo.getCaseId());
                }
            }catch (Exception e){}
        }
    }

    @Override
    public Long getByMemberId(Long caseId,Long memberId, String oper) {
        return bbsLogDao.getByMemberId(caseId,memberId,oper);
    }

    @Override
    public List<BbsLogEO> getLogs(Long caseId) {
        if(caseId == null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "外键id不能为空");
        }
        return bbsLogDao.getLogs(caseId);
    }

    @Override
    public void deleteByCaseIds(Long[] caseIds) {
        bbsLogDao.deleteByCaseIds(caseIds);
    }
}
