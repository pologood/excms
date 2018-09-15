package cn.lonsun.common.upload.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.lonsun.common.upload.internal.entity.AttachmentEO;
import cn.lonsun.common.upload.internal.service.IUploadService;
import cn.lonsun.common.util.FileUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.enums.SystemCodes;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.AjaxObj;
import cn.lonsun.core.util.TipsMode;

@Controller
@RequestMapping(value = "upload")
public class UploadController extends BaseController{

    @Autowired
    private IUploadService uploadService;
    /**
     * 上传附件的Controller
     * @Time 2014年9月23日 下午7:59:26
     * @param request
     * @param files
     * @return
     * @throws UnsupportedEncodingException 
     */
    @RequestMapping("uploadAttachment")
    @ResponseBody
    public Object uploadAttachment(HttpServletRequest request, @RequestParam("Filedata") MultipartFile[] files,String moduleName,Integer storageLocation)
                    throws UnsupportedEncodingException{
        if(files==null || files.length==0){
            throw new BaseRunTimeException(TipsMode.Key.toString(),"AttachmentEOFilesNotNull");
        }
        SystemCodes module=null;
        try {
            module = SystemCodes.valueOf(moduleName);
        } catch (Exception e) {
            throw new BaseRunTimeException(TipsMode.Message.toString(),"模块名称不正确");
        }
        String rootPath = request.getSession().getServletContext().getRealPath("/");
        List<AttachmentEO> attachs = uploadService.uploadFile(files, module,rootPath,storageLocation);
        //循环获取file数组中得文件  
        return AjaxObj.Ok(attachs);
    }
    /**
     * 删除附件(本平台)
     * 
     * @param attachmentId
     * @param request
     */
    @RequestMapping("deleteAttach")
    @ResponseBody
    public Object deleteAttachment(Long attachmentId,HttpServletRequest request){
        uploadService.deleteFile(attachmentId,request.getSession().getServletContext().getRealPath("/"));
        return getObject();
    }

    /**
     * 删除附件(跨平台)
     * 
     * @param attachmentId
     * @param request
     */
    @RequestMapping("deleteAttachByUuid")
    @ResponseBody
    public Object deleteAttachment(String attachmentUuid,HttpServletRequest request){
        uploadService.deleteFile(attachmentUuid,request.getSession().getServletContext().getRealPath("/"));
        return getObject();
    }
    
    @RequestMapping("downLoadFile")
    public void downLoadFile(Long attachId,HttpServletResponse response) throws Exception {
        AttachmentEO att = uploadService.getEntity(AttachmentEO.class,attachId);
        if(att!=null){
//            FileUtil.downLoadFile(att.getFilePath(),att.getFileName(),response);
            uploadService.downLoadFile(att.getFilePath(),att.getFileName(), response);
        }
    }

    @RequestMapping("downLoadFileByUuid")
    public void downLoadFile(String uuid,HttpServletResponse response) throws Exception {
        AttachmentEO att = uploadService.getAttachmentEO(uuid);
        if(att!=null){
//            FileUtil.downLoadFile(att.getFilePath(),att.getFileName(),response);
            uploadService.downLoadFile(att.getFilePath(),att.getFileName(), response);
        }
    }
    
    @RequestMapping("getAttachment")
    @ResponseBody
    public Object getAttachment(String attachIds){
        String[] array = attachIds.split(",");
        List<Long> list = new ArrayList<Long>();
        for (String str : array) {
            list.add(Long.parseLong(str));
        }
        List<AttachmentEO> attList = uploadService.getEntities(AttachmentEO.class,list);
        return getObject(attList);
    }
 
    @RequestMapping("getAttachmentByUuid")
    @ResponseBody
    public Object getAttachmentByUuid(String attachIds){
        String[] array = attachIds.split(",");
        List<AttachmentEO> attList = uploadService.getAttachs(array);
        return getObject(attList);
    }   
}
