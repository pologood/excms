package cn.lonsun.datasourcemanage.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datasourcemanage.internal.entity.DataimportDataSourceEO;
import cn.lonsun.datasourcemanage.internal.service.IDataimportDataSourceService;
import cn.lonsun.datasourcemanage.internal.vo.DataSourceQueryVo;
import cn.lonsun.internal.metadata.DbType;
import cn.lonsun.manufacturer.internal.entity.ManufacturerEO;
import cn.lonsun.manufacturer.internal.service.IManufacturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**数据源控制层
 * Created by lonsun on 2018-2-5.
 *
 *
 *
 *
 */
@Controller
@RequestMapping("/dataimportdatasource")
public class DataimportDataSourceController extends BaseController {

    @Autowired
    private IDataimportDataSourceService dataimportDataSourceService;
    @Autowired
    private IManufacturerService manufacturerService;

    @RequestMapping("/index")
    public String index(Model model){
        Map<String,Object> param =new HashMap<String, Object>();
        param.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<ManufacturerEO> manufacturerEOs =manufacturerService.getEntities(ManufacturerEO.class,param);
        model.addAttribute("manufacturerEOs",manufacturerEOs);
        return "dataimport/datasource/index";
    }

    /**
     * 数据源列表
     *
     * @return
     */
    @RequestMapping("/getPage")
    @ResponseBody
    public Object getPage(DataSourceQueryVo dataSourceQueryVo){
        Pagination pagination = dataimportDataSourceService.getPage(dataSourceQueryVo);
        return getObject(pagination);
    }

    /**数据源编辑页
     *
     * @param dataSourceId
     * @param model
     * @return
     */
    @RequestMapping("/editPage")
    public String editPage(Long dataSourceId,Model model){
        DbType [] dbTypes =   DbType.values();
        model.addAttribute("dbTypes", Arrays.asList(dbTypes));
        model.addAttribute("dataSourceId",dataSourceId);
        Map<String,Object> param =new HashMap<String, Object>();
        param.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<ManufacturerEO> manufacturerEOs =manufacturerService.getEntities(ManufacturerEO.class,param);
        model.addAttribute("manufacturerEOs",manufacturerEOs);
        return "dataimport/datasource/edit_page";
    }

    /**
     * 获取对象
     * @param dataSourceId
     * @return
     *
     *
     */
    @RequestMapping("/getDataSource")
    @ResponseBody
    public Object getDataSource(Long dataSourceId){
        return getObject(AppUtil.isEmpty(dataSourceId)?new DataimportDataSourceEO():dataimportDataSourceService.getEntity(DataimportDataSourceEO.class,dataSourceId));
    }

    @RequestMapping("/saveDataSource")
    @ResponseBody
    public Object saveDataSource(DataimportDataSourceEO dataimportDataSourceEO){
        dataimportDataSourceService.saveOrUpdateDataSource(dataimportDataSourceEO);


        return getObject();
    }
    @RequestMapping("/delete")
    @ResponseBody
    public Object delete(@RequestParam("ids[]") Long[] ids){
        if(null==ids||ids.length<0){
            return ajaxErr("请选择待删除记录!");
        }
        dataimportDataSourceService.delete(ids);
        return getObject();
    }

}
