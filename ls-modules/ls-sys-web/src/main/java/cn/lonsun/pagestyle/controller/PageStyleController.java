package cn.lonsun.pagestyle.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.AjaxObj;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.ResultVO;
import cn.lonsun.pagestyle.internal.entity.PageStyleEO;
import cn.lonsun.pagestyle.internal.entity.PageStyleModelEO;
import cn.lonsun.pagestyle.internal.service.IPageStyleService;
import cn.lonsun.site.contentModel.internal.service.IContentModelService;
import cn.lonsun.util.LoginPersonUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 界面样式
 * @author zhongjun
 */
@Controller
@RequestMapping("pageStyle")
public class PageStyleController extends BaseController {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private IPageStyleService pageStyleService;

    @Autowired
    private IContentModelService contentModelService;


    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index(ModelAndView mv){

        mv.setViewName("pagestyle/index");
        return mv;
    }

    @RequestMapping(value="edit", method = RequestMethod.GET)
    public ModelAndView editPage(ModelAndView mv, Long id){
        PageStyleEO eo = getEntity(id);
        mv.addObject("item", eo);
        mv.addObject("itemJson", JSONObject.toJSONString(eo));
        List model = contentModelService.getList(LoginPersonUtil.getSiteId());
        mv.addObject("models", model);
        mv.setViewName("pagestyle/edit");
        return mv;
    }

    @ResponseBody
    @RequestMapping("getPage")
    public Object getPage(PageQueryVO pageVo, HttpServletRequest request){
        try {
            Map<String, Object> param = AppUtil.parseRequestToMap(request);
            Pagination page = pageStyleService.getPage(pageVo, param);
            return page;
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultVO(ResultVO.Status.Failure.getValue(), "获取数据失败：" + e.getMessage());
        }
    }

    /**
     * 保存
     * @param style
     * @return
     */
    @ResponseBody
    @RequestMapping("save")
    public Object save(PageStyleEO style, @RequestParam(value = "modelConfig[]", required = false) String[] styleModelConfig){
        try {
            if(StringUtils.isEmpty(style.getName())){
                return new ResultVO(ResultVO.Status.Failure.getValue(), "请输入配置名称");
            }
            if(StringUtils.isEmpty(style.getWidth())){
                return new ResultVO(ResultVO.Status.Failure.getValue(), "请输入页面宽度");
            }
            if(StringUtils.isEmpty(style.getStyle())){
                return new ResultVO(ResultVO.Status.Failure.getValue(), "请输入样式");
            }
            if(styleModelConfig != null){
                style.setStyleModelConfig(Arrays.asList(styleModelConfig));
            }
            style.setDefaultValue();
            pageStyleService.save(style);
            return ajaxOk();
        } catch (BaseRunTimeException e) {
            return new ResultVO(ResultVO.Status.Failure.getValue(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultVO(ResultVO.Status.Failure.getValue(), "保存失败：" + e.getMessage());
        }
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("remove")
    public Object remove(Long id){
        try {
            if(id == null || id.longValue()==0){
                return new ResultVO(ResultVO.Status.Failure.getValue(), "请选择删除行");
            }
            pageStyleService.delete(id);
            return ajaxOk();
        } catch (BaseRunTimeException e) {
            return new ResultVO(ResultVO.Status.Failure.getValue(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultVO(ResultVO.Status.Failure.getValue(), "保存失败：" + e.getMessage());
        }
    }


    /**
     * 保存模型配置
     * @param id
     * @param modelCodes
     * @return
     */
    @ResponseBody
    @RequestMapping("saveModelConfig")
    public Object save(Long id, @RequestParam(value = "modelIds[]", required = false) String[] modelCodes){
        try {
            pageStyleService.saveModelConfig(id, modelCodes);
            return ajaxOk();
        } catch (BaseRunTimeException e) {
            return new ResultVO(ResultVO.Status.Failure.getValue(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultVO(ResultVO.Status.Failure.getValue(), "获取数据失败：" + e.getMessage());
        }
    }

    /**
     * 根据id获取配置
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("getOne")
    public Object getOne(Long id){
        try {
            return getObject(getEntity(id));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultVO(ResultVO.Status.Failure.getValue(), "获取数据失败：" + e.getMessage());
        }
    }

    public PageStyleEO getEntity(Long id){
        if(id == null || id.longValue() == 0){
            return PageStyleEO.getInstance();
        }
        PageStyleEO entity = pageStyleService.getEntity(PageStyleEO.class, id);
        if(entity != null){
            List<PageStyleModelEO> modelConfig = pageStyleService.getEfficientModel(id);
            if(modelConfig != null){
                List<String> list = new ArrayList<String>();
                for(PageStyleModelEO item : modelConfig){
                    list.add(item.getModelCode());
                }
                entity.setStyleModelConfig(list);
            }
        }
        entity.setDefaultValue();
        return entity;
    }

    /**
     * 根据栏目获取配置
     * @param columnId
     * @return
     */
    @ResponseBody
    @RequestMapping("getByColumn/{columnId}")
    public Object getByColumn(@PathVariable("columnId") Long columnId){
        try {
            PageStyleEO list = pageStyleService.getStyleByColumn(columnId);
            return getObject(list);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultVO(ResultVO.Status.Failure.getValue(), "获取数据失败：" + e.getMessage());
        }
    }

    /**
     * 根据栏目获取配置
     * @return 输出为js对象定义
     */
    @ResponseBody
    @RequestMapping("getAllConfig")
    public Object getAllConfig(){
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            PageStyleEO base = pageStyleService.getBaseStyle();
            if(base != null){
                base.setStyle("");
            }
            map.put("baseConfig", base);
            Map<Long, Object> list = pageStyleService.getAllWithColumn();
            map.put("columnConfig", list);
            return "var pageStyle = " + JSONObject.toJSONString(getObject(map), SerializerFeature.DisableCircularReferenceDetect);
        } catch (Exception e) {
            e.printStackTrace();
            return "var pageStyle = {baseConfig: null, columnConfig:[]}";
        }
    }

    /**
     * 根据栏目获取配置
     * @param columnId
     * @return
     */
    @ResponseBody
    @RequestMapping("getCssByColumn/{columnId}")
    public Object getByColumnCss(@PathVariable("columnId") Long columnId){
        try {
            PageStyleEO list = pageStyleService.getStyleByColumn(columnId);
            return list.getStyle();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultVO(ResultVO.Status.Failure.getValue(), "获取数据失败：" + e.getMessage());
        }
    }

    /**
     * 根据模型获取配置
     * @param modelCode
     * @return
     */
    @ResponseBody
    @RequestMapping("getByModel")
    public Object getByModel(String modelCode){
        try {
            Object list = pageStyleService.getStyleByModel(modelCode);
            return getObject(list);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultVO(ResultVO.Status.Failure.getValue(), "获取数据失败：" + e.getMessage());
        }
    }
}
