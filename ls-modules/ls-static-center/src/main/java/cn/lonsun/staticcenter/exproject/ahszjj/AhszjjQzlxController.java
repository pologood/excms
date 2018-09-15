package cn.lonsun.staticcenter.exproject.ahszjj;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.SpringContextHolder;
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
 * Created by 1960274114 on 2016-10-13.
 * 质量品牌升级工程专题
 */
@Controller
@RequestMapping("/ahszjj/qzlx")
public class AhszjjQzlxController extends BaseController {
    //政民互动导入页面
    private final String PAGE_News = "/ahszjj/qzlx/impListPage";
    //政民互动导入
    private final String ACTION_News = "/ahszjj/qzlx/newsImp";

    private static JdbcUtils loadRemoteJdbc() {
        JdbcUtils jdbcUtils = JdbcUtils.getInstance();
        ////10.0.0.253:1433
       /* jdbcUtils.setURLSTR("jdbc:jtds:sqlserver://61.191.61.136:31433;DatabaseName=ahzjj_qzlx;useLOBs=false;");
        jdbcUtils.setUSERNAME("ahzj_newweb");
        jdbcUtils.setUSERPASSWORD("ahzj_newweb");*/

        jdbcUtils.setURLSTR("jdbc:jtds:sqlserver://".concat(AhszjjConstant.db_qzlx_IP).concat(":" + AhszjjConstant.db_qzlx_Port)
                .concat(";DatabaseName=".concat(AhszjjConstant.db_qzlx_name).concat(";useLOBs=false;")));
        jdbcUtils.setUSERNAME(AhszjjConstant.db_qzlx_user);
        jdbcUtils.setUSERPASSWORD(AhszjjConstant.db_qzlx_pwd);
        return jdbcUtils;
    }
    @RequestMapping("impListPage")
    public ModelAndView impListPage(String siteId){
        Long site_id = null !=siteId ? AppUtil.getLong(siteId):AhszjjConstant.siteId;
        JdbcUtils jdbcUtils = loadRemoteJdbc();
        ModelAndView mv =  new ModelAndView("/dbconvert/ahszjj/zlpp_imp");
        String type_title = "综合信息.两学一做";
        String imp_action = ACTION_News;
        SyncArticleNews articleNews = new SyncArticleNews(jdbcUtils, site_id);
        //原站点栏目
        List<OldSiteColumnPairVO> fromList = articleNews.getOldColumnList();

        //ex8站点栏目
        IColumnConfigService columnConfigService = SpringContextHolder.getBean(IColumnConfigService.class);
        List<ColumnMgrEO> toList = new ArrayList<ColumnMgrEO>();

        List<ColumnMgrEO> rootList = columnConfigService.getColumnTreeBySite(site_id);
        if(null !=rootList){
            IColumnConfigDao configDao = SpringContextHolder.getBean(IColumnConfigDao.class);
            for(ColumnMgrEO eo1:rootList){//1级
                toList.add(eo1);
                List<ColumnMgrEO> list1 = configDao.getColumnByParentId(eo1.getIndicatorId(), true, site_id);
                if(null !=list1){
                    for(ColumnMgrEO eo2:list1){//2级
                        eo2.setName(" - " + eo2.getName());
                        toList.add(eo2);
                        List<ColumnMgrEO> list2 = configDao.getColumnByParentId(eo2.getIndicatorId(), true, site_id);
                        if(null !=list2){
                            for(ColumnMgrEO eo3:list2) {//2级
                                eo3.setName(" -- " + eo3.getName());
                                toList.add(eo3);
                                List<ColumnMgrEO> list3 = configDao.getColumnByParentId(eo3.getIndicatorId(), true, site_id);
                                if(null !=list3){
                                    for(ColumnMgrEO eo4:list3) {//3级
                                        eo4.setName(" ---  " + eo4.getName());
                                        toList.add(eo4);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        mv.addObject("siteId",site_id);
        mv.addObject("fromList", fromList);
        mv.addObject("toList", toList);
        mv.addObject("type_title",type_title);
        mv.addObject("imp_action",imp_action);
        return mv;
    }

    @RequestMapping("newsImp")
    @ResponseBody
    public String newsImp(Long siteId,String fromCode,String indicatorIdAndCode) {
        siteId = null ==siteId?AhszjjConstant.siteId:siteId;
        if(StringUtils.isEmpty(fromCode) || StringUtils.isEmpty(indicatorIdAndCode)){
            return "未知操作!<a href=\"".concat(PAGE_News  + "?siteId=" + siteId).concat("\">返回</a>");
        }

        JdbcUtils jdbcUtils = loadRemoteJdbc();
        Long start = System.currentTimeMillis();
        String[] indicatorIdAndCodeArgs = indicatorIdAndCode.split("~");
        String columnTypeCode = indicatorIdAndCodeArgs[0];
        Long indicatorId = AppUtil.getLong(indicatorIdAndCodeArgs[1]);
        SyncArticleNews articleNews = new SyncArticleNews(jdbcUtils, siteId, AhszjjConstant.createUserId, indicatorId);
        articleNews.imp(fromCode,columnTypeCode);
        return "导入成功（耗时：" + (System.currentTimeMillis() - start) / 1000 + "秒）！<a href=\"".concat(PAGE_News  + "?siteId=" + siteId).concat("\">返回</a>");
    }
}
