package cn.lonsun.govbbs.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.govbbs.internal.service.IBbsPostService;
import cn.lonsun.govbbs.internal.vo.*;
import cn.lonsun.govbbs.util.ExportExcel;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lonsun on 2016-12-23.
 */
@Controller
@RequestMapping(value = "bbsStatic", produces = { "application/json;charset=UTF-8" })
public class BbsStaticController extends BaseController {

    @Autowired
    private IBbsPostService bbsPostService;

    /**
     * 部门统计
     * @return
     */
    @RequestMapping("unitList")
    public String unitList(){

        return "/bbs/bbs_static";
    }

    /**
     * 部门统计
     * @return
     */
    @RequestMapping("getUnitList")
    @ResponseBody
    public Object getUnitList(PostQueryVO query){
        query.setIsPage(1);
       Pagination pagination =bbsPostService.getUnitList(query);
        return getObject(pagination) ;
    }

    @RequestMapping("/downUnit")
    @ResponseBody
    public void downUnit(PostQueryVO query, HttpServletResponse response ){
        query.setIsPage(0);
        Pagination page =bbsPostService.getUnitList(query);
        List<StaticUnitVO> staticUnitVOs =(List<StaticUnitVO> )page.getData();

        ExportExcel<StaticUnitVO>  exportExcel=new ExportExcel();
        String[] colums = {"name","replyCount","unReplyCount","outCount","yellowCount","readCount","count","rate"};
        String[] headers = {"部门名称","已回复","未回复","逾期数","黄牌督办","红牌督办","案件总数","回复率"};
        String name = "部门统计表.xls";
        String title = "部门统计表";
        try {
            // 设定输出文件头
            response.setHeader("Content-disposition", "attachment; filename=" + new String(name.getBytes("GB2312"), "ISO8859-1"));
            response.setContentType("application/vnd.ms-excel");// 定义输出类型
            exportExcel.exportExcel(title, colums, headers, staticUnitVOs, response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }







    /**
     * 部门板块数据统计页
     * @return
     */
    @RequestMapping("/unitPlateList")
    public String unitPlateList(){


        return "/bbs/unit_plate_list";

    }





    @RequestMapping("/getUnitPlate")
    @ResponseBody
    public Object getUnitPlate(PostQueryVO query){
        query.setIsPage(1);

        if (query.getPageIndex()==null||query.getPageIndex() < 0) {
            query.setPageIndex(0L);
        }
        Integer size = query.getPageSize();
        if (size==null||size <= 0 || size > Pagination.MAX_SIZE) {
            query.setPageSize(15);
        }
        if(!LoginPersonUtil.isRoot()){
            if(!LoginPersonUtil.isSuperAdmin()){
                query.setAdmin(false);
                query.setUnitId(LoginPersonUtil.getUnitId());
            }
        }
        Pagination page = bbsPostService.getUnitPlate(query);

         return  getObject(page);
    }

    @RequestMapping("/downUnitPlate")
    @ResponseBody
    public void downUnitPlate(PostQueryVO query, HttpServletResponse response ){
        query.setIsPage(0);
        Pagination page = bbsPostService.getUnitPlate(query);
         List<UnitPalteVO> unitPalteVOs =(List<UnitPalteVO> )page.getData();
         List<StaticUnitPalteVO> staticUnitPalteVOs =new ArrayList<StaticUnitPalteVO>();
        if(unitPalteVOs!=null&&unitPalteVOs.size()>0){
            for(UnitPalteVO unitPalteVO: unitPalteVOs){
                StaticUnitPalteVO vo =new StaticUnitPalteVO();
                AppUtil.copyProperties(vo,unitPalteVO);

                if(vo.getIsAccept().equals(0)){
                    vo.setDealStatus("未回复");
                }
                 else if(vo.getIsAccept().equals(1)){

                    vo.setDealStatus("已回复");
                }

                staticUnitPalteVOs.add(vo);
            }
        }

        ExportExcel<StaticUnitPalteVO>  exportExcel=new ExportExcel();
        String[] colums = {"dealStatus","title","plateName","acceptUnitName","memberName","createDate","acceptTime","handleTime"};
        String[] headers = {"处理状态","标题","所属板块","督办部门","发帖人","发帖时间","督办时间","回复时间"};
        String name = "部门版块数据表.xls";
        String title = "部门版块数据表";
        try {
            // 设定输出文件头
            response.setHeader("Content-disposition", "attachment; filename=" + new String(name.getBytes("GB2312"), "ISO8859-1"));
            response.setContentType("application/vnd.ms-excel");// 定义输出类型
            exportExcel.exportExcel(title, colums, headers, staticUnitPalteVOs, response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    /**
     * 统计会员页面
     */
      @RequestMapping("/memberStatic")
      public String memberStatic(){


          return   "/bbs/member_static_list";
      }


    /**
     * 会员统计列表数据
     * @return
     */
    @RequestMapping("/getmemberStatic")
    @ResponseBody
     public Object getMemberStatic(PostQueryVO query){
         query.setIsPage(1);
         Pagination pagination =   bbsPostService.getMemberStatic(query);
         return pagination;
     }

    /**
     * 会员统计列表数据
     * @return
     */
    @RequestMapping("/downMember")
    @ResponseBody
    public void downMember(PostQueryVO query, HttpServletResponse response ){

        Pagination pagination =   bbsPostService.getMemberStatic(query);
        List<MemberStaticVO>   memberStaticVOs =( List<MemberStaticVO> )pagination.getData();

        ExportExcel<MemberStaticVO>  exportExcel=new ExportExcel();
        String[] colums = {"name","postCount","replyCount"};
        String[] headers = {"用户名","帖子数","回复数"};
        String name = "会员统计表.xls";
        String title = "会员统计表";
        try {
            // 设定输出文件头
            response.setHeader("Content-disposition", "attachment; filename=" + new String(name.getBytes("GB2312"), "ISO8859-1"));
            response.setContentType("application/vnd.ms-excel");// 定义输出类型
            exportExcel.exportExcel(title, colums, headers, memberStaticVOs, response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }



    }


    /**
     * 部门统计
     * @return
     */
    @RequestMapping("staticUnitPlate")
    public String staticUnitPlate(){

        return "/bbs/static_unit_plate";
    }


    /**
     * 会员统计列表
     * @return
     */
    @RequestMapping("/getStaticUnitPlate")
    @ResponseBody
    public Object getStaticUnitPlate(PostQueryVO query){
        query.setIsPage(1);
        Pagination pagination = bbsPostService.getStaticUnitPlate(query);
        return   getObject(pagination);

    }

    /**
     * 会员统计列表数据
     * @return
     */
    @RequestMapping("/downStaticUnitPlate")
    @ResponseBody
    public void downStaticUnitPlate(PostQueryVO query, HttpServletResponse response ){
        Pagination pagination =   bbsPostService.getStaticUnitPlate(query);
        List<UnitPlateStaticVO>  unitPlateStaticVOs =( List<UnitPlateStaticVO> )pagination.getData();

        ExportExcel<UnitPlateStaticVO>  exportExcel=new ExportExcel();
        String[] colums = {"name","plateName","replyCount","unReplyCount","outCount","yellowCount","readCount","count"};
        String[] headers = {"部门名称","版块名称","已回复","未回复","逾期数","黄牌督办","红牌督办","案件总数"};
        String name = "部门板块统计表.xls";
        String title = "部门板块统计表";
        try {
            // 设定输出文件头
            response.setHeader("Content-disposition", "attachment; filename=" + new String(name.getBytes("GB2312"), "ISO8859-1"));
            response.setContentType("application/vnd.ms-excel");// 定义输出类型
            exportExcel.exportExcel(title, colums, headers, unitPlateStaticVOs, response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }



    }



    /**
     * 部门统计
     * @return
     */
    @RequestMapping("unitReply")
    public String unitReply(){

        return "/bbs/unit_reply";
    }


    /**
     * 统计列表
     * @return
     */
    @RequestMapping("/getUnitReply")
    @ResponseBody
    public Object getUnitReply(PostQueryVO query){
        query.setIsPage(1);
        Pagination pagination = bbsPostService.getUnitReply(query);

        return   getObject(pagination);

    }

    /**
     * 部门回复
     * @return
     */
    @RequestMapping("/downUnitReply")
    @ResponseBody
    public void downUnitReply(PostQueryVO query, HttpServletResponse response ){
        Pagination pagination =   bbsPostService.getUnitReply(query);
        List<UnitReplyVO>  unitReplyVOs =( List<UnitReplyVO> )pagination.getData();

        ExportExcel<UnitReplyVO>  exportExcel=new ExportExcel();
        String[] colums = {"acceptUnitName","plateName","title","memberName","publishDate","ip","replyStatus","handleTime"};
        String[] headers = {"部门名称","版块名称","标题","发帖人","发帖时间","ip地址","回复状态","回复时间"};
        String name = "部门回复信息表.xls";
        String title = "部门回复信息表";
        try {
            // 设定输出文件头
            response.setHeader("Content-disposition", "attachment; filename=" + new String(name.getBytes("GB2312"), "ISO8859-1"));
            response.setContentType("application/vnd.ms-excel");// 定义输出类型
            exportExcel.exportExcel(title, colums, headers, unitReplyVOs, response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }



    }
}
