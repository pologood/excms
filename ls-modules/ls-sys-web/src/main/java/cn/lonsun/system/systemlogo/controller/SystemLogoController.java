package cn.lonsun.system.systemlogo.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.system.systemlogo.internal.entity.SystemLogoEO;
import cn.lonsun.system.systemlogo.internal.service.ISystemLogoService;
import cn.lonsun.system.systemlogo.util.SystemLogoUtil;
import cn.lonsun.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hu on 2016/7/11.
 */
@Controller
@RequestMapping(value = "sysLogo", produces = { "application/json;charset=UTF-8" })
public class SystemLogoController extends BaseController{
    @Autowired
    private ISystemLogoService systemLogoService;
    //获取列表页面
    @RequestMapping("list")
    public String list(){
        return "/system/syslogo/sys_logo";
    }

    //获取对象
    @RequestMapping("getSystemLogo")
    @ResponseBody
    public Object getSystemimg(Long id){
        SystemLogoEO eo=null;
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<SystemLogoEO> imgs = systemLogoService.getEntities(SystemLogoEO.class,params);
        SystemLogoEO img = null;
        if(imgs != null && imgs.size() > 0){
            img = imgs.get(0);
        }else{
            img = new SystemLogoEO();
        }
        return getObject(img);
    }




    @RequestMapping("save")
    @ResponseBody
    public Object save(SystemLogoEO eo){

        if(eo.getLogoId() != null){
            systemLogoService.updateEntity(eo);
        }else{
            systemLogoService.saveEntity(eo);
        }
        if (!StringUtils.isEmpty(eo.getIndexImg())) {
            FileUploadUtil.saveFileCenterEO(eo.getIndexImg());
        }
        if (!StringUtils.isEmpty(eo.getLoginImg())) {
            FileUploadUtil.saveFileCenterEO(eo.getLoginImg());
        }
        if (!StringUtils.isEmpty(eo.getSystemImg())) {
            FileUploadUtil.saveFileCenterEO(eo.getSystemImg());
        }
        SystemLogoUtil.loginImg = eo.getLoginImg();
        SystemLogoUtil.systemImg = eo.getSystemImg();
        SystemLogoUtil.indexImg = eo.getIndexImg();
        return getObject();

    }

}
