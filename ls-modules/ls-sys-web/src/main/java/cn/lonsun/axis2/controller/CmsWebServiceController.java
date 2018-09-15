package cn.lonsun.axis2.controller;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.webservice.internal.entity.WebServiceEO;
import cn.lonsun.webservice.internal.service.IWebserviceService;
import cn.lonsun.webservice.vo.Axis2ServiceVO;
import cn.lonsun.webservice.vo.WebServiceQueryVO;

/**
 * @ClassName: WebServiceController
 * @Description: WebService管理控制器
 * @author Hewbing
 * @date 2015年10月10日 下午5:15:05
 * 
 */
@Controller
@RequestMapping("cmswebservice")
public class CmsWebServiceController extends BaseController {
    @Autowired
    private IWebserviceService webserviceService;

    /**
     * WebService管理首页
     * 
     * @return
     */
    @RequestMapping("index")
    public String index() {
        return "/system/webservice/index";
    }

    /**
     * 剪辑页面
     * 
     * @return
     */
    @RequestMapping("edit")
    public String edit(Long webServiceId, ModelMap map) {
        map.put("webServiceId", webServiceId);
        List<DataDictVO> dataDictList = DataDictionaryUtil.getDDList("webservice_app");
        map.put("dataDictList", dataDictList);
        return "/system/webservice/edit";
    }

    /**
     * 获取分页
     * 
     * @param vo
     * @return
     */
    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(WebServiceQueryVO vo) {
        if (vo.getPageIndex() == null || vo.getPageIndex() < 0) {
            vo.setPageIndex(Long.valueOf(0));
        }
        if (vo.getPageSize() == null || vo.getPageSize() < 0) {
            vo.setPageSize(Integer.valueOf(15));
        }
        return getObject(webserviceService.getPage(vo));
    }

    /**
     * 获取WebService对象
     * 
     * @param webserviceId
     * @return
     */
    @RequestMapping("getEmptyService")
    @ResponseBody
    public Object getEmptyService() {
        return getObject(new WebServiceEO());
    }

    /**
     * 获取WebService
     * 
     * @param webserviceId
     * @return
     */
    @RequestMapping("getService")
    @ResponseBody
    public Object getService(Long webServiceId) {
        WebServiceEO service = webserviceService.getEntity(WebServiceEO.class, webServiceId);
        if (AppUtil.isEmpty(service)) {
            service = new WebServiceEO();
        }
        // service.setSystemCode("sysMgr");
        // service.setSystemName("系统管理");
        return getObject(service);
    }

    /**
     * 保存WebService
     * 
     * @param service
     * @return
     */
    @RequestMapping("save")
    @ResponseBody
    public Object save(WebServiceEO service) {
        // 系统编码不能为空
        // if (StringUtils.isEmpty(service.getSystemCode())) {
        // throw new BaseRunTimeException(TipsMode.Message.toString(),
        // "请选择服务所属系统");
        // }
        // uri不能为空
        if (StringUtils.isEmpty(service.getUri())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入服务地址");
        }
        // namespace不能为空
        if (StringUtils.isEmpty(service.getNameSpace())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入命名空间");
        }
        // 服务编码不能为空
        String code = service.getCode();
        if (StringUtils.isEmpty(code)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入服务编码");
        }
        // 验证编码是否重复
        if (webserviceService.getServiceByCode(code) != null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "服务编码已存在，请输入新的编码");
        }
        webserviceService.save(service);
        return getObject();
    }

    /**
     * 更新WebService
     * 
     * @param service
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public Object update(Long webServiceId, Axis2ServiceVO service) {
        // 系统编码不能为空
        // if (StringUtils.isEmpty(service.getSystemCode())) {
        // throw new BaseRunTimeException(TipsMode.Message.toString(),
        // "请选择服务所属系统");
        // }
        // uri不能为空
        if (StringUtils.isEmpty(service.getUri())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入服务地址");
        }
        // namespace不能为空
        if (StringUtils.isEmpty(service.getNameSpace())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入命名空间");
        }
        // 服务编码不能为空
        String code = service.getCode();
        if (StringUtils.isEmpty(code)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入服务编码");
        }
        // 验证编码是否重复
        WebServiceEO src = webserviceService.getEntity(WebServiceEO.class, webServiceId);
        if (!src.getCode().equals(service.getCode()) && webserviceService.getServiceByCode(code) != null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "服务编码已存在，请输入新的编码");
        }
        BeanUtils.copyProperties(service, src);
        webserviceService.update(src);
        return getObject();
    }

    /**
     * 删除WebService
     * 
     * @param webserviceId
     *            主键
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public Object delete(@RequestParam("webServiceIds[]") Long[] webServiceIds) {
        if (webServiceIds != null && webServiceIds.length > 0) {
            webserviceService.delete(webServiceIds);
        }
        return getObject();
    }

}
