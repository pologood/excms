package cn.lonsun.dbimport.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.dbimport.service.IImportService;
import cn.lonsun.dbimport.service.base.PublicInfoImportService;
import cn.lonsun.dbimport.service.impl.Ex7PublicContentImportService;
import cn.lonsun.dbimport.util.ContentUrlUpdateUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Controller
@RequestMapping("dbimport")
public class DbImportController extends BaseController {

    @Resource
    private IImportService ex7publicCatalogImportService;

    @Resource(name="ex7PublicContentImportService")
    private PublicInfoImportService ex7PublicContentImportService;


    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index(ModelAndView mv){

        mv.setViewName("dbimport/index");
        return mv;
    }

    @ResponseBody
    @RequestMapping("importCatalog")
    public Object importCatalog(){

        ex7publicCatalogImportService.doImport();

        return getObject();
    }

    @ResponseBody
    @RequestMapping("importContent")
    public Object importContent(){
        try {
            Object taskId = ex7PublicContentImportService.doImport();
            return getObject(taskId);
        } catch (Exception e) {
            e.printStackTrace();
            return ajaxErr("导入失败：" + e.getMessage());
        }
    }

    /**
     * 根据目录id导入
     * @param catId
     * @param ex8CatId
     * @param organId
     * @param ex8OrganId
     * @return
     */
    @ResponseBody
    @RequestMapping("importContentByOrganCatId")
    public Object importContentByOrganCatId(Long catId, Long ex8CatId, Long organId, Long ex8OrganId){
        try {
            ExecutorService executor = Executors.newCachedThreadPool();
            ex7PublicContentImportService.doBefore();
            Callable c = ex7PublicContentImportService.importDrivingPublic(catId, ex8CatId, organId, ex8OrganId);
            Future f = executor.submit(c);
            return getObject(f.get());
        } catch (Exception e) {
            e.printStackTrace();
            return ajaxErr("导入失败：" + e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping("stopImport")
    public Object importContent(String taskId){
        try {
            if(StringUtils.isEmpty(taskId)){
                return ajaxErr("任务id不能为空");
            }
            ex7PublicContentImportService.stopImport(taskId);
            return getObject(taskId);
        } catch (Exception e) {
            e.printStackTrace();
            return ajaxErr("导入失败：" + e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping("importOrgan")
    public Object importContent(Long organId){
        try {
//            ex7PublicContentImportService.importOrgan(organId);

        } catch (Exception e) {
            e.printStackTrace();
            return ajaxErr("导入失败：" + e.getMessage());
        }
        return getObject();
    }

    @ResponseBody
    @RequestMapping("revert")
    public Object revert(Long id){
        ex7publicCatalogImportService.revert(id);
        return getObject();
    }

    @ResponseBody
    @RequestMapping("getProcess")
    public Object getProcess(String taskId){
        Queue<String> q = Ex7PublicContentImportService.importMsg.get(taskId);
        if(q == null || q.isEmpty()){
            return getObject("任务不存在");
        }
        return getObject(q);
    }

    @ResponseBody
    @RequestMapping("refreshConfig")
    public Object refreshConfig(){
        try {
            ex7publicCatalogImportService.reloadConfig();
        } catch (Exception e) {
            e.printStackTrace();
            return ajaxErr("配置刷新失败：" + e.getMessage());
        }
        return getObject();
    }

    @ResponseBody
    @RequestMapping("updateContent")
    public Object updateContent(){
        try {
            final ContentUrlUpdateUtil contentUrlUpdateUtil = new ContentUrlUpdateUtil();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    contentUrlUpdateUtil.updateContent(".*href=\"/UploadFile/*", "/UploadFile/","/oldfiles/UploadFile/");
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    contentUrlUpdateUtil.updateContent(".*src=\"/UploadFile/*", "/UploadFile/","/oldfiles/UploadFile/");
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            return ajaxErr("配置刷新失败：" + e.getMessage());
        }
        return getObject();
    }

}
