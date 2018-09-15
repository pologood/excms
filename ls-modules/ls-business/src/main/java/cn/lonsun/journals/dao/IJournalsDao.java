package cn.lonsun.journals.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.journals.entity.JournalsEO;
import cn.lonsun.journals.vo.JournalsQueryVO;
import cn.lonsun.journals.vo.JournalsSearchVO;

import java.util.List;

/**
 * Created by lonsun on 2017-1-3.
 */
public interface IJournalsDao extends IMockDao<JournalsEO> {
    Pagination getJournalsList(JournalsQueryVO journalsQueryVO);

    List<JournalsEO> checkMateria(JournalsEO journalsEO);

    List<JournalsSearchVO> getAllSearchVO();
}
