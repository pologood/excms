package cn.lonsun.datasourcemanage.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datasourcemanage.internal.entity.DataimportSiteDataSourceEO;
import cn.lonsun.datasourcemanage.internal.service.IDataimportDataSourceService;
import cn.lonsun.datasourcemanage.internal.service.IDataimportSiteDataSourceService;
import cn.lonsun.datasourcemanage.internal.vo.DataSourceQueryVo;
import cn.lonsun.internal.metadata.DataModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by lonsun on 2018-2-5.
 *
 */
@RequestMapping("/dataimportSiteDataSource")
@Controller
public class DataimportSiteDataSourceController extends BaseController {
    @Autowired
    private IDataimportSiteDataSourceService dataimportSiteDataSourceService;
    @Autowired
    private IDataimportDataSourceService dataimportDataSourceService;



    /**
     * 站点数据源首页
     * @return
     */
    @RequestMapping("index")
    public String index(){

        return "dataimport/sitedatasource/index";
    }

    /**
     * 站点列表
     * @return
     */
    @RequestMapping("getSitePage")
    @ResponseBody
    public Object getSitePage(DataSourceQueryVo queryVo){
        Pagination  pagination= dataimportSiteDataSourceService.getSitePage(queryVo);
        return getObject(pagination);
    }
    /**
     * 站点数据源首页
     * @return
     */
    @RequestMapping("columnTypePage")
    public String columnTypePage(Long siteId,Model model){
        model.addAttribute("siteId",siteId);

        return "dataimport/sitedatasource/column_type_page";
    }
    /**
     * 栏目类别列表
     *
     * @param queryVo
     * @return
     */
    @RequestMapping("getClomunType")
    @ResponseBody
    public Object getClomunType(DataSourceQueryVo queryVo){
        Pagination pagination=  dataimportSiteDataSourceService.getClomunTypePage(queryVo);
        return getObject(pagination);
    }

    /**
     * 数据源关联编辑
     * @param id
     *
     * @param model
     * @return
     */
    @RequestMapping("editPage")
    public String editPage(Long id,Model model,Long siteId,String typeCode){
        model.addAttribute("id",id);
        model.addAttribute("siteId",siteId);
        model.addAttribute("typeCode",typeCode);

        DataModule[] dataSourceTypes = DataModule.values();
        model.addAttribute("dataSourceTypes",dataSourceTypes);
        return "dataimport/sitedatasource/bind_page";
    }

    /**
     *
     * @param siteId
     * @param id
     * @param dataSourceId
     * @return
     */
    @RequestMapping("bindDataSource")
    @ResponseBody
    public Object bindDataSource(Long siteId,Long id,Long dataSourceId,String typeCode){
        dataimportSiteDataSourceService.bindDataSource(siteId,id,dataSourceId,typeCode);
        return getObject();
    }

    @RequestMapping("/delete")
    @ResponseBody
    public Object delete(@RequestParam("ids[]") Long[] ids){
        if(null==ids||ids.length<0){
            return ajaxErr("请选择待删除记录!");
        }
        dataimportSiteDataSourceService.delete(DataimportSiteDataSourceEO.class,ids);
        return getObject();
    }

}
