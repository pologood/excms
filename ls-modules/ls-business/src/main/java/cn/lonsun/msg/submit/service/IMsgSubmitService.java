package cn.lonsun.msg.submit.service;

import cn.lonsun.content.vo.ContentChartQueryVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.msg.submit.entity.CmsMsgSubmitEO;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.StatisticsQueryVO;
import cn.lonsun.statistics.SubmitListVO;

import java.util.List;

/**
 * @author gu.fei
 * @version 2015-11-18 13:45
 */
public interface IMsgSubmitService extends IBaseService<CmsMsgSubmitEO> {

    public List<CmsMsgSubmitEO> getEOs();

    public Pagination getPageEOs(ParamDto dto);

    public List<CmsMsgSubmitEO> getEOs(ParamDto dto);

    public Long getCountByClassifyId(Long classifyId);

    public void saveEO(CmsMsgSubmitEO eo);

    public void updateEO(CmsMsgSubmitEO eo);

    public void deleteEO(Long[] ids);

    public Long getCountChart(ContentChartQueryVO queryVO);

    public List<ContentChartVO> getContentChart(ContentChartQueryVO queryVO);

    public List<ContentChartVO> getEmpContentChart(ContentChartQueryVO queryVO) ;

    public List<SubmitListVO> getSubmitList(StatisticsQueryVO vo);

    public List<SubmitListVO> getSubmitList2(StatisticsQueryVO queryVO) ;

    public Pagination getSubmitPage(StatisticsQueryVO vo);

    public Pagination getDetailPage(Long pageIndex,Integer pageSize,Long uId,String uName);
}
