package cn.lonsun.govbbs.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.govbbs.internal.entity.BbsLogEO;

import java.util.List;

/**
 * Created by zhangchao on 2016/12/21.
 */
public interface IBbsLogDao extends IBaseDao<BbsLogEO> {

    List<BbsLogEO> getLogs(Long caseId);

    Long getByMemberId(Long caseId, Long memberId, String operation);

    void deleteByCaseIds(Long[] caseIds);
}
