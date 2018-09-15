package cn.lonsun.journals.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.journals.entity.JournalsEO;
import cn.lonsun.journals.vo.JournalsQueryVO;
import cn.lonsun.journals.vo.JournalsSearchVO;
import cn.lonsun.security.internal.entity.SecurityMateria;

import java.util.List;

/**
 * Created by lonsun on 2017-1-3.
 */
public interface IJournalsService extends IMockService<JournalsEO> {
    Pagination getJournalsList(JournalsQueryVO journalsQueryVO);

    JournalsEO saveJournals(JournalsEO journalsEO,Long columnId);

    List<JournalsSearchVO> getAllSearchVO();
}
