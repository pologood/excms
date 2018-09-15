package cn.lonsun.system.filecenter.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.util.FileUtil;
import cn.lonsun.content.vo.VideoNewsVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.*;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.shiro.util.OnlineSessionUtil;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.site.internal.entity.WaterMarkConfigEO;
import cn.lonsun.site.site.internal.service.IWaterMarkConfigService;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.system.filecenter.internal.service.IFileCenterService;
import cn.lonsun.system.filecenter.internal.vo.FileCenterVO;
import cn.lonsun.util.*;
import cn.lonsun.util.ZipUtil;
import com.mongodb.gridfs.GridFSDBFile;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Hewbing
 * @ClassName: FileCenterController
 * @Description: filecenter controller
 * @date 2015年11月23日 上午9:29:15
 */
@Controller
@RequestMapping(value = "fileCenter")
public class FileCenterController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(FileCenterController.class);

    private static String PNG = "png";
    private static String JPG = "jpg";
    private static String GIF = "gif";
    private static String SWF = "swf";

    @Autowired
    private IMongoDbFileServer mongoDbFileServer;

    @Autowired
    private IFileCenterService fileCenterService;

    @Autowired
    private IWaterMarkConfigService waterMarkConfigService;

    @Value("${fileServerPath}")
    private String fileServerPath;

    @Value("${fileServerNamePath}")
    private String fileServerNamePath;

    @RequestMapping("index")
    public String index(ModelMap map) {
        List<DataDictVO> dict = DataDictionaryUtil.getDDList("file_type");
        map.put("DICT", dict);
        return "/system/filecenter/index";
    }

    @RequestMapping("winIndex")
    public String winIndex(ModelMap map) {
        List<DataDictVO> dict = DataDictionaryUtil.getDDList("file_type");
        map.put("DICT", dict);
        return "/system/filecenter/winIndex";
    }

    @RequestMapping("fileListPage")
    public String fileListPage() {
        return "/system/filecenter/file_list";

    }

    @RequestMapping("text")
    public String text() {
        return "/system/filecenter/testUpload";
    }

    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(FileCenterVO fileVO) {
        Pagination page = fileCenterService.getFilePage(fileVO);
        return getObject(page);
    }

    /**
     * @param ids
     * @return Object   返回类型
     * @throws
     * @Title: deleteFile
     * @Description: 从mongodb彻底删除文件，判断是否超级管理员  delete from mongodb ,Judge to is Super administrator
     */
    @RequestMapping("deleteFile")
    @ResponseBody
    public Object deleteFile(@RequestParam(value = "ids[]", required = false) Long[] ids) {
        //判断是否为超级管理员或root
        if (ids.length <= 0) {
            return ajaxErr("参数不能为空(Parameters can not be empty)");
        }
        if (LoginPersonUtil.isSuperAdmin() || LoginPersonUtil.isRoot()) {
            fileCenterService.removeFromDir(ids);
            return getObject();
        } else {
            return ajaxErr("您无此权限(You do not have this permission)");
        }
    }

    @RequestMapping("cleanUp")
    @ResponseBody
    public Object cleanUp(@RequestParam(value = "ids[]", required = false) Long[] ids) {

        //判断是否为超级管理员或root
        if (ids.length <= 0) {
            return ajaxErr("参数不能为空(Parameters can not be empty)");
        }
        if (LoginPersonUtil.isSuperAdmin() || LoginPersonUtil.isRoot()) {
            fileCenterService.cleanUp(ids);
            return getObject();
        } else {
            return ajaxErr("您无此权限(You do not have this permission)");
        }

    }

    @RequestMapping("zipDownload")
    public void zipDownload(HttpServletRequest request, HttpServletResponse response, String mongoIds) {
        String[] str = mongoIds.split("_");
        ZipUtil.zip(str, response);
    }

    @RequestMapping("deleteByMongoIds")
    @ResponseBody
    public Object deleteByMongoIds(@RequestParam(value = "mongoIds[]", required = false) String[] mongoIds) {
        fileCenterService.deleteByMongoId(mongoIds);
        return getObject();
    }

    @RequestMapping("deleteByMongoId")
    @ResponseBody
    public Object deleteByMongoId(String mongoId) {
        fileCenterService.deleteByMongoId(new String[]{mongoId});
        return getObject();
    }

    //the public file upload Interface
    @RequestMapping("fileUpload")
    @ResponseBody
    public Object fileUpload(MultipartFile Filedata, HttpServletRequest request ,String sessionId) {

        /**
         * 参数自动注入失败，现采用手动设置方式
         */
        FileCenterEO fileEO = new FileCenterEO();
        fileEO.setColumnId(AppUtil.getLong(request.getParameter("columnId")));
        fileEO.setSiteId(AppUtil.getLong(request.getParameter("siteId")));
        fileEO.setType(AppUtil.getValue(request.getParameter("type")));
        fileEO.setCode(AppUtil.getValue(request.getParameter("code")));
        fileEO.setContentId(AppUtil.getLong(request.getParameter("contentId")));
        fileEO.setDesc(AppUtil.getValue(request.getParameter("desc")));

        Session session = OnlineSessionUtil.getSessionById(sessionId);
        if (AppUtil.isEmpty(session)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "会话失效，请重新登入操作");
        }
        if (Filedata.isEmpty()) {
            return ajaxErr("文件上传失败(File upload failed)");
        }

        MongoFileVO mongoEO = null;
        String fileName = Filedata.getOriginalFilename();
        String fileSuffix = FileUtil.getType(fileName);
        if ((fileSuffix.equals(PNG) || fileSuffix.equals(JPG) || fileSuffix.equals(GIF)) && fileEO.getColumnId() != null && fileEO.getSiteId() != null) {
            WaterMarkConfigEO eo = waterMarkConfigService.getConfigBySiteId(fileEO.getSiteId());
            ColumnTypeConfigVO modelVO = ModelConfigUtil.getCongfigVO(fileEO.getColumnId(), fileEO.getSiteId());
            int width = 0;
            int heigth = 0;

            if (modelVO != null && !AppUtil.isEmpty(modelVO.getPicWidth())) {
                width = modelVO.getPicWidth();
            }
            if (modelVO != null && !AppUtil.isEmpty(modelVO.getPicHeight())) {
                heigth = modelVO.getPicHeight();
            }
            InputStream in = null;
            try {
                in = Filedata.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] b = null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (eo != null && eo.getEnableStatus() == 1 && modelVO != null && modelVO.getIsWater() == 1) {
                /*byte[] by = WaterMarkUtil.createWaterMark(in, fileEO.getSiteId(),fileSuffix);
                InputStream inNew = new ByteArrayInputStream(by);
                ImgHander.ImgTrans(inNew, width, heigth, baos, fileSuffix);*/

                ImgHander.ImgTrans(in, width, heigth, baos, fileSuffix);
                in = new ByteArrayInputStream(baos.toByteArray());
                b = WaterMarkUtil.createWaterMark(in, fileEO.getSiteId(), fileSuffix);

            } else if (modelVO != null) {
                ImgHander.ImgTransWithOutWater(in, width, heigth, baos, fileSuffix);
                b = baos.toByteArray();
            }
            mongoEO = mongoDbFileServer.uploadByteFile(b, fileName, null, null);

            /*mongoEO = FileUploadUtil.uploadUtil(b, fileName, FileCenterEO.Type.Image.toString(), FileCenterEO.Code.ThumbUpload.toString(), fileEO.getSiteId(), fileEO.getColumnId(),
                fileEO.getContentId(), "新闻缩略图", LoginPersonUtil.getRequest());//此处多保存了一次*/
        } else {
            mongoEO = mongoDbFileServer.uploadMultipartFile(Filedata, null);
        }

        String singleName = fileName.substring(0, fileName.lastIndexOf("."));
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        try {
            fileEO.setMd5(MD5Util.getMd5ByByte(Filedata.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileEO.setFileSize(Filedata.getSize());
        fileEO.setFileName(singleName);
        fileEO.setSuffix(suffix);
        fileEO.setMongoId(mongoEO.getMongoId());
        fileEO.setMongoName(mongoEO.getFileName());
        fileEO.setStatus(0);
        fileEO.setCreateUserId((Long) session.getAttribute("userId"));
        fileEO.setCreateOrganId((Long) session.getAttribute("organId"));
        if (AppUtil.isEmpty(fileEO.getSiteId())) {
            fileEO.setSiteId((Long) session.getAttribute("siteId"));
        }
        if (AppUtil.isEmpty(fileEO.getType())) {
            fileEO.setType(FileCenterEO.Type.NotDefined.toString());
        }
        if (AppUtil.isEmpty(fileEO.getCode())) {
            fileEO.setType(FileCenterEO.Code.Default.toString());
        }
        if (AppUtil.isEmpty(fileEO.getDesc())) {
            fileEO.setDesc("默认端口上传的附件");
        }
        HttpServletRequest request2 = LoginPersonUtil.getRequest();
        fileEO.setIp(IpUtil.getIpAddr(request2));
        fileCenterService.saveEntity(fileEO);
        logger.info("File upload successed ,mongoid: >>> " + mongoEO.getMongoId());
        return mongoEO;
    }

    @RequestMapping("UeUpload")
    @ResponseBody
    public Object UeUpload(MultipartFile Filedata, FileCenterEO fileEO, String sessionId) {

        Session session = OnlineSessionUtil.getSessionById(sessionId);
        if (AppUtil.isEmpty(session)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "会话失效，请重新登入操作");
        }
        if (Filedata.isEmpty()) {
            return UeditorJSON.Info("上传源不能为空", "", "", "");
        }

        MongoFileVO mongoEO = null;
        String fileName = Filedata.getOriginalFilename();
        String fileSuffix = FileUtil.getType(fileName);
        if ((fileSuffix.equals(PNG) || fileSuffix.equals(JPG) || fileSuffix.equals(GIF)) && fileEO.getColumnId() != null && fileEO.getSiteId() != null) {
            WaterMarkConfigEO eo = waterMarkConfigService.getConfigBySiteId(fileEO.getSiteId());
            ColumnTypeConfigVO modelVO = ModelConfigUtil.getCongfigVO(fileEO.getColumnId(), fileEO.getSiteId());
            int width = 0;
            int heigth = 0;

            if (modelVO != null && !AppUtil.isEmpty(modelVO.getPicWidth())) {
                width = modelVO.getPicWidth();
            }
            if (modelVO != null && !AppUtil.isEmpty(modelVO.getPicHeight())) {
                heigth = modelVO.getPicHeight();
            }
            InputStream in = null;
            try {
                in = Filedata.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] b = null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (eo != null && eo.getEnableStatus() == 1 && modelVO != null && modelVO.getIsWater() == 1) {
                /*byte[] by = WaterMarkUtil.createWaterMark(in, fileEO.getSiteId(),fileSuffix);
                InputStream inNew = new ByteArrayInputStream(by);
                ImgHander.ImgTrans(inNew, width, heigth, baos, fileSuffix);*/

                ImgHander.ImgTrans(in, width, heigth, baos, fileSuffix);
                in = new ByteArrayInputStream(baos.toByteArray());
                b = WaterMarkUtil.createWaterMark(in, fileEO.getSiteId(), fileSuffix);

            } else if (modelVO != null) {
                ImgHander.ImgTrans(in, width, heigth, baos, fileSuffix);
                b = baos.toByteArray();
            }
            mongoEO = FileUploadUtil.uploadUtil(b, fileName, FileCenterEO.Type.Image.toString(), FileCenterEO.Code.ThumbUpload.toString(), fileEO.getSiteId(), fileEO.getColumnId(),
                fileEO.getContentId(), "新闻缩略图", LoginPersonUtil.getRequest());
        } else {
            mongoEO = mongoDbFileServer.uploadMultipartFile(Filedata, null);
        }

        String singleName = fileName.substring(0, fileName.lastIndexOf("."));
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        try {
            fileEO.setMd5(MD5Util.getMd5ByByte(Filedata.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileEO.setFileSize(Filedata.getSize());
        fileEO.setFileName(singleName);
        fileEO.setSuffix(suffix);
        fileEO.setMongoId(mongoEO.getMongoId());
        fileEO.setMongoName(mongoEO.getFileName());
        fileEO.setStatus(0);
        fileEO.setCreateUserId((Long) session.getAttribute("userId"));
        fileEO.setCreateOrganId((Long) session.getAttribute("organId"));
        if (AppUtil.isEmpty(fileEO.getSiteId())) {
            fileEO.setSiteId((Long) session.getAttribute("siteId"));
        }
        if (AppUtil.isEmpty(fileEO.getType())) {
            fileEO.setType(FileCenterEO.Type.NotDefined.toString());
        }
        if (AppUtil.isEmpty(fileEO.getCode())) {
            fileEO.setType(FileCenterEO.Code.Default.toString());
        }
        if (AppUtil.isEmpty(fileEO.getDesc())) {
            fileEO.setDesc("默认端口上传的附件");
        }
        HttpServletRequest request2 = LoginPersonUtil.getRequest();
        fileEO.setIp(IpUtil.getIpAddr(request2));
        fileCenterService.saveEntity(fileEO);
        logger.info("File upload successed ,mongoid: >>> " + mongoEO.getMongoId());

        return UeditorJSON.Info("SUCCESS", fileName, fileServerNamePath + mongoEO.getFileName(), "");
    }

    @RequestMapping("checkFile")
    @ResponseBody
    public Object checkFile(Long id) {
        FileCenterEO fileEO = fileCenterService.getEntity(FileCenterEO.class, id);
        GridFSDBFile file = null;
        String fileMD5 = null;
        try {
            file = mongoDbFileServer.getGridFSDBFile(fileEO.getMongoId(), null);
            fileMD5 = file.getMD5();//MD5Util.getMd5ByByte(b);
        } catch (Exception e) {
            e.printStackTrace();
            return ajaxErr("文件不存在或被破坏");
        }
        if (file.getLength() < (1024 * 1024 * 20)) {
            byte[] b = WaterMarkUtil.inputStreamToByte(file.getInputStream());
            fileMD5 = MD5Util.getMd5ByByte(b);
        }
        if (fileMD5.equals(fileEO.getMd5()) && fileEO.getFileSize() == file.getLength()) {
            return ajaxOk("文件检测正常");
        } else {
            return ajaxErr("文件已被串改或破坏");
        }
    }

    @RequestMapping("updateFile")
    @ResponseBody
    public Object updateFile(MultipartFile Filedata, FileCenterEO fileEO, String mongoId, String desc, String sessionId) {
        Session session = OnlineSessionUtil.getSessionById(sessionId);
        if (AppUtil.isEmpty(session)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "会话失效，请重新登入操作");
        }
        if (Filedata.isEmpty()) {
            return UeditorJSON.Info("上传源不能为空", "", "", "");
        }

        if (AppUtil.isEmpty(mongoId)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "mongoId 不能为空！");
        }

        MongoFileVO mongoEO = mongoDbFileServer.uploadMultipartFile(Filedata, null, mongoId);
        String fileName = Filedata.getOriginalFilename();
        String singleName = fileName.substring(0, fileName.lastIndexOf("."));
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        try {
            fileEO.setMd5(MD5Util.getMd5ByByte(Filedata.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileEO.setFileSize(Filedata.getSize());
        fileEO.setFileName(singleName);
        fileEO.setSuffix(suffix);
        fileEO.setMongoId(mongoEO.getMongoId());
        fileEO.setStatus(0);
        fileEO.setCreateUserId((Long) session.getAttribute("userId"));
        fileEO.setCreateOrganId((Long) session.getAttribute("organId"));
        if (AppUtil.isEmpty(fileEO.getSiteId())) {
            fileEO.setSiteId((Long) session.getAttribute("siteId"));
        }
        if (AppUtil.isEmpty(fileEO.getType())) {
            fileEO.setType(FileCenterEO.Type.NotDefined.toString());
        }
        if (AppUtil.isEmpty(fileEO.getCode())) {
            fileEO.setType(FileCenterEO.Code.Default.toString());
        }
        fileEO.setDesc(desc);
        if (AppUtil.isEmpty(fileEO.getDesc())) {
            fileEO.setDesc("默认端口上传的附件");
        }
        HttpServletRequest request2 = LoginPersonUtil.getRequest();
        fileEO.setIp(IpUtil.getIpAddr(request2));
        fileCenterService.updateEntity(fileEO);
        logger.info("File upload successed ,mongoid: >>> " + mongoEO.getMongoId());
        return mongoEO;
    }


    @RequestMapping("normalUpload")
    @ResponseBody
    public Object normalUpload(MultipartFile Filedata, HttpServletRequest request) {

        HttpSession session = request.getSession();
        if (AppUtil.isEmpty(session)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "会话失效，请重新登入操作");
        }
        if (Filedata.isEmpty()) {
            return ajaxErr("文件上传失败(File upload failed)");
        }
        FileCenterEO fileEO = new FileCenterEO();
        MongoFileVO mongoEO = mongoDbFileServer.uploadMultipartFile(Filedata, null);
        String fileName = Filedata.getOriginalFilename();
        String singleName = fileName.substring(0, fileName.lastIndexOf("."));
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        try {
            fileEO.setMd5(MD5Util.getMd5ByByte(Filedata.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileEO.setFileSize(Filedata.getSize());
        fileEO.setFileName(singleName);
        fileEO.setSuffix(suffix);
        fileEO.setMongoId(mongoEO.getMongoId());
        fileEO.setMongoName(mongoEO.getFileName());
        fileEO.setStatus(0);
        fileEO.setCreateUserId((Long) session.getAttribute("userId"));
        fileEO.setCreateOrganId((Long) session.getAttribute("organId"));
        if (AppUtil.isEmpty(fileEO.getSiteId())) {
            fileEO.setSiteId((Long) session.getAttribute("siteId"));
        }
        if (AppUtil.isEmpty(fileEO.getType())) {
            fileEO.setType(FileCenterEO.Type.NotDefined.toString());
        }
        if (AppUtil.isEmpty(fileEO.getCode())) {
            fileEO.setType(FileCenterEO.Code.Default.toString());
        }
        fileEO.setDesc("非Flash的普通上传");
        fileEO.setIp(IpUtil.getIpAddr(request));
        fileCenterService.saveEntity(fileEO);
        logger.info("File upload successed ,mongoid: >>> " + mongoEO.getMongoId());
        return mongoEO;
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
    @RequestMapping("download/{mongoId}")
    public void download(@PathVariable String mongoId, HttpServletRequest request, HttpServletResponse response) {
        mongoDbFileServer.downloadFile(response, mongoId, null);
    }

    //get into upload page
    @RequestMapping("uploadPage")
    public String uploadPage(ModelMap map, HttpServletRequest request) {
        logger.info("injection SESSIONID to the page for upload >>> " + request.getSession().getId());
        map.put("sessionId", request.getSession().getId());
        return "/system/filecenter/uploadPage";
    }

    @RequestMapping("playVideo")
    public String videoPlayer(Long id, Model model) {
        if (id != null) {
            FileCenterEO file = fileCenterService.getEntity(FileCenterEO.class, id);
            VideoNewsVO vo = new VideoNewsVO();
            vo.setVideoPath(file.getMongoId());
            vo.setVideoName(file.getFileName() + "." + file.getSuffix());
            // videoService.changeHit(id);
            if (vo != null) {
                if (vo.getVideoPath() == null || "".equals(vo.getVideoPath())) {
                    model.addAttribute("videoPath", "");
                    model.addAttribute("videoName", "");
                } else {
                    model.addAttribute("videoName", vo.getVideoName());
                    model.addAttribute("videoPath", vo.getVideoPath());
                }
            }
        }
        return "/content/video_news/video_player";
    }
}

