package cn.lonsun.govbbs.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.govbbs.internal.entity.BbsFileEO;
import cn.lonsun.system.filecenter.internal.vo.FileCenterVO;

import java.util.List;

/**
 * 论坛附件Service层<br/>
 *
 */

public interface IBbsFileDao extends IBaseDao<BbsFileEO> {

    /**
     * 获取论坛附件分页
     */
    public Pagination getPage(FileCenterVO fileVO);

    void deleteIds(Long[] ids);

    List<BbsFileEO> getBbsFiles(Long caseId);
}

