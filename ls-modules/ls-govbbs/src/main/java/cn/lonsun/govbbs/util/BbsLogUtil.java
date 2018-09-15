package cn.lonsun.govbbs.util;

import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.govbbs.internal.entity.BbsLogEO;
import cn.lonsun.govbbs.internal.service.IBbsLogService;
import cn.lonsun.govbbs.internal.vo.BbsMemberVO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.core.task.TaskExecutor;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by zhangchao on 2016/12/21.
 */
public class BbsLogUtil{

    private static IBbsLogService bbsLogService= SpringContextHolder.getBean("bbsLogService");

    private static TaskExecutor taskExecutor = SpringContextHolder.getBean(TaskExecutor.class);


    /**
     * 保存系统记录
     * @param caseId
     * @param oper
     */
    public static void saveSysLog(final Long caseId,final String oper,Integer status){
        BbsLogEO log  = new BbsLogEO();
        log.setCaseId(caseId);
        log.setOperation(oper);
        log.setType(BbsLogEO.Type.User.toString());
        Long userId = LoginPersonUtil.getUserId();
        log.setMemberId(userId);
        log.setMemberName(LoginPersonUtil.getPersonName());
        log.setCreateUserId(userId);
        log.setStatus(status);
        saveAsynLog(log);
    }


    /**
     * 保存网站
     * @param caseId
     * @param oper
     */
    public static void saveStaticLog(final Long postId){
        BbsLogEO log  = new BbsLogEO();
        log.setCaseId(postId);
        log.setOperation(BbsLogEO.Operation.View.toString());
        HttpSession session = ContextHolderUtils.getSession();
        if(session != null && session.getAttribute("bbsUser") != null){
            BbsMemberVO member = (BbsMemberVO)session.getAttribute("bbsUser") ;
            if(member != null){
                log.setMemberId(member.getId());
                log.setMemberName(member.getName());
                log.setCreateUserId(member.getId());
                //游客
                if(member.getMemberType().equals(BbsMemberVO.MemberType.TOURIST.getMemberType())){
                    log.setType(BbsLogEO.Type.Tourist.toString());
                }else{
                    log.setType(BbsLogEO.Type.Member.toString());
                }
                saveAsynLog(log);
            }

        }
    }

    /**
     * 支持
     * @param caseId
     * @param oper
     * @param status
     */
    public static void saveSupportLog(final Long caseId,Integer status){
        BbsLogEO log  = new BbsLogEO();
        log.setCaseId(caseId);
        log.setOperation(BbsLogEO.Operation.View.toString());
        log.setStatus(status);
        log.setType(BbsLogEO.Type.Member.toString());
        HttpSession session = ContextHolderUtils.getSession();
        if(session != null && session.getAttribute("bbsUser") != null){
            BbsMemberVO member = (BbsMemberVO)session.getAttribute("bbsUser") ;
            if(member != null){
                log.setMemberId(member.getId());
                log.setMemberName(member.getName());
                log.setCreateUserId(member.getId());
                saveSupportLog(log);
            }
        }

    }

    /**
     * 支持
     * @param log
     */
    public static void saveSupportLog(final BbsLogEO log){
        if(log != null){
            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        bbsLogService.saveSupportLog(log);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally{
                    }
                }
            });
        }
    }

    /**
     * 保存日志
     * @param log
     */
    public static void saveAsynLog(final BbsLogEO log){
        if(log != null){
            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        bbsLogService.saveLog(log);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally{
                    }
                }
            });
        }
    }


    /**
     * 获取所有日志
     * @param caseId
     * @return
     */
    public static List<BbsLogEO> getLogs(Long caseId){
        return bbsLogService.getLogs(caseId);
    }

    /**
     * 删除日志
     * @param caseIds
     */
    public static void deleteByCaseIds(Long[] caseIds) {
        bbsLogService.deleteByCaseIds(caseIds);
    }
}
