package cn.lonsun.content.filedownload;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.util.FileUtil;
import cn.lonsun.content.filedownload.internal.service.IFileDownloadService;
import cn.lonsun.content.filedownload.vo.FileDownloadVO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.site.internal.entity.WaterMarkConfigEO;
import cn.lonsun.site.site.internal.service.IWaterMarkConfigService;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;

/**
 * 文件下载控制层<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-23<br/>
 */
@Controller
@RequestMapping("fileDownload")
public class FileDownloadController extends BaseController {
    @Autowired
    private IFileDownloadService downloadService;

    @Autowired
    private IMongoDbFileServer mongoDbFileServer;

    @Autowired
    private IWaterMarkConfigService waterMarkConfigService;

    /**
     * 去往列表页面
     *
     * @param pageIndex
     * @param model
     * @return
     */
    @RequestMapping("index")
    private String index(Long pageIndex, Model model) {
        if (pageIndex == null) pageIndex = 0L;
        model.addAttribute("pageIndex", pageIndex);
        return "/content/filedownload/download_list";
    }

    /**
     * 去往编辑页面
     *
     * @param contentId
     * @param model
     * @return
     */
    @RequestMapping("edit")
    private String edit(Long contentId, Model model) {
        if (contentId == null) {
            model.addAttribute("contentId", "");
        } else {
            model.addAttribute("contentId", contentId);
        }
        return "/content/filedownload/download_edit";
    }

    /**
     * 去往图片上传页面
     *
     * @return
     */
    @RequestMapping("toUploadPic")
    private String toUploadPic() {
        return "/content/filedownload/upload_pic";
    }

    /**
     * 去往文件上传页面
     *
     * @return
     */
    @RequestMapping("toUploadFile")
    private String toUploadFile() {
        return "/content/filedownload/upload_file";
    }

    /**
     * 获取分页列表
     *
     * @param pageVO
     * @return
     */
    @RequestMapping("getPage")
    @ResponseBody
    private Object getPage(ContentPageVO pageVO) {
        Pagination page = downloadService.getPage(pageVO);
        return page;
    }

    /**
     * 根据主表ID获取文件下载信息
     *
     * @param id
     * @return
     */
    @RequestMapping("getVO")
    @ResponseBody
    public Object getVO(Long id) {
        FileDownloadVO vo = new FileDownloadVO();
        if (id == null) {
            vo.setSiteId(LoginPersonUtil.getSiteId());
            vo.setAddDate(new Date());
            return vo;
        } else {
            vo = downloadService.getVO(id);
            if (vo == null) {
                vo = new FileDownloadVO();
                vo.setSiteId(LoginPersonUtil.getSiteId());
            }
        }
        return vo;
    }

    /**
     * 保存编辑方法
     *
     * @param vo
     * @return
     */
    @RequestMapping("saveVO")
    @ResponseBody
    public Object saveVO(FileDownloadVO vo) {
        /*if(!AppUtil.isEmpty(vo.getRemarks())){
            String remarks= WordsSplitHolder.wordsRplc(vo.getText(), vo.getRemarks(), Type.SENSITIVE.toString());
            vo.setRemarks(remarks);
        }*/
        if (StringUtils.isEmpty(vo.getTitle())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "标题不能为空");
        }
        if (StringUtils.isEmpty(vo.getFileName())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "文件路径不能为空");
        }
        if (!StringUtils.isEmpty(vo.getRemarks()) && vo.getRemarks().length() > 1000) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "描述内容字数长度应为0～1000");
        }
        downloadService.saveVO(vo);
        if (vo.getIsPublish() == 1) {
            //处理保存时发布,正式部署时启用
            boolean rel = MessageSender.sendMessage(
                new MessageStaticEO(vo.getSiteId(), vo.getColumnId(), new Long[]{vo.getId()})
                    .setType(MessageEnum.PUBLISH.value()));
            if (rel) {
                return getObject(1);
            } else {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "保存成功,发布失败");
            }
        }
        return getObject(0);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("deleteVOs")
    @ResponseBody
    public Object deleteVOs(String ids) {
        if (AppUtil.isEmpty(ids)) {
            return getObject(1);
        } else {
            downloadService.deleteVOs(ids);
            return getObject(0);
        }
    }

    /**
     * 图片上传
     *
     * @param Filedata
     * @param siteId
     * @param columnId
     * @param contentId
     * @param imageLink
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("uploadPic")
    @ResponseBody
    public Object uploadPic(MultipartFile Filedata, Long siteId, Long columnId, Long contentId, String imageLink)
        throws UnsupportedEncodingException {
        //FileUploadUtil.deleteFileCenterEO(imageLink);
        if (null == siteId) {
            return ajaxErr("站点不能为空");
        }
        if (null == columnId) {
            return ajaxErr("栏目不能为空");
        }
        String fileName = Filedata.getOriginalFilename();
        String fileSuffix = FileUtil.getType(fileName);
        InputStream in = null;
        try {
            in = Filedata.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ColumnTypeConfigVO modelVO = ModelConfigUtil.getCongfigVO(columnId, siteId);
        WaterMarkConfigEO eo = waterMarkConfigService.getConfigBySiteId(siteId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int width = 0;
        int heigth = 0;

        if (!AppUtil.isEmpty(modelVO.getPicWidth())) {
            width = modelVO.getPicWidth();
        }
        if (!AppUtil.isEmpty(modelVO.getPicHeight())) {
            heigth = modelVO.getPicHeight();
        }

        byte[] b = null;

        if (eo != null && eo.getEnableStatus() == 1 && modelVO != null && modelVO.getIsWater() == 1) {
            ImgHander.ImgTrans(in, width, heigth, baos, fileSuffix);
            in = new ByteArrayInputStream(baos.toByteArray());
            b = WaterMarkUtil.createWaterMark(in, siteId, fileSuffix);
        } else {
            ImgHander.ImgTrans(in, width, heigth, baos, fileSuffix);
            b = baos.toByteArray();
        }

        MongoFileVO mongvo = FileUploadUtil.uploadUtil(b, fileName, FileCenterEO.Type.Image.toString(), FileCenterEO.Code.ThumbUpload.toString(), siteId, columnId,
            contentId, "文件下载缩略图", LoginPersonUtil.getRequest());
        return mongvo.getFileName();
    }

    /**
     * 文件上传
     *
     * @param Filedata
     * @param siteId
     * @param columnId
     * @param contentId
     * @param filePath
     * @return
     */
    @RequestMapping("uploadFile")
    @ResponseBody
    public Object uploadFile(MultipartFile Filedata, Long siteId, Long columnId, Long contentId, String filePath) {
        //FileUploadUtil.deleteFileCenterEO(filePath);
        MongoFileVO mongvo = FileUploadUtil.uploadUtil(Filedata, FileCenterEO.Type.NotDefined.toString(), FileCenterEO.Code.FileDownload.toString(),
            siteId, columnId, contentId, "文件下载", LoginPersonUtil.getRequest());
        return mongvo.getFileName();
    }

    /**
     * 下载文件
     *
     * @param response
     * @param downId
     * @param filePath
     */
    @RequestMapping("downloadFile")
    public void downloadFile(HttpServletResponse response, Long downId, String filePath) {
        downloadService.addCount(downId);
        mongoDbFileServer.downloadFile(response, filePath, null);
    }

}
