package cn.lonsun.content.onlinePetition.internal.service;

import cn.lonsun.content.onlinePetition.internal.entity.OnlinePetitionEO;
import cn.lonsun.content.onlinePetition.vo.OnlinePetitionVO;
import cn.lonsun.content.onlinePetition.vo.PetitionQueryVO;
import cn.lonsun.content.vo.ContentChartQueryVO;
import cn.lonsun.core.base.service.IMockService;
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

public interface IOnlinePetitionService extends IMockService<OnlinePetitionEO> {
    /**
     * 获取分页列表
     * @param pageVO
     * @return
     */
    public Pagination getPage(PetitionQueryVO pageVO);

    /**
     * 保存
     * @param vo
     */
    public  void saveVO(OnlinePetitionVO vo);

    /**
     * 根据主表ID获取详细信息
     * @param id
     * @return
     */
    public OnlinePetitionVO getVO(Long id);

    /**
     * 批量删除
     * @param ids
     */
    public void deleteVOs(String ids);

    /**
     * 信访转办
     * @param petitionId
     * @param recUnitId
     * @param recUnitName
     * @param recType
     * @param remark
     */
    public void transfer(Long petitionId,Long recUnitId,String recUnitName,Integer recType,String remark);

    /**
     * 获取某栏目下的信访数量
     * @param columnId
     * @return
     */
    public Long countData(Long columnId);

    /**
     * 更新统计：获取单位发文Top10
     * @param queryVO
     * @return
     */
    public List<ContentChartVO> getStatisticsList(ContentChartQueryVO queryVO);

    /**
     * 更新统计：统计信访类别数量
     * @param queryVO
     * @return
     */
    public List<ContentChartVO> getTypeList(ContentChartQueryVO queryVO);

    /**
     * 更新统计：获取信访详细分页列表
     * @param vo
     * @return
     */
    public Pagination getPetitionPage(StatisticsQueryVO vo);

    /**
     * 更新统计：获取信访详细列表
     * @param vo
     * @return
     */
    public List<GuestListVO> getPetitionList(StatisticsQueryVO vo);

    /**
     * 根据查询密码获取信访信息
     * @param checkCode
     * @param siteId
     * @return
     */
    public OnlinePetitionVO getByCheckCode(String checkCode,Long siteId);


    }
