package cn.lonsun.govbbs.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.govbbs.internal.entity.BbsFileEO;
import cn.lonsun.govbbs.internal.service.IBbsFileService;
import cn.lonsun.govbbs.internal.vo.BbsMemberVO;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;

/**
 * Created by zhangchao on 2017/1/7.
 */
@Controller
@RequestMapping(value = "/govbbs/static/file")
public class GovBbsStaticFileController extends BaseController {


    @Value("${bbs.path}")
    private String bbsPath;


    @Autowired
    private IBbsFileService bbsFileService;


    @Autowired
    private IMongoDbFileServer mongoDbFileServer;


    @RequestMapping("fileUpload")
    @ResponseBody
    public Object fileUpload(MultipartFile Filedata, HttpServletRequest request, BbsFileEO file, String sessionId) {
        if (Filedata.isEmpty()) {
            return ajaxErr("文件上传失败(File upload failed)");
        }
        try{
            BbsMemberVO member = (BbsMemberVO) request.getSession().getAttribute("bbsUser");
            if(member != null){
                file.setCreateUserId(member.getId());
                file.setCreateOrganId(member.getMemberRoleId());
                file.setCreateUserName(member.getName());
            }
            bbsFileService.fileUpload(Filedata,request,file);
            file.setStatus(1);
        }catch (Exception e){
            return ajaxErr("文件上传失败(File upload failed)");
        }
        return file;
    }

    /**
     * @param mongoId
     * @param request
     * @param response
     * @return void
     * @throws
     * @Title: download
     * @Description: file download public interface
     */
    @RequestMapping("download")
    public void download(String mongoId,Long id,HttpServletRequest request, HttpServletResponse response) {

        try {

            if(!StringUtils.isEmpty(mongoId)){
                mongoDbFileServer.downloadFile(response, mongoId, null);
            }else if (id != null){
                BbsFileEO eo = bbsFileService.getEntity(BbsFileEO.class,id);
                if(eo == null){
                    throw new BaseRunTimeException(TipsMode.Message.toString(), "附件不存在");
                }
                File file = new File(bbsPath + eo.getMongoId());
                System.out.println("111111111"+bbsPath + eo.getMongoId());
                System.out.println("22222"+file);
                response.setContentType("application/octet-stream");
                String agent = request.getHeader("User-Agent").toLowerCase();
                if (!StringUtils.isEmpty(agent) && agent.indexOf("firefox") > 0) {
                    response.addHeader("Content-Disposition", "attachment;filename=" + new String(eo.getFileName().getBytes("GB2312"), "ISO-8859-1"));
                } else {
                    response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(eo.getFileName(), "UTF-8"));
                }
                FileInputStream inputStream = new FileInputStream(file);
                ServletOutputStream out;
                out = response.getOutputStream();
                int b = 0;
                byte[] buffer = new byte[1024];
                while (b != -1) {
                    b = inputStream.read(buffer);
                    out.write(buffer, 0, b);
                }
                inputStream.close();
                out.close();
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
//            throw new BaseRunTimeException(TipsMode.Message.toString(), "文件下载失败！");
        }
    }

    /**
     * @param ids
     * @throws isDel 0 逻辑删除 1 物理删除
     */
    @RequestMapping("deleteFiles")
    @ResponseBody
    public Object deleteFiles(Long[] ids) {
        //判断是否为超级管理员或root
        if (ids.length <= 0) {
            return ajaxErr("参数不能为空");
        }
        bbsFileService.deleteWebFiles(ids);
        return getObject();
    }


}
