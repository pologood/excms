package cn.lonsun.govbbs.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.govbbs.internal.entity.BbsLogEO;

import java.util.List;

/**
 * Created by zhangchao on 2016/12/21.
 */
public interface IBbsLogService extends IBaseService<BbsLogEO> {
    //保存日志
    void saveLog(final BbsLogEO vo);

    //获取日志
    List<BbsLogEO> getLogs(Long caseId);

    /**
     * 查询日志数
     * @param caseId
     * @param memberId
     * @param s
     * @return
     */
    Long getByMemberId(Long caseId, Long memberId, String s);

    /**
     * 支持  反对
     * @param log
     */
    void saveSupportLog(BbsLogEO log);

    /**
     * 删除
     * @param caseIds
     */
    void deleteByCaseIds(Long[] caseIds);
}
