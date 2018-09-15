package cn.lonsun.net.service.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.net.service.entity.CmsNetServiceClassifyEO;
import cn.lonsun.net.service.service.INetServiceClassifyService;
import cn.lonsun.site.template.util.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gu.fei
 * @version 2015-11-18 8:13
 */
@Controller
@RequestMapping("/netClassify")
public class NetServiceController extends BaseController {

    private static final String FILE_BASE = "/net/service";

    @Autowired
    private INetServiceClassifyService netServiceClassifyService;

    @RequestMapping("/index")
    public String index() {
        return FILE_BASE + "/classify/index";
    }

    /*
    * to:跳转的页面
    * */
    @RequestMapping("/addOrEdit")
    public ModelAndView addOrEdit(String to) {
        ModelAndView model = new ModelAndView(FILE_BASE + "/classify/" + to);
        return model;
    }

    @ResponseBody
    @RequestMapping("/getClassifyEOs")
    public Object getClassifyEOs(Long id) {
        boolean flag = false;
        if(id == null) {
            id = (long) -1;
            flag = true;
        }
        List<CmsNetServiceClassifyEO> list = netServiceClassifyService.getEOsByPid(id);

        if(flag) {
            CmsNetServiceClassifyEO eo = new CmsNetServiceClassifyEO();
            eo.setId((long) -1);
            eo.setName("分类列表");
            list.add(eo);
        }

        return list == null?new ArrayList<CmsNetServiceClassifyEO>() : list;
    }

    @ResponseBody
    @RequestMapping("/save")
    public Object save(CmsNetServiceClassifyEO eo,String flag) {
        netServiceClassifyService.save(eo,flag);
        return ResponseData.success(eo,"保存成功!");
    }

    @ResponseBody
    @RequestMapping("/update")
    public Object update(CmsNetServiceClassifyEO eo) {
        netServiceClassifyService.update(eo);
        return ResponseData.success(eo,"修改成功!");
    }

    @ResponseBody
    @RequestMapping("/delete")
    public Object delete(Long id,Long pid) {
        netServiceClassifyService.delete(id, pid);
        return ResponseData.success("删除成功!");
    }

    @ResponseBody
    @RequestMapping("/getSortNum")
    public Object getSortNum(Long pid) {
        return netServiceClassifyService.getSortNum(pid);
    }
}
