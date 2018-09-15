package cn.lonsun.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.internal.service.IDataimportMemberImportService;
import cn.lonsun.source.dataexport.organperson.IMemberExportService;
import cn.lonsun.target.datamodel.organperson.MemberExportQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by lonsun on 2018-3-16.
 */
@Controller
@RequestMapping("memberImport")
public class MemberImportController extends BaseController {
    @Resource(name = "ex7MemberExportService")
    private IMemberExportService memberExportService;
    @Autowired
    private IDataimportMemberImportService memberImportService;


    @RequestMapping("index")
    public Object index(){



        return "";
    }

    /**
     * 获取老数据列表
     * @param memberExportQueryVO
     *
     * @return
     */
    @RequestMapping("getOldPage")
    @ResponseBody
    public Object getOldPage(MemberExportQueryVO memberExportQueryVO){
        Pagination pagination = memberImportService.getRelationPage(memberExportQueryVO);
        return getObject(pagination);
    }

    @RequestMapping("importMember")
    @ResponseBody
    public Object importMember(String importType, @RequestParam(value = "oldIds",required = false)Long[] oldIds,@RequestParam(value = "ids",required = false)Long[] ids){

         return getObject();
    }

}
