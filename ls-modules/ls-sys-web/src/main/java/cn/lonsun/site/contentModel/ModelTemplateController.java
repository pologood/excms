package cn.lonsun.site.contentModel;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;
import cn.lonsun.site.contentModel.internal.service.IModelTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-10-13<br/>
 */
@Controller
@RequestMapping("modelTpl")
public class ModelTemplateController extends BaseController {
    @Autowired
    private IModelTemplateService modelTplService;

    /**
     * 去往模型模板编辑页面
     * @param modelId
     * @param model
     * @return
     */
    @RequestMapping("toEditTpl")
    public String toEditTpl(Long modelId,Model model){
        if(modelId==null){
            modelId=0L;
        }
        model.addAttribute("mId",modelId);
        return "site/contModel/tpl_edit";
    }

    /**
     * 获取模型模板分页列表
     * @param pageIndex
     * @param pageSize
     * @param modelId
     * @return
     */
    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(Long pageIndex,Integer pageSize,Long modelId){
        Pagination page=modelTplService.getPage(pageIndex,pageSize,modelId);
        if(page.getData()==null||page.getData().size()==0){
            List<Object> list=new ArrayList<Object>();
            ModelTemplateEO eo=new ModelTemplateEO();
            list.add((Object)eo);
            page.setData(list);
        }
        return page;
    }

    /**
     * 保存模型模板
     * @param eo
     * @return
     */
    @RequestMapping("saveEO")
    @ResponseBody
    public Object saveEO(ModelTemplateEO eo){
        if(eo.getModelTypeCode()==null||eo.getModelTypeCode().trim()==""){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "模板类型不能为空");
        }
        modelTplService.saveEO(eo);
        return getObject("1");
    }

    /**
     * 删除模型模板
     * @param tplId
     * @return
     */
    @RequestMapping("delEO")
    @ResponseBody
    public Object delEO(Long tplId){
        if(tplId==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择要删除的项");
        }
        modelTplService.delEO(tplId);
        return getObject("1");
    }

    /**
     * 校验栏目类型code值是否存在
     * @param modelId
     * @param code
     * @param tplId
     * @return
     */
    @RequestMapping("checkCode")
    @ResponseBody
    public Object checkCode(Long modelId,String code,Long tplId){
        Boolean b=modelTplService.checkCode(modelId,code,tplId);
        return getObject(b);
    }
}
