package cn.lonsun.fastdfs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.fastdfs.internal.entity.FastDFSEO;
import cn.lonsun.fastdfs.internal.service.IFastDFSService;

/**
 * Created by yy on 2014/8/12.
 */
@Controller
@RequestMapping("fastdfs")
public class FastDFSController extends BaseController {
    @Autowired
    private IFastDFSService fastDFSService;

    /**
     * 列表页
     *
     * @return
     */
    @RequestMapping("listPage")
    public String listPage(){
    	return "/app/mgr/fastdfs/fastdfsconfig_list";
    }
    /**
     * 编辑页
     *
     * @return
     */
    @RequestMapping("editPage")
    public String editPage(){
    	return "/app/mgr/fastdfs/fastdfsconfig_edit";
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @RequestMapping("getById")
    @ResponseBody
    public Object getById(Long id) {
        FastDFSEO eo;
        if(AppUtil.isEmpty(id)) {
            eo = new FastDFSEO();
        } else {
            eo = fastDFSService.getEntity(FastDFSEO.class, id);
        }
        return getObject(eo);
    }

    /**
     * 查询
     *
     * @author yy
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(Long pageIndex,Integer pageSize){
        if(pageIndex==null||pageIndex<0){
            pageIndex = 0L;
        }
        if(pageSize==null||pageSize<0){
            pageSize = 15;
        }
        Pagination page = null;
        page = fastDFSService.getPage(pageIndex, pageSize);
        return getObject(page);
    }

    /**
     * 保存
     *
     * @param eo
     * @return
     */
    @RequestMapping("save")
    @ResponseBody
    public Object save(FastDFSEO eo) {

        if(AppUtil.isEmpty(eo.getId())) {
            fastDFSService.saveEntity(eo);
        } else {
            fastDFSService.updateEntity(eo);
        }

        return getObject();
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @RequestMapping("del")
    @ResponseBody
    public Object del(Long id) {

        if(AppUtil.isEmpty(id)) {
            return ajaxErr("id不能为空！");
        }

        fastDFSService.delete(FastDFSEO.class, id);

        return getObject();
    }
}
