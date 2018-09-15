package cn.lonsun.content.onlinePetition.internal.dao;

import cn.lonsun.content.onlinePetition.internal.entity.OnlinePetitionEO;
import cn.lonsun.content.onlinePetition.vo.OnlinePetitionVO;
import cn.lonsun.content.onlinePetition.vo.PetitionQueryVO;
import cn.lonsun.content.vo.ContentChartQueryVO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.GuestListVO;
import cn.lonsun.statistics.StatisticsQueryVO;

import java.util.List;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-27<br/>
 */

public interface IOnlinePetitionDao extends IMockDao<OnlinePetitionEO> {
    public Pagination getPage(PetitionQueryVO pageVO);

    public OnlinePetitionVO getVO(Long id);

    public Long countData(Long columnId);

    public List<ContentChartVO> getStatisticsList(ContentChartQueryVO queryVO);

    public List<ContentChartVO> getTypeList(ContentChartQueryVO queryVO);
    public Pagination getPetitionPage(StatisticsQueryVO vo);

    public List<GuestListVO> getPetitionList(StatisticsQueryVO vo);

    public OnlinePetitionVO getByCheckCode(String checkCode, Long siteId);
}
