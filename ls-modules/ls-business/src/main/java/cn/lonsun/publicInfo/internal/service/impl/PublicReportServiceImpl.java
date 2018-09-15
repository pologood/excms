package cn.lonsun.publicInfo.internal.service.impl;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.CSVUtils;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.publicInfo.internal.entity.PublicReportEO;
import cn.lonsun.publicInfo.internal.service.IOrganConfigService;
import cn.lonsun.publicInfo.internal.service.IPublicReportService;
import cn.lonsun.publicInfo.vo.PublicReportVO;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fth on 2017/1/19.
 */
@Service
public class PublicReportServiceImpl extends MockService<PublicReportEO> implements IPublicReportService {

    @Resource
    private IOrganConfigService organConfigService;

    @Override
    public Long saveEntity(PublicReportEO publicReportEO) {
        Long id = publicReportEO.getId();
        if (null == id) {
            return super.saveEntity(publicReportEO);
        }
        super.updateEntity(publicReportEO);
        return id;
    }


    @Override
    public List<PublicReportEO> getPublicReportList() {
        String hql = "from PublicReportEO p where p.siteId = :siteId and p.recordStatus = :recordStatus order by p.sortNum";
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("siteId", LoginPersonUtil.getSiteId());
        paramMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        return super.getMockDao().getEntitiesByHql(hql, paramMap);
    }

    @Override
    public List<PublicReportEO> getPublicReportTjList(PublicReportVO publicReportVO) {
        List<PublicReportEO> reportList = this.getPublicReportList();
        if (null != reportList && !reportList.isEmpty()) {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            Long catId = publicReportVO.getCatId();// 信息公开目录顶层id
            if (null != publicReportVO.getStartDate()) {
                paramMap.put("startDate", publicReportVO.getStartDate());
            }
            if (null != publicReportVO.getEndDate()) {
                paramMap.put("endDate", publicReportVO.getEndDate());
            }
            for (PublicReportEO reportEO : reportList) {// 计算每一项值
                if (reportEO.getIsFill()) {//自定义值
                    continue;
                }
                if (reportEO.getIsSql()) {//自定义sql
                    String sql = reportEO.getSql();
                    if (StringUtils.isNotEmpty(sql)) {
                        if (sql.indexOf("WHERE") > -1) {
                            sql += " AND 1 = 1 ";
                        } else {
                            sql += " WHERE 1 = 1 ";
                        }
                        if (null != publicReportVO.getStartDate()) {
                            sql += " AND T.PUBLISH_DATE >= :startDate ";
                        }
                        if (null != publicReportVO.getEndDate()) {
                            sql += " AND T.PUBLISH_DATE <= :endDate ";
                        }
                    }
                    Long result = super.getMockDao().getCountBySql(sql, paramMap);
                    reportEO.setResult(result);
                } else {//根据目录id查询
                    boolean catalogInQuery = false;
                    StringBuffer sql = new StringBuffer();
                    sql.append(" SELECT COUNT (1) FROM CMS_PUBLIC_CONTENT P LEFT JOIN CMS_BASE_CONTENT B ON P.CONTENT_ID = B.ID ");
                    sql.append(" WHERE P.TYPE = '" + PublicContentEO.Type.DRIVING_PUBLIC.toString()).append("' ");
                    sql.append(" AND P.RECORD_STATUS = '" + AMockEntity.RecordStatus.Normal.toString()).append("' ");
                    sql.append(" AND B.RECORD_STATUS = '" + AMockEntity.RecordStatus.Normal.toString()).append("' ");
                    if (null != publicReportVO.getStartDate()) {
                        sql.append(" AND B.PUBLISH_DATE >= :startDate ");
                    }
                    if (null != publicReportVO.getEndDate()) {
                        sql.append(" AND B.PUBLISH_DATE <= :endDate ");
                    }
                    String keyIds = reportEO.getKeyIds();
                    if (StringUtils.isNotEmpty(keyIds)) {
                        int index = 0;
                        String[] topIdArr = keyIds.split("\\|");
                        int length = topIdArr.length;
                        for (String id : topIdArr) {
                            if (null == catId) {//统计全部
                                if (index == 0) {
                                    sql.append(" AND ( ");
                                } else {
                                    sql.append(" OR ");
                                }
                                if (id.indexOf("-") > -1) {//表明有子栏目
                                    sql.append(" P.CAT_ID IN (").append(id.substring(id.indexOf("-") + 1)).append(") ");
                                } else {
                                    sql.append(" P.ORGAN_ID IN (SELECT C.ORGAN_ID FROM CMS_ORGAN_CONFIG C LEFT JOIN RBAC_ORGAN R ON C.ORGAN_ID = R.ORGAN_ID");
                                    sql.append(" WHERE C.CAT_ID = ").append(id).append(" AND R.RECORD_STATUS = '" + AMockEntity.RecordStatus.Normal.toString()).append("' AND R.IS_PUBLIC = 1)");
                                }
                                if (++index == length) {
                                    sql.append(" ) ");
                                }
                                catalogInQuery = true;
                            } else {
                                if (id.indexOf(catId + "") > -1) {//找到符合当前目录，循环结束
                                    if (id.indexOf("-") > -1) {//表明有子栏目
                                        sql.append(" AND P.CAT_ID IN (").append(id.substring(id.indexOf("-") + 1)).append(") ");
                                    } else {
                                        sql.append(" AND P.ORGAN_ID IN (SELECT C.ORGAN_ID FROM CMS_ORGAN_CONFIG C LEFT JOIN RBAC_ORGAN R ON C.ORGAN_ID = R.ORGAN_ID");
                                        sql.append(" WHERE C.CAT_ID = ").append(id).append(" AND R.RECORD_STATUS = '" + AMockEntity.RecordStatus.Normal.toString()).append("' AND R.IS_PUBLIC = 1)");
                                    }
                                    catalogInQuery = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (catalogInQuery) {
                        Long result = super.getMockDao().getCountBySql(sql.toString(), paramMap);
                        reportEO.setResult(result);
                    }
                }
            }
        }
        return reportList;
    }

    @Override
    public void exportPublicReportList(PublicReportVO publicReportVO, HttpServletResponse response) {
        List<PublicReportEO> reportList = this.getPublicReportTjList(publicReportVO);
        String[] titles = new String[]{"统计指标", "单位", "统计数"};
        List<String[]> resultList = new ArrayList<String[]>();
        if (null != reportList && !reportList.isEmpty()) {
            for (PublicReportEO tj : reportList) {
                Long result = null == tj.getResult() ? 0L : tj.getResult();
                String[] row = {tj.getTitle(), tj.getUnit(), tj.getIsFill() ? "" : result.toString()};
                resultList.add(row);
            }
        }
        try {
            CSVUtils.download("信息公开报表统计", titles, resultList, response);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BaseRunTimeException(TipsMode.Message.toString(), "导出失败，请稍后重试！");
        }
    }
}
