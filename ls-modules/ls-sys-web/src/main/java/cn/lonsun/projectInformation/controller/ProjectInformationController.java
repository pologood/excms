package cn.lonsun.projectInformation.controller;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.projectInformation.internal.entity.ProjectInformationEO;
import cn.lonsun.projectInformation.internal.service.IProjectInformationService;
import cn.lonsun.projectInformation.vo.ProjectInformationQueryVO;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.PoiExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by huangxx on 2017/3/3.
 */
@Controller
@RequestMapping("/projectInformation")
public class ProjectInformationController extends BaseController{


    @Autowired
    private IProjectInformationService projectInformationService;

    @Autowired
    private IBaseContentService baseContentService;
    /**
     * 列表页面
     * @return
     */
    @RequestMapping("index")
    public String index() {
        return "/projectInformation/index";
    }


    /**
     * 获取分页
     * @return
     */
    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(ProjectInformationQueryVO queryVO) {
        Pagination page = projectInformationService.getPageEntities(queryVO);
        return page;
    }


    @RequestMapping("/saveEO")
    @ResponseBody
    public Object saveEO(ProjectInformationEO eo) {
        projectInformationService.saveEO(eo);
        Long siteId = eo.getSiteId();
        Long columnId = eo.getColumnId();
        MessageSenderUtil.publishContent(
                new MessageStaticEO(siteId, columnId, new Long[]{}).setType(MessageEnum.PUBLISH.value()), 1);
        return ResponseData.success("保存成功!");
    }


    @RequestMapping("/updateEO")
    @ResponseBody
    public Object updateEO(ProjectInformationEO eo) {
        projectInformationService.updateEO(eo);
        Long siteId = eo.getSiteId();
        Long columnId = eo.getColumnId();
        MessageSenderUtil.publishContent(
                new MessageStaticEO(siteId, columnId, new Long[]{}).setType(MessageEnum.PUBLISH.value()), 1);
        return ResponseData.success("更新成功!");
    }


    @RequestMapping("/delete")
    @ResponseBody
    public Object deleteEO(String ids) {
        Long[] idsn = StringUtils.getArrayWithLong(ids,",");
        projectInformationService.deleteEO(idsn);
        return ResponseData.success("删除成功!");
    }

    @RequestMapping("projectInfoExport")
    public String unitMemberExport(Model m){

        return "/projectInformation/project_info_export";
    }

    @RequestMapping("downInfoxls")
    public void downInfoxls(HttpServletRequest request, HttpServletResponse response) throws Exception{
       // String organName = request.getParameter("name");
        String[] headers =new String[]{"受理单位名称","项目名称","项目地址","面积","发证文号","发证日期"};
        String name = "项目规划信息导入模板";
        String title = "项目规划信息导入";
        PoiExcelUtil.exportExcel(title, name, "xls", headers, null, response);
    }

    @RequestMapping("exportInfoxls")
    @ResponseBody
    public Object  exportInfoxls(@RequestParam("file") MultipartFile file, HttpServletRequest request,Long columnId,String columnType) throws Exception{
        Long siteId= LoginPersonUtil.getSiteId();
        if (file.getSize() == 0L){
            return ajaxErr("请选择导入文件!");
        }
        String suffix = PoiExcelUtil.getSuffix(file.getOriginalFilename());
        if (!suffix.equals("xls")){
            return ajaxErr("文件类型不正确!");
        }
        try {
            List<ProjectInformationEO> projectInformationEOs = PoiExcelUtil.getProjectInfos(file.getInputStream(),siteId,columnId,columnType);//导入会员
            if(projectInformationEOs != null && projectInformationEOs.size() >0){
                for (ProjectInformationEO eo : projectInformationEOs) {
                    //导入进来的项目信息也要管理basecontent表
                    BaseContentEO contentEO = new BaseContentEO();
                    contentEO.setTitle(eo.getProjectName());
                    contentEO.setColumnId(eo.getColumnId());
                    contentEO.setSiteId(eo.getSiteId());
                    contentEO.setRecordStatus(AMockEntity.RecordStatus.Normal.toString());
                    contentEO.setTypeCode(eo.getInformationType());

                    //id为内容模型的ID
                    Long id = baseContentService.saveEntity(contentEO);
                    CacheHandler.saveOrUpdate(BaseContentEO.class,contentEO);
                    eo.setContentId(id);
                    projectInformationService.saveEntity(eo);
                }
            }

        }catch(BaseRunTimeException e){
            return ajaxErr(e.getTipsMessage());
        }
        return getObject();
    }

}
