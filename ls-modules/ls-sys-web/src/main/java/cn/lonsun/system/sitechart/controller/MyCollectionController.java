package cn.lonsun.system.sitechart.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.system.sitechart.internal.entity.MyCollectionEO;
import cn.lonsun.system.sitechart.service.IMyCollectionService;
import cn.lonsun.system.sitechart.vo.MyCollectionVO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by hu on 2016/8/15.
 */
@Controller
@RequestMapping("mycollection")
public class MyCollectionController extends BaseController {
    @Autowired
    private IMyCollectionService myCollectionService;

    @RequestMapping("list")
    public String list() {
        return "system/sitechart/mycollec_list";
    }


    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(MyCollectionVO query) {
        query.setSiteId(LoginPersonUtil.getSiteId());
        Pagination pagination = myCollectionService.getPage(query);
        return getObject(pagination);

    }

    @ResponseBody
    @RequestMapping("GetMycollection")
    public Object getMycollection(Long id) {
        MyCollectionEO myCollectionEO = null;
        if (id != null) {
            myCollectionEO = myCollectionService.getEntity(MyCollectionEO.class, id);
        } else {
            myCollectionEO = new MyCollectionEO();
        }
        return getObject(myCollectionEO);
    }

    @ResponseBody
    @RequestMapping("del")
    public Object del(Long id){
        if (id !=null){

            myCollectionService.delete(MyCollectionEO.class,id);
        }
        return  getObject();
    }




}