package cn.lonsun.staticcenter.exproject.ahszjj;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.site.internal.dao.IColumnConfigDao;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.staticcenter.util.JdbcUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1960274114 on 2016-10-9.
 * 安徽省质量技术监督管理局
 */
@Controller
@RequestMapping("/ahszjj/db")
public class AhszjjController extends BaseController {

    //政民互动导入页面
    private final String PAGE_InteractiveVirtual = "/ahszjj/db/impListPage?type=interactiveVirtual";
    //政民互动导入
    private final String ACTION_InteractiveVirtual = "/ahszjj/db/interactiveVirtualImp";

    //政民互动导入页面
    private final String PAGE_NetWork = "/ahszjj/db/impListPage?type=netWork";
    //政民互动导入
    private final String ACTION_NetWork = "/ahszjj/db/netWorkImp";

    //政民互动.领导邮箱.导入页面
    private final String PAGE_GUSTBOOK = "/ahszjj/db/impPage?type=1";
    //政民互动.领导邮箱.导入
    private final String ACTION_GUSTBOOK = "/ahszjj/db/impGustBook";

    //新闻默认来源
    private final String RESOURCE_DEFAULT = "安徽省质量技术监督管理局";

    private JdbcUtils loadRemoteJdbc() {
        JdbcUtils jdbcUtils = JdbcUtils.getInstance();
        ////10.0.0.253:1433
        jdbcUtils.setURLSTR("jdbc:jtds:sqlserver://".concat(AhszjjConstant.db_Site_IP).concat(":" + AhszjjConstant.db_Site_Port)
                .concat(";DatabaseName=".concat(AhszjjConstant.db_Site_name).concat(";useLOBs=false;")));
        jdbcUtils.setUSERNAME(AhszjjConstant.db_Site_user);
        jdbcUtils.setUSERPASSWORD(AhszjjConstant.db_Site_pwd);
        return jdbcUtils;
    }

    @RequestMapping("impPage")
    public ModelAndView impPage(String type,String siteId){
        Long site_id = null !=siteId ? AppUtil.getLong(siteId):AhszjjConstant.siteId;
        String type_title ;
        String imp_action;
        JdbcUtils jdbcUtils = loadRemoteJdbc();
        if("1".equals(type)){//政民互动.领导邮箱
            ModelAndView mv =  new ModelAndView("/dbconvert/ahszjj/fixColumn_imp");
            type_title = "政民互动.领导邮箱";
            imp_action = ACTION_GUSTBOOK;
            mv.addObject("type",type);
            mv.addObject("type_title",type_title);
            mv.addObject("imp_action",imp_action);

            //查询无效总记录数
            SyncGustBook syncGustBook = new SyncGustBook(jdbcUtils, site_id, AhszjjConstant.createUserId,964161L);
            //栏目ID
            mv.addObject("siteId",site_id);
            mv.addObject("curColumdId",964161L);
            mv.addObject("validateCount",syncGustBook.getValidateCount());
            return mv;
        }else{
            return null;
        }
    }

    @RequestMapping("impListPage")
    public ModelAndView impListPage(String type,String siteId){
        Long site_id = null !=siteId ? AppUtil.getLong(siteId):AhszjjConstant.siteId;
        ModelAndView mv =  new ModelAndView("/dbconvert/ahszjj/ahszjj_list_imp");
        String type_title ;
        String imp_action;
        //原站点栏目
        List<OldSiteColumnPairVO> fromList = new ArrayList<OldSiteColumnPairVO>();
        //ex8站点栏目
        List<ColumnMgrEO> toList = null;
        if("interactiveVirtual".equals(type)) {//政民互动
            type_title = "政民互动";
            imp_action = ACTION_InteractiveVirtual;
            String columnTypeCode = "InteractiveVirtual";
            IColumnConfigService columnConfigService = SpringContextHolder.getBean(IColumnConfigService.class);

            fromList.add(new OldSiteColumnPairVO("SYS_Jzxx", "局长信箱"));
            fromList.add(new OldSiteColumnPairVO("SYS_Zxbsinfo", "12365质量热线"));
            fromList.add(new OldSiteColumnPairVO("SYS_zxftjs", "在线访谈"));
            fromList.add(new OldSiteColumnPairVO("sys_zxdclist", "调查征集"));

            List<ColumnMgrEO> plist = null;
            IndicatorEO siteEO = CacheHandler.getEntity(IndicatorEO.class, site_id);
            if (null !=siteEO && IndicatorEO.Type.CMS_Site.toString().equals(siteEO.getType())) {
                plist = CacheHandler.getList(ColumnMgrEO.class, CacheGroup.CMS_COLUMN_TYPE_CODE, columnTypeCode);
            } else {
                String[] columnTypeCodes = {columnTypeCode};
                plist = columnConfigService.getVirtualColumn(site_id, true, columnTypeCodes);
            }
            Long pId = null;
            if (plist != null && plist.size() > 0) {
                for (ColumnMgrEO eo : plist) {
                    if (site_id.equals(eo.getSiteId())) {
                        pId = eo.getIndicatorId();
                        break;
                    }
                }
            }

            if (pId != null) {
                IColumnConfigDao configDao = SpringContextHolder.getBean(IColumnConfigDao.class);
                toList = configDao.getColumnByParentId(pId, true, site_id);
            }

        }else if("netWork".equals(type)){//
            mv =  new ModelAndView("/dbconvert/ahszjj/ahszjj_list_impForOrg");
            fromList.add(new OldSiteColumnPairVO("sys_news", "办事指南"));
            type_title = "办事指南";
            imp_action = ACTION_NetWork;
            toList = getNetClassifyList(site_id);

            mv.addObject("organgId",AhszjjConstant.organgId);
            mv.addObject("organName",AhszjjConstant.organName);
        }else{
            return null;
        }
        mv.addObject("siteId",site_id);
        mv.addObject("fromList", fromList);
        mv.addObject("toList", toList);
        mv.addObject("type_title",type_title);
        mv.addObject("imp_action",imp_action);
        return mv;
    }

    /**
     * 办事指南栏目列表
     * @return
     */
    private List<ColumnMgrEO> getNetClassifyList(Long site_id) {
        JdbcUtils jdbcUtils = loadRemoteJdbc();
        List<ColumnMgrEO> ret = new ArrayList<ColumnMgrEO>();
        IColumnConfigService columnConfigService = SpringContextHolder.getBean(IColumnConfigService.class);
        //ex8站点栏目
        String columnTypeCode = "net_work";
        String targetColumnTypeCode = "netClassify";

        List<ColumnMgrEO> plist = null;
        IndicatorEO siteEO = CacheHandler.getEntity(IndicatorEO.class, site_id);
        if (IndicatorEO.Type.CMS_Site.toString().equals(siteEO.getType())) {
            plist = CacheHandler.getList(ColumnMgrEO.class, CacheGroup.CMS_COLUMN_TYPE_CODE, columnTypeCode);
        } else {
            String[] columnTypeCodes = {columnTypeCode};
            plist = columnConfigService.getVirtualColumn(site_id, true, columnTypeCodes);
        }
        Long pId = null;
        if (plist != null && plist.size() > 0) {
            for (ColumnMgrEO eo : plist) {
                if (site_id.equals(eo.getSiteId())) {
                    pId = eo.getIndicatorId();
                    break;
                }
            }
        }
        List<ColumnMgrEO> toList = null;
        IColumnConfigDao configDao = SpringContextHolder.getBean(IColumnConfigDao.class);
        if (pId != null) {
            toList = configDao.getColumnByParentId(pId, true, site_id);
        }
        if(null !=toList && toList.size()>0){
            Long rootId = null;
            for(ColumnMgrEO eo:toList){
                if(eo.getColumnTypeCode().equals(targetColumnTypeCode)){
                    rootId =  eo.getIndicatorId();
                    break;
                }
            }
            if(null !=rootId){
                toList = configDao.getColumnByParentId(rootId, true, site_id);
                for(ColumnMgrEO eo:toList){
                    ret.add(eo);
                    String pname = eo.getName();
                    //二级菜单
                    List<ColumnMgrEO> toChildList = configDao.getColumnByParentId(eo.getIndicatorId(), true, site_id);
                    if(null !=toChildList){
                        for(ColumnMgrEO child:toChildList){
                            child.setName(pname.concat(".").concat(child.getName()));
                            ret.add(child);
                        }
                    }
                }
            }
        }

        return ret;
    }

    /**
     * 政民互动导入
     * @param fromCode
     * @param indicatorIdAndCode
     * @return
     */
    @RequestMapping("interactiveVirtualImp")
    @ResponseBody
    public String interactiveVirtualImp(Long siteId,String fromCode,String indicatorIdAndCode){
        siteId = null ==siteId?AhszjjConstant.siteId:siteId;
        Long start = System.currentTimeMillis();
        if(StringUtils.isEmpty(fromCode) || StringUtils.isEmpty(indicatorIdAndCode)){
            return "未知操作!<a href=\"".concat(PAGE_InteractiveVirtual  + "&siteId=" + siteId).concat("\">返回</a>");
        }
        JdbcUtils jdbcUtils = loadRemoteJdbc();
        String[] indicatorIdAndCodeArgs = indicatorIdAndCode.split("~");
        String columnTypeCode = indicatorIdAndCodeArgs[0];
        Long indicatorId = AppUtil.getLong(indicatorIdAndCodeArgs[1]);

        if("messageBoard".equals(columnTypeCode)) {
            SyncMessageBoard messageBoard = new SyncMessageBoard(jdbcUtils, siteId, AhszjjConstant.createUserId, indicatorId);
            messageBoard.impByTable(fromCode);
        }else if("interviewInfo".equals(columnTypeCode)) {
            SyncInterviewInfo syncInterviewInfo = new SyncInterviewInfo(jdbcUtils, siteId, AhszjjConstant.createUserId, indicatorId);
            syncInterviewInfo.imp();
        }else if("survey".equals(columnTypeCode)){
            SynSurvey synSurvey = new SynSurvey(jdbcUtils, siteId, AhszjjConstant.createUserId, indicatorId);
            synSurvey.imp();
        }else{
            return "亲，此栏目待开发中...!<a href=\"".concat(PAGE_InteractiveVirtual  + "&siteId=" + siteId).concat("\">返回</a>");
        }
        return "导入成功（耗时：" + (System.currentTimeMillis() - start) / 1000 + "秒）！<a href=\"".concat(PAGE_InteractiveVirtual  + "&siteId=" + siteId).concat("\">返回</a>");
    }

    /**
     * 网上办事导入
     * @param organgId
     * @param indicatorIdAndCode
     * @return
     */
    @RequestMapping("netWorkImp")
    @ResponseBody
    public String netWorkImp(Long siteId,String organgId,String organName,String indicatorIdAndCode){
        siteId = null ==siteId?AhszjjConstant.siteId:siteId;
        Long start = System.currentTimeMillis();
        if(StringUtils.isEmpty(indicatorIdAndCode)){
            return "未知操作!<a href=\"".concat(PAGE_NetWork  + "?siteId=" + siteId).concat("\">返回</a>");
        }
        JdbcUtils jdbcUtils = loadRemoteJdbc();
        String[] indicatorIdAndCodeArgs = indicatorIdAndCode.split("~");
        String columnTypeCode = indicatorIdAndCodeArgs[0];
        Long indicatorId = AppUtil.getLong(indicatorIdAndCodeArgs[1]);

        if("workGuide".equals(columnTypeCode)) {
            SyncWorkGuide workGuide = new SyncWorkGuide(jdbcUtils, siteId, AhszjjConstant.createUserId, indicatorId);
            workGuide.imp(organgId,organName);
        }else{
            return "亲，此栏目待开发中...!<a href=\"".concat(PAGE_NetWork  + "?siteId=" + siteId).concat("\">返回</a>");
        }

        return "导入成功（耗时：" + (System.currentTimeMillis() - start) / 1000 + "秒）！<a href=\"".concat(PAGE_NetWork  + "?siteId=" + siteId).concat("\">返回</a>");
    }
    /**
     * 加载明细
     * @param type
     * @return
     */
    @RequestMapping("loadDetail")
    @ResponseBody
    public Object loadDetail(String type,Long site_id){
        JdbcUtils jdbcUtils = loadRemoteJdbc();
        if("1".equals(type)) {//政民互动.领导邮箱
            SyncGustBook syncGustBook = new SyncGustBook(jdbcUtils,site_id,AhszjjConstant.createUserId,964161L);
            return ajaxOk(syncGustBook.getloadDetailList());
        }else{
            return ajaxErr("未知操作!");
        }
    }

    @RequestMapping("impGustBook")
    @ResponseBody
    public String impGustBook(Long siteId,Integer rangType,String id){
        siteId = null ==siteId?AhszjjConstant.siteId:siteId;
        JdbcUtils jdbcUtils = loadRemoteJdbc();
        Long start = System.currentTimeMillis();
        if(rangType==1 && StringUtils.isEmpty(id)){
            return "请选择操作项,<a href=\"".concat(PAGE_GUSTBOOK + "?siteId=" + siteId).concat("\">返回</a>");
        }
        SyncMessageBoard messageBoard = new SyncMessageBoard(jdbcUtils,siteId,AhszjjConstant.createUserId,964161L);
        if(rangType==0){
            messageBoard.imp(null);
        }else{
            messageBoard.imp(id);
        }

        return "导入成功（耗时：" + (System.currentTimeMillis() - start) / 1000 + "秒）！<a href=\"".concat(PAGE_GUSTBOOK  + "?siteId=" + siteId).concat("\">返回</a>");
    }



}
