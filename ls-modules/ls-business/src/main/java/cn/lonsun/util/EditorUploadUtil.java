package cn.lonsun.util;

import cn.lonsun.GlobalConfig;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.enums.SystemCodes;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.util.Jacksons;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Hewbing
 * @ClassName: EditorUploadUtil
 * @Description: editor upload file to mongodb
 * @date 2015年10月29日 上午8:07:07
 */
public class EditorUploadUtil {

    private static IMongoDbFileServer mongoDbFileServer = SpringContextHolder.getBean(IMongoDbFileServer.class);

    private static final GlobalConfig config = SpringContextHolder.getBean(GlobalConfig.class);

    /**
     * @param moduleEnum
     * @param request
     * @param response   设定文件
     * @return void 返回类型
     * @throws
     * @Title: uploadByKindEditor
     * @Description: kondeditor upload file util
     */
    public static void uploadByKindEditor(SystemCodes moduleEnum, HttpServletRequest request, HttpServletResponse response) {

        // 定义允许上传的文件扩展名
        HashMap<String, String> extMap = new HashMap<String, String>();
        extMap.put("image", "gif,jpg,jpeg,png,bmp");
        extMap.put("flash", "swf,flv");
        extMap.put("video", "wav,flv,mp4,rmvb,rm,asf,mpg,avi,wmv");
        extMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
        extMap.put("file", "doc,docx,xls,xlsx,ppt,htm,html,xml,sql,txt,zip,rar,gz,bz2,pdf,gif,jpg,jpeg,png,bmp");//客户要求附件里面可以上传图片

        // 最大文件大小
        long maxSize = 200 * 1048576;
        Long siteId = LoginPersonUtil.getSiteId();
        response.setContentType("text/html; charset=UTF-8");

        if (!ServletFileUpload.isMultipartContent(request)) {
            outPrintErrorMsg(response, "Please select a file.");
            return;
        }
        String dirName = request.getParameter("dir");
        if (dirName == null) {
            dirName = "image";
        }
        MultipartHttpServletRequest multipartRequest = WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
        MultipartFile file1 = multipartRequest.getFile("imgFile");

        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("UTF-8");
        List<?> items = null;
        try {
            items = upload.parseRequest(request);
        } catch (FileUploadException e1) {
            outPrintErrorMsg(response, "Error uploading file.");
        }
        System.out.println("items.size() = " + items.size());
        Iterator<?> itr = items.iterator();
        System.out.println("itr.hasNext() = " + itr.hasNext());
        String fileExt = "";
        String fileName = "";
        String filePath = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String newFileName = "";
        Date date = new Date();
        MongoFileVO mgvo = new MongoFileVO();
        Long columnId = null;
        if (!AppUtil.isEmpty(request.getParameter("siteId"))) {
            siteId = Long.parseLong(request.getParameter("siteId"));
        }
        if (!AppUtil.isEmpty(request.getParameter("columnId"))) {
            columnId = Long.parseLong(request.getParameter("columnId"));
        }
        ColumnTypeConfigVO modelvo = ModelConfigUtil.getCongfigVO(columnId, siteId);
        int contentWidth = 0;
        if (modelvo != null && !AppUtil.isEmpty(modelvo.getContentWidth())) {
            contentWidth = modelvo.getContentWidth();
        } else {
            String imgWidth = request.getParameter("imgWidth");// 图片宽度
            if (StringUtils.isEmpty(imgWidth)) {
                contentWidth = Integer.parseInt(imgWidth);// 自定义上传的宽度
            }
        }
        if (items.isEmpty()) {
            MultipartHttpServletRequest req = WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
            List<MultipartFile> files = req.getFiles("imgFile");
            for (MultipartFile file : files) {
                fileName = file.getOriginalFilename();
                // 检查文件大小
                if (file.getSize() > maxSize) {
                    outPrintErrorMsg(response, "Upload file size exceeds the limit.");
                    return;
                }
                // 检查扩展名
                fileExt = fileName.substring(fileName.lastIndexOf(".") + 1)
                    .toLowerCase();
                if (!Arrays.<String>asList(extMap.get(dirName).split(",")).contains(fileExt)) {
                    outPrintErrorMsg(response, "只允许：\n " + extMap.get(dirName) + "格式.");
                    return;

                }
                newFileName = df.format(date) + "_" + new Random().nextInt(1000) + "." + fileExt;
                try {
                    if (!AppUtil.isEmpty(siteId) && checkPic(fileExt)) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImgHander.ImgTrans(file.getInputStream(), contentWidth, 0, baos, fileExt);
                        byte[] b = baos.toByteArray();
                        byte[] bt = WaterMarkUtil.createWaterMark(new ByteArrayInputStream(b), siteId, fileExt);
                        mgvo = FileUploadUtil.editorUpload(bt, fileName, FileCenterEO.Type.EditorUpload.toString(), FileCenterEO.Code.EditorAttach.toString(), siteId, columnId, null, "编辑器上传附件", request);
                    } else {
                        mgvo = FileUploadUtil.editorUpload(file, FileCenterEO.Type.EditorUpload.toString(), FileCenterEO.Code.EditorAttach.toString(), siteId, columnId, null, "编辑器上传附件", request);
                    }
                    // update by zhongjun 20171206 : 改成使用带后缀的文件名访问文件
                    filePath = mgvo.getFileName();
                } catch (Exception e) {
                    outPrintErrorMsg(response, "Upload failed.");
                    return;

                }
                Map<String, Object> msg = new HashMap<String, Object>();
                msg.put("error", 0);
                if ("image".equals(dirName)) {
//                    msg.put("url", config.getFileServerPath() + filePath);
                    msg.put("url", config.getFileServerNamePath() + filePath);
                } else {
                    msg.put("url", "/download/" + filePath);
                }
                msg.put("filename", fileName);
                outPrintMsg(response, msg);
                return;
            }
        } else {
            // 普通上传
            while (itr.hasNext()) {
                FileItem item = (FileItem) itr.next();
                fileName = item.getName();
                System.out.println("fileName = " + fileName);
                if (!item.isFormField()) {
                    // 检查文件大小
                    if (item.getSize() > maxSize) {
                        throw new BaseRunTimeException();
                        // writeMsg(response, "上传文件大小超过限制。");
                    }
                    // 检查扩展名
                    fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

                    if (!Arrays.<String>asList(extMap.get(dirName).split(",")).contains(fileExt)) {
                        outPrintErrorMsg(response, "只允许：\n " + extMap.get(dirName) + "格式.");
                    }

                    newFileName = df.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExt;
                    try {
                        InputStream is = item.getInputStream();
                        if (AppUtil.isEmpty(siteId) && checkPic(fileExt)) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImgHander.ImgTrans(is, contentWidth, 0, baos, fileExt);
                            byte[] b = baos.toByteArray();

                            byte[] bt = WaterMarkUtil.createWaterMark(new ByteArrayInputStream(b), siteId, fileExt);
                            mgvo = FileUploadUtil.editorUpload(bt, fileName, FileCenterEO.Type.EditorUpload.toString(), FileCenterEO.Code.EditorAttach.toString(), siteId, columnId, null, "编辑器上传附件", request);
                        } else {
                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            byte[] data = new byte[4096];
                            int count = -1;
                            while ((count = is.read(data, 0, 4096)) != -1)
                                outStream.write(data, 0, count);
                            data = null;
                            byte[] b = outStream.toByteArray();
                            mgvo = FileUploadUtil.editorUpload(b, fileName, FileCenterEO.Type.EditorUpload.toString(), FileCenterEO.Code.EditorAttach.toString(), siteId, columnId, null, "编辑器上传附件", request);
                        }
                        filePath = mgvo.getFileName();
                    } catch (Exception e) {
                        outPrintErrorMsg(response, "Upload failed.");
                        return;

                    }
                    Map<String, Object> msg = new HashMap<String, Object>();
                    msg.put("error", 0);
                    msg.put("url", config.getFileServerNamePath() + filePath);
                    msg.put("filename", fileName);
                    outPrintMsg(response, msg);

                }
            }
        }
    }

    /**
     * 输出Msg给KindEdit
     *
     * @param response
     * @param msg
     */
    private static void outPrintMsg(HttpServletResponse response, Object msg) {
        /************ 以下是解决避免ie下载文件 start *************/
        response.reset();
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.println(Jacksons.json().fromObjectToJson(msg));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        /************ 以下是解决避免ie下载文件 end *************/
    }

    public static boolean checkPic(String suffix) {
        String[] picSuffix = new String[]{"png", "jpg", "jpeg", "bmp"};
        for (int i = 0; i < picSuffix.length; i++) {
            if (picSuffix[i].equals(suffix)) {
                return true;
            }
        }
        return false;

    }


    /**
     * 输出错误的信息
     *
     * @param response
     * @param message
     * @Time 2014年9月18日 上午10:29:15
     */
    private static void outPrintErrorMsg(HttpServletResponse response,
                                         String message) {
        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put("error", 1);
        msg.put("message", message);
        outPrintMsg(response, msg);
    }

    public static String getType(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            String suffix = fileName.substring(index + 1);// 后缀
            return suffix;
        } else {
            return null;
        }
    }

    public static void uploadByKindEditorNew(SystemCodes contentmgr, HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html; charset=UTF-8");

        //参数获取
        Long siteId = LoginPersonUtil.getSiteId();
        String downLoadIp = config.getFileServerNamePath();
        Long columnId = null;
        if (!AppUtil.isEmpty(request.getParameter("siteId"))) {
            siteId = Long.parseLong(request.getParameter("siteId"));
        }
        if (!AppUtil.isEmpty(request.getParameter("columnId"))) {
            columnId = Long.parseLong(request.getParameter("columnId"));
        }
        // 最大文件大小
        long maxSize = 10000000;
        if (!ServletFileUpload.isMultipartContent(request)) {
            outPrintErrorMsg(response, "Please select a file.");
            return;
        }
        MultipartHttpServletRequest multipartRequest = WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
        MultipartFile file = multipartRequest.getFile("imgFile");
        // 普通上传
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            System.out.println("fileName = " + fileName);
            // 检查扩展名
            String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            if (!fileCode.doc.toString().equals(fileExt) && !fileCode.docx.toString().equals(fileExt)
                && !fileCode.xls.toString().equals(fileExt) && !fileCode.xlsx.toString().equals(fileExt)
                && !fileCode.ppt.toString().equals(fileExt) && !fileCode.pptx.toString().equals(fileExt)
                && !fileCode.pdf.toString().equals(fileExt)) {
                outPrintErrorMsg(response, "文件类型不正确，支持doc,docx,xls,xlsx,ppt,pptx,pdf");
            }
            try {
                InputStream is = file.getInputStream();
                String html = "";
                if (fileCode.doc.toString().equals(fileExt)) {
                    html = POIReadWordToHtml.convertDocHtml(is, siteId, columnId, downLoadIp, request)
                            .replace("<table","<table border=\"1\"");
                } else if (fileCode.docx.toString().equals(fileExt)) {
                    html = POIReadWordToHtml.convertDocxHtml(is, siteId, columnId, downLoadIp, request)
                            .replace("<table","<table border=\"1\"");
                } else if (fileCode.ppt.toString().equals(fileExt)) {
                    html = POIReadPPTToHtml.convertPptHtml(is, siteId, columnId, downLoadIp, request);
                } else if (fileCode.pptx.toString().equals(fileExt)) {
                    html = POIReadPPTToHtml.convertPptxHtml(is, siteId, columnId, downLoadIp, request);
                } else if (fileCode.pdf.toString().equals(fileExt)) {
                    html = PdfBoxReadPdfToHtml.convertPdfHtml(is, siteId, columnId, downLoadIp, request)
                            .replace("&lt;","<").replace("&gt;",">").replace("&quot;","\"");
                } else {
                    html = POIReadExcelToHtml.readExcelToHtml(is, true);
                }
                if (!StringUtils.isEmpty(html)) {

                    html = html.replace("<table","<table border=\"1\"");

                    JSONObject json = new JSONObject();
                    json.put("error", 0);
                    json.put("content", parseHtml(html));
                    outPrintMsg(response, json);
                } else {
                    outPrintErrorMsg(response, "Upload failed.");
                }
            } catch (Exception e) {
                outPrintErrorMsg(response, "Upload failed.");
            }

        } else {
            outPrintErrorMsg(response, "Upload failed.");
        }
    }

    //特殊字符转义
    private static Object parseHtml(String html) {
        return HtmlUtils.htmlEscapeHex(html).replaceAll("&#x22;", "&#x27;");
    }

    enum fileCode {
        doc,
        docx,
        xls,
        xlsx,
        ppt,
        pptx,
        pdf;
    }
}
