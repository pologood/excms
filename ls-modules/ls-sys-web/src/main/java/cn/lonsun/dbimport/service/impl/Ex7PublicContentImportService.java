package cn.lonsun.dbimport.service.impl;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.ThreadUtil;
import cn.lonsun.dbimport.service.IImportService;
import cn.lonsun.dbimport.service.base.PublicInfoImportService;
import cn.lonsun.dbimport.service.thread.ImportPublic;
import cn.lonsun.dbimport.service.thread.ImportPublicInfo;
import cn.lonsun.dbimport.service.thread.ImportPublicInfoOther;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;


/**
 * ex7信息公开数据导入
 * @author zhongjun
 */
@Component("ex7PublicContentImportService")
public class Ex7PublicContentImportService extends PublicInfoImportService implements IImportService {

    public Ex7PublicContentImportService() {
        super("ex7");
        super.guideId_ = 121699L;// 公开指南id
        super.institutionId_ = 121704L;// 公开制度id
        super.reportId_ = 121701L;// 公开年报
        super.catalog_ = 2657091L;//依申请公开目录
    }

    /**
     * 导入已申请公开
     * @param organId
     * @param ex8organId
     * @return
     */
    public Callable<String> importPublic(Long organId, Long ex8organId) {
        List<Map<String, Object>> objectList = queryMapBySqlKey(group, "getXxgkApply", organId.longValue());
        return new ImportPublic(publicContentService,publicApplyService, LoginPersonUtil.getSiteId(), objectList, ex8organId, ThreadUtil.session.get());
    }
    /**
     * 导入公开指南
     *
     * @return
     */
    public Callable importPublicGuide(Long organId, Long ex8organId) {
        List<Map<String, Object>> data = queryMapBySqlKey(group, "getXxgkContentByDocTypeUnit", 6, organId.longValue());
        return new ImportPublicInfoOther(publicContentService, siteId.get(), data,
                ex8organId, guideId_, PublicContentEO.Type.PUBLIC_GUIDE.toString(), ThreadUtil.session.get());
    }

    /**
     * 导入公开制度
     * @return
     */
    public Callable importPublicInstitution(Long organId, Long ex8organId) {
        List<Map<String, Object>> data = queryMapBySqlKey(group, "getXxgkContentByDocTypeUnit", 8, organId.longValue());
        return new ImportPublicInfoOther(publicContentService, siteId.get(), data,
                ex8organId, institutionId_, PublicContentEO.Type.PUBLIC_INSTITUTION.toString(), ThreadUtil.session.get());
    }

    /**
     * 导入公开年报
     *
     * @return
     */
    public Callable importPublicAnnualReport(Long organId, Long ex8organId) {
        List<Map<String, Object>> data = queryMapBySqlKey(group, "getXxgkContentByDocTypeUnit", 4, organId.longValue());
        return new ImportPublicInfoOther(publicContentService, siteId.get(), data,
                ex8organId, reportId_, PublicContentEO.Type.PUBLIC_ANNUAL_REPORT.toString(), ThreadUtil.session.get());
    }

    /**
     * 导入依申请公开目录
     *
     * @return
     */
    public Callable importPublicCatalog(Long organId, Long ex8organId) {
        List<Map<String, Object>> data = queryMapBySqlKey(group, "getXxgkContentByCatIdUnit", 1602, organId.longValue());
        return new ImportPublicInfo(publicContentService, publicClassService, siteId.get(), data, xxgkClassMap,
                null,  ex8organId, catalog_, PublicContentEO.Type.PUBLIC_CATALOG.toString(), ThreadUtil.session.get());
    }

    /**
     * 导入主动公开信息
     * @return
     */
    public Callable importDrivingPublic(Long catId, Long ex8CatId, Long organId, Long ex8OrganId) {
        List<Map<String, Object>> data = queryMapBySqlKey(group, "getXxgkContentByCatIdUnit", catId.longValue(), organId.longValue());
        return new ImportPublicInfo(publicContentService, publicClassService, siteId.get(), data,
                xxgkClassMap, xxgkClassRelMap, ex8OrganId, ex8CatId, PublicContentEO.Type.DRIVING_PUBLIC.toString(), ThreadUtil.session.get());
    }

    @Override
    public List doBackUp() {
        return null;
    }

    @Override
    public void revert(Long task_id) throws BaseRunTimeException {

    }

}
