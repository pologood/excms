package cn.lonsun.msg.submit.dao;

import cn.lonsun.content.vo.ContentChartQueryVO;
import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.msg.submit.entity.CmsMsgSubmitEO;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.StatisticsQueryVO;
import cn.lonsun.statistics.SubmitListVO;

import java.util.List;

/**
 * @author gu.fei
 * @version 2015-11-18 13:44
 */
public interface IMsgSubmitDao extends IBaseDao<CmsMsgSubmitEO> {

    public List<CmsMsgSubmitEO> getEOs();

    public Pagination getPageEOs(ParamDto vo);

    public List<CmsMsgSubmitEO> getEOs(ParamDto dto);

    public Long getCountByClassifyId(Long classifyId);

    public Long getCountChart(ContentChartQueryVO queryVO);

    public List<ContentChartVO> getContentChart(ContentChartQueryVO queryVO);

    public List<ContentChartVO> getEmpContentChart(ContentChartQueryVO queryVO);
    public List<ContentChartVO> getEmpContentChart1(ContentChartQueryVO queryVO);
    public List<ContentChartVO> getEmpContentChart2(ContentChartQueryVO queryVO);

    public List<SubmitListVO> getSubmitList(StatisticsQueryVO vo);

    public List<SubmitListVO> getSubmitList2(StatisticsQueryVO queryVO);

    public List<SubmitListVO> getSubmitList1(StatisticsQueryVO queryVO);

    public List<ContentChartVO> getContentChart1(ContentChartQueryVO queryVO);
    public List<ContentChartVO> getContentChart2(ContentChartQueryVO queryVO);

    public Pagination getDetailPage(Long pageIndex,Integer pageSize,Long uId, String uName);
}
